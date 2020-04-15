package com.bridge.processor;

import com.bridge.log.LogHandler;
import com.bridge.processor.init.BridgeEnvironment;
import com.bridge.annotation.BridgeValue;
import com.bridge.annotation.BridgeValueChangedListener;
import com.bridge.domain.BeanDefinition;
import com.bridge.domain.BridgeConfig;
import com.bridge.enums.BridgeErrorEnum;
import com.bridge.enums.NeedUpdateEnum;
import com.bridge.exception.BridgeProcessFailException;
import com.bridge.listener.PropertiesChangeListener;
import com.bridge.listener.PropertiesChangeListenerHolder;
import com.bridge.processor.init.cache.container.LocalCacheHolder;
import com.bridge.processor.init.cache.container.BeanDefinitionListenerContainer;
import com.bridge.utils.PropUtils;
import com.bridge.utils.TagUtils;
import com.bridge.zookeeper.BridgeNodeManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.bridge.domain.Constants.LOAD_FORMAT;
import static com.bridge.domain.Constants.PRINT_LOG_FORMAT;

/**
 * @author Jay
 * @version v1.0
 * @description 在bean的生命周期内重载对象实例属性
 * @date 2018-12-26 14:52
 *
 * <p>
 * 1.实现{@link InitializingBean#afterPropertiesSet}接口可以用于初始化相关属性操作
 * 2.实现{@link Aware}类的子类接口{@link BeanNameAware#setBeanName}和{@link BeanFactoryAware#setBeanFactory}可以获得spring暴露出来的beanName和BeanFactory
 * 3.实现{@link DisposableBean#destroy}接口可以去做一下资源关闭的动作
 * 4.继承{@link InstantiationAwareBeanPostProcessorAdapter#postProcessAfterInstantiation#postProcessPropertyValues}抽象类可以重新做属性的注入操作
 * </p>
 *
 * <p>
 * spring相关的调用过程：
 *
 * <1>容器启动时：
 * 1.1最先调用
 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory
 *
 * <2>getBean时：
 * 实例化之后调用：
 * @see org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessPropertyValues
 *
 * <3>初始化时：
 * 属性注入（setter）
 * @see org.springframework.beans.factory.BeanNameAware#setBeanName
 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * init-method属性
 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * destroy-method属性
 * @see org.springframework.beans.factory.DisposableBean#destroy
 *
 * </p>
 */
@Slf4j
public class BridgeConfigBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements InitializingBean, DisposableBean, BeanNameAware, BeanFactoryAware, ApplicationContextAware {

    public static final String BEAN_NAME = "bridgeConfigBeanPostProcessor";

    public BridgeConfig bridgeConfig;

    private String beanName;

    private BeanFactory beanFactory;

    private static boolean canRegister = BridgeEnvironment.CAN_REGISTER_ZK_NODE;


    /**
     * 在设置了属性值等之后需要做的事情,此处做初始化,[3]
     */
    @Override
    public void afterPropertiesSet() {
        this.bridgeConfig = PropUtils.buildBridgeConfig(beanFactory);
    }


    /**
     * {@link BridgeConfigBeanPostProcessor#postProcessPropertyValues}方法对属性值进行修改(这个时候属性值还未被设置，但是我们可以修改原本该设置进去的属性值)。
     * 如果postProcessAfterInstantiation方法返回false，该方法可能不会被调用。可以在该方法内对属性值进行修改[5]
     *
     * @param pvs
     * @param pds
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
            throws BeansException {
        if (beanName.equals(this.beanName)) {
            return super.postProcessPropertyValues(pvs, pds, bean, beanName);
        }
        PropertyValue[] propertyValues = pvs.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues) {
            if (propertyValue.getValue() instanceof TypedStringValue) {
                String propertyName = propertyValue.getName();
                String typeStringValue = ((TypedStringValue) propertyValue.getValue()).getValue();
                if (TagUtils.isBridgeXmlTag(typeStringValue)) {
                    String xmlTagKey = TagUtils.bridgeKeyParse(typeStringValue);
                    if (StringUtils.isEmpty(xmlTagKey)) {
                        throw new BridgeProcessFailException(BridgeErrorEnum.XML_PARES_ERROR);
                    }
                    boolean isRefresh = TagUtils.isBridgeXmlTagKeyRefresh(typeStringValue);
                    // 从缓存中拉取key对应的value
                    String xmlValue = LocalCacheHolder.getValue(xmlTagKey);
                    if (xmlValue == null) {
                        throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR.getCode(),
                                "配置项: " + xmlTagKey + " 的值为空，请检查是否配置");
                    }
                    // 获取property对应的属性类型
                    Class propertyTypeClazz = String.class;
                    for (PropertyDescriptor propertyDescriptor : pds) {
                        if (propertyName.equals(propertyDescriptor.getName())) {
                            propertyTypeClazz = propertyDescriptor.getPropertyType();
                        }
                    }
                    // 对需要修改的属性做类型转换
                    Object value = PropUtils.convertValue(propertyTypeClazz, xmlValue);
                    // 设置属性
                    propertyValue.setConvertedValue(value);
                    // 构造BeanDefinition
                    BeanDefinition beanDefinition = new BeanDefinition(beanName, propertyName, xmlTagKey, isRefresh);
                    // 设置监听,这是为了防止重复的订阅
                    boolean isNeedPut = BeanDefinitionListenerContainer.putToListenerBeanContainer(xmlTagKey, beanDefinition);
                    if (isNeedPut) {
                        log.info(PRINT_LOG_FORMAT, beanName, propertyName, xmlTagKey, value);
                        // 将machine节点注册到key节点下,状态标识为不需要更新
                        if (canRegister) {
                            // 发送日志至控制台
                            LogHandler.info(String.format(LOAD_FORMAT, beanName, propertyName, xmlTagKey, value));
                            BridgeNodeManager.getInstance().registerMachineNode(xmlTagKey, LocalCacheHolder.get(xmlTagKey)
                                    , NeedUpdateEnum.NOT_NEED_UPDATE.getKey(), bridgeConfig.getEnvEnum());
                        }
                    }
                }
            }
        }
        return super.postProcessPropertyValues(pvs, pds, bean, beanName);
    }

    /**
     * {@link BridgeConfigBeanPostProcessor#postProcessAfterInstantiation} 方法在目标对象实例化之后调用，这个时候对象已经被实例化，但是该实例的属性还未被设置，都是null。
     * 因为它的返回值是决定要不要调用postProcessPropertyValues方法的其中一个因素(因为还有一个因素是mbd.getDependencyCheck());
     * 如果该方法返回false,并且不需要check，那么postProcessPropertyValues就会被忽略不执行；
     * 如果返回true，{@link BridgeConfigBeanPostProcessor#postProcessPropertyValues}就会被执行[4]
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        // 通过反射获取所有,除自己外的bean的信息
        if (beanName.equals(this.beanName)) {
            return super.postProcessAfterInstantiation(bean, beanName);
        }
        ReflectionUtils.doWithFields(bean.getClass(), (field) -> {
            if (field.isAnnotationPresent(BridgeValue.class)) {
                // 静态属性无法注入
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new BridgeProcessFailException(BridgeErrorEnum.STATIC_INJECT_ERROR);
                }
                BridgeValue bridgeValue = field.getAnnotation(BridgeValue.class);
                String key = TagUtils.keyParse(bridgeValue.value());
                boolean autoRefreshed = bridgeValue.autoRefreshed();
                String propertyName = field.getName();
                String value = LocalCacheHolder.getValue(key);
                if (value == null) {
                    throw new BridgeProcessFailException(BridgeErrorEnum.BNS_CHK_ERROR.getCode(),
                            "配置项: " + key + " 的值为空，请检查是否配置");
                }
                BeanDefinition beanDefinition = new BeanDefinition(beanName, propertyName, key, autoRefreshed);
                boolean isNeedPut = BeanDefinitionListenerContainer.putToListenerBeanContainer(key, beanDefinition);
                if (isNeedPut) {
                    try {
                        Object objectConvert = PropUtils.convertValue(field.getType(), value);
                        field.setAccessible(true);
                        field.set(bean, objectConvert);
                        log.info(PRINT_LOG_FORMAT, beanName, propertyName, key, value);
                        // 将machine节点注册到key节点下,状态标识为不需要更新
                        if (canRegister) {
                            // 发送日志至控制台
                            LogHandler.info(String.format(LOAD_FORMAT, beanName, propertyName, key, value));
                            BridgeNodeManager.getInstance().registerMachineNode(key, LocalCacheHolder.get(key)
                                    , NeedUpdateEnum.NOT_NEED_UPDATE.getKey(), bridgeConfig.getEnvEnum());
                        }
                    } catch (Exception e) {
                        throw new BridgeProcessFailException(BridgeErrorEnum.UNKNOWN_ERROR.getCode(), e.getMessage());
                    }
                }
            }
//            // 注册spring原生注解
///            if (field.isAnnotationPresent(Value.class)) {
//                Value springValue = field.getAnnotation(Value.class);
//                String key = TagUtils.keyParse(springValue.value());
//                String propertyName = field.getName();
//                String value = LocalCacheHolder.getValue(key);
//                // 这里设置配置项自动刷新为true，有业务需要的可以调整为false，那么就不会刷新了
//                BeanDefinition beanDefinition = new BeanDefinition(beanName, propertyName, key, true);
//                boolean isNeedPut = BeanDefinitionListenerContainer.putToListenerBeanContainer(key, beanDefinition);
//                if (isNeedPut) {
//                    // 写入值
//                    RefreshBeanField.refreshByReflect(field, beanDefinition, value, bean);
//                    // 将machine节点注册到key节点下,状态标识为不需要更新
//                    BridgeNodeManager.getInstance().registerMachineNode(key, LocalCacheHolder.get(key)
//                            , NeedUpdateEnum.NOT_NEED_UPDATE.getKey(), bridgeConfig.getEnvEnum());
//                }
//            }
        });
        return super.postProcessAfterInstantiation(bean, beanName);
    }

    /**
     * 声明bean的时候,spring会自动注入BeanFactory,可以通过BeanFactory的getBean方法获取实例对象 [1]
     *
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        RefreshBeanField.setBeanFactory(beanFactory);
    }

    /**
     * 获取applicationContext,读取监听的注解
     *
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] beanNameArray = applicationContext.getBeanNamesForAnnotation(BridgeValueChangedListener.class);
        if (beanNameArray != null && beanNameArray.length > 0) {
            for (String beanName : beanNameArray) {
                Object bean = applicationContext.getBean(beanName);
                if (!(bean instanceof PropertiesChangeListener)) {
                    continue;
                }
                PropertiesChangeListener beanObj = (PropertiesChangeListener) bean;
                // 获取注解的key
                BridgeValueChangedListener bridgeValue = bean.getClass().getAnnotation(BridgeValueChangedListener.class);
                String[] keyArray = bridgeValue.key();
                List<String> configKeyList = new ArrayList<>();
                if (keyArray.length > 0 && !StringUtils.isEmpty(keyArray[0])) {
                    for (String configKey : keyArray) {
                        if (!StringUtils.isEmpty(configKey)) {
                            configKeyList.add(TagUtils.keyParse(configKey));
                        }
                    }
                    // 去重后放入容器
                    Set<String> configKeySet = new HashSet<>(configKeyList);
                    PropertiesChangeListenerHolder.addPropertiesChangeListener(configKeySet, beanObj);
                } else {
                    // 监听所有的key
                    PropertiesChangeListenerHolder.addAllKeyListener(beanObj);
                }
            }
        }
    }

    /**
     * 实现了BeanNameAware接口需要实现该方法，取到beanName,[2]
     *
     * @param name
     */
    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }


    /**
     * bean销毁时，需要清空本地缓存，释放相关资源
     */
    @Override
    public void destroy() {
        BridgeEnvironment.destroy();
    }
}
