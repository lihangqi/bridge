package com.bridge.console.utils;

import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseErrorEnum;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description bean的copy工具类
 * @date 2019-01-21 14:08
 */
public class BeanUtil {

    /**
     * 对象属性值复制，这个是浅复制
     *
     * @param source 有值对象，即源
     * @param target 没有值得对象，即目标对象
     */
    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }


    /**
     * 集合复制，传入源list集合 和目标实体类型
     *
     * @param sourceList
     * @param targetType
     * @param <D>        源集合数据类型
     * @param <T>        目标集合数据类型
     * @return 转换后的集合
     */
    public static <D, T> List<T> copyList(List<D> sourceList, Class<T> targetType) {
        List<T> list = new ArrayList<>(sourceList.size());
        for (D d : sourceList) {
            T t;
            try {
                t = targetType.newInstance();
            } catch (Exception e) {
                throw new BusinessCheckFailException(BaseErrorEnum.UNKNOWN_ERROR.getCode(), e.getMessage());
            }
            copyProperties(d, t);
            list.add(t);
        }
        return list;
    }

}
