package com.bridge.download;

import com.bridge.domain.BridgeConfig;
import com.bridge.domain.Constants;
import com.bridge.enums.BridgeErrorEnum;
import com.bridge.exception.BridgeProcessFailException;
import com.bridge.log.LogHandler;
import com.bridge.utils.DateUtils;
import com.bridge.utils.PropUtils;
import com.bridge.zookeeper.data.ConfigKeyNodeData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @author Jay
 * @version v1.0
 * @description 处理配置文件的下载、加载读取、解析
 * @date 2019-11-13 13:54
 */
@Slf4j
public class DownLoadService {


    /**
     * 默认的配置项下载目录
     */
    private static final String PATH = System.getProperty("user.home").concat("/bridge-config/");

    /**
     * 换行符
     */
    private static final String NEXT_LINE = "\r\n";


    private static final String REMARK = "#";


    private static final String LINE = "-";


    private static final String PROPERTIES = ".properties";


    private static final String EQUAL = "=";

    /**
     * 配置文件快照的数量
     */
    private static final int SNAPSHOT_FILE_NUMBER = 10;

    /**
     * 正则，判断是否为整数
     */
    private static final String REGX = "^[0-9]*[1-9][0-9]*$";

    /**
     * 默认的key的版本号
     */
    private static final String DEFAULT_KEY_VERSION = "00000000000000000";


    private static final int TWO = 2;
    private static final int THREE = 3;

    /**
     * 下载配置文件到用默认的目录 /Users/home/bridge-config/
     *
     * @param data
     * @param bridgeConfig
     */
    public static synchronized void downloadProperties(List<ConfigKeyNodeData> data, BridgeConfig bridgeConfig) {
        PropUtils.checkBridgeConfigNotNull(bridgeConfig);
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        // 排序
        data.sort(Comparator.comparing(ConfigKeyNodeData::getKey));
        // 例如：test-dev
        String envPath = bridgeConfig.getAppName().concat(LINE).concat(bridgeConfig.getEnvEnum().name().toLowerCase());
        // 例如：/Users/home/bridge-config/test-dev/
        String currentPath = PATH.concat(envPath).concat(Constants.SLASH);
        // 例如：test-dev-20190101210955.properties
        String fileName = envPath.concat(LINE).concat(DateUtils.getVersion()).concat(PROPERTIES);
        // 例如：/Users/home/bridge-config/test-dev/test-dev-20190101210955.properties
        String fullPath = currentPath.concat(fileName);
        File file = new File(currentPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new BridgeProcessFailException(BridgeErrorEnum.FILE_CREATE_ERROR);
            }
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fullPath));
            data.forEach(configKeyNodeData -> {
                if (configKeyNodeData == null || StringUtils.isEmpty(configKeyNodeData.getKey())) {
                    return;
                }
                if (StringUtils.isEmpty(configKeyNodeData.getValue())) {
                    return;
                }
                String version = REMARK.concat(" ").concat(configKeyNodeData.getVersion());
                String kv = configKeyNodeData.getKey().concat(" = ").concat(configKeyNodeData.getValue());
                try {
                    bufferedWriter.write(version);
                    bufferedWriter.write(NEXT_LINE);
                    bufferedWriter.write(kv);
                    bufferedWriter.write(NEXT_LINE);
                    bufferedWriter.write(NEXT_LINE);
                } catch (Exception e) {
                    throw new BridgeProcessFailException(BridgeErrorEnum.PROPERTIES_WRITE_ERROR);
                }
            });
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new BridgeProcessFailException(BridgeErrorEnum.STREAM_CLOSE_ERROR);
        }
        // 删除最老版本的配置文件，使其快照版本保留指定的个数
        deleteOldFileProperties(bridgeConfig);
        log.info("Configuration file successfully written to local at path -> 「{}」", fullPath);
        LogHandler.info(String.format("配置项数据已成功写入本地，路径为 -> 「%s」", fullPath));
    }


    /**
     * 将配置文件读到内存中
     *
     * @param bridgeConfig {@link BridgeConfig}
     * @return {@link ConfigKeyNodeData}
     */
    public static synchronized List<ConfigKeyNodeData> loadProperties(BridgeConfig bridgeConfig) {
        // 找到最近版本的配置文件
        File file = findPropertiesFile(bridgeConfig).get(0);
        try {
            // 读文件
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String content;
            List<ConfigKeyNodeData> configKeyNodeDataList = new ArrayList<>();
            ConfigKeyNodeData configKeyNodeData = null;
            while ((content = bufferedReader.readLine()) != null) {
                if (StringUtils.isEmpty(content)) {
                    continue;
                }
                String str = content.trim();
                // 若是 "#" 则是version
                if (str.startsWith(REMARK)) {
                    configKeyNodeData = new ConfigKeyNodeData();
                    String version = parseVersion(str);
                    if (!StringUtils.isEmpty(version)) {
                        configKeyNodeData.setVersion(version);
                        continue;
                    }
                }
                // 解析 key value
                String[] kv = parseKv(str);
                if (kv != null && !StringUtils.isEmpty(kv[0])) {
                    if (configKeyNodeData == null) {
                        log.error("解析本地配置文件失败，失败的行:{}", content);
                        throw new BridgeProcessFailException(BridgeErrorEnum.PROPERTIES_WRITE_ERROR);
                    }
                    configKeyNodeData.setKey(kv[0]);
                }
                if (kv != null && !StringUtils.isEmpty(kv[1])) {
                    if (configKeyNodeData == null) {
                        log.error("解析本地配置文件失败，失败的行:{}", content);
                        throw new BridgeProcessFailException(BridgeErrorEnum.PROPERTIES_WRITE_ERROR);
                    }
                    configKeyNodeData.setValue(kv[1]);
                    configKeyNodeDataList.add(configKeyNodeData);
                }
            }
            return configKeyNodeDataList;
        } catch (Exception e) {
            log.debug("本地加载配置文件失败，现在切换为Properties模式加载。异常信息为: ", e);
        }
        return loadFromProperties(file);
    }


    /*----------------------------------------------private method------------------------------------------------*/


    /**
     * 通过Properties加载，这种加载不能解析到版本号，只能将版本号默认为00000000000000000
     *
     * @param file
     * @return
     */
    private static List<ConfigKeyNodeData> loadFromProperties(File file) {
        Properties properties = PropUtils.loadProps(file.getAbsolutePath());
        if (properties == null) {
            throw new BridgeProcessFailException(BridgeErrorEnum.LOAD_ERROR);
        }
        List<ConfigKeyNodeData> configKeyNodeDataList = new ArrayList<>();
        properties.forEach((k, v) -> {
            if (k == null || StringUtils.isEmpty(v)) {
                return;
            }
            configKeyNodeDataList.add(new ConfigKeyNodeData(k.toString(), v.toString(), DEFAULT_KEY_VERSION));
        });
        return configKeyNodeDataList;
    }

    /**
     * 删除最老版本的配置文件，使其快照版本保留指定的个数
     *
     * @param bridgeConfig
     */
    private static void deleteOldFileProperties(BridgeConfig bridgeConfig) {
        List<File> fileList = findPropertiesFile(bridgeConfig);
        int size = fileList.size();
        if (size <= SNAPSHOT_FILE_NUMBER) {
            return;
        }
        for (int i = SNAPSHOT_FILE_NUMBER; i < size; i++) {
            String filePath = fileList.get(i).getAbsolutePath();
            if (!fileList.get(i).delete()) {
                throw new BridgeProcessFailException(BridgeErrorEnum.FILE_DELETE_ERROR);
            }
            log.debug("删除旧的配置文件：{}", filePath);
        }
    }


    /**
     * 解析version
     *
     * @param content
     * @return
     */
    private static String parseVersion(String content) {
        if (StringUtils.isEmpty(content) || !content.startsWith(REMARK)) {
            return null;
        }
        char[] chas = content.toCharArray();
        StringBuilder version = new StringBuilder();
        for (int i = 0; i < chas.length; i++) {
            char chs = chas[i];
            if (chs == ' ' || chs == '#') {
                continue;
            }
            version.append(chs);
        }
        return version.toString().trim();
    }


    /**
     * 解析key和value
     *
     * @param content
     * @return
     */
    private static String[] parseKv(String content) {
        if (StringUtils.isEmpty(content) || content.startsWith(REMARK)) {
            return null;
        }
        String[] kvArray = new String[2];
        kvArray[0] = "";
        kvArray[1] = "";
        char[] chas = content.toCharArray();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean isKeyOk = false;
        for (char chs : chas) {
            if (chs == ' ') {
                continue;
            }
            if (EQUAL.equals(String.valueOf(chs)) && !isKeyOk) {
                isKeyOk = true;
                continue;
            }
            if (!isKeyOk) {
                key.append(chs);
                continue;
            }
            value.append(chs);
        }
        if (!StringUtils.isEmpty(key.toString())) {
            kvArray[0] = key.toString().trim();
        }
        if (!StringUtils.isEmpty(value.toString())) {
            kvArray[1] = value.toString().trim();
        }
        return kvArray;
    }


    /**
     * 获取配置文件并根据版本号大小排序（非文件夹、配置文件命名格式正确，例如 test-dev-20190101210955.properties）
     * 排序规则为从大到小
     *
     * @param bridgeConfig
     * @return
     */
    private static List<File> findPropertiesFile(BridgeConfig bridgeConfig) {
        PropUtils.checkBridgeConfigNotNull(bridgeConfig);
        // 例如：test-dev
        String envPath = bridgeConfig.getAppName().concat(LINE).concat(bridgeConfig.getEnvEnum().name().toLowerCase());
        // 例如：/Users/home/bridge-config/test-dev/
        String currentPath = PATH.concat(envPath).concat(Constants.SLASH);
        File file = new File(currentPath);
        if (!file.exists()) {
            throw new BridgeProcessFailException(BridgeErrorEnum.FILE_NOT_EXIST_ERROR);
        }
        // 找到非文件夹的文件
        File[] fileArray = file.listFiles(File::isFile);
        if (fileArray == null || fileArray.length == 0) {
            throw new BridgeProcessFailException(BridgeErrorEnum.FILE_NOT_EXIST_ERROR);
        }
        List<FileProperties> fileList = new ArrayList<>();
        for (File fileObj : fileArray) {
            String fileName = fileObj.getName();
            Long version = parsePropertiesFileVersion(fileName, bridgeConfig);
            if (version != null) {
                fileList.add(new FileProperties(version, fileObj));
            } else {
                log.warn("加载本地配置文件时，解析到了文件名不符合命名格式的文件，" +
                                "文件名格式应为: 系统名称-环境-版本号.properties，当前文件为:{}。若不是配置文件，请及时清理。"
                        , currentPath.concat(fileName));
            }
        }
        if (CollectionUtils.isEmpty(fileList)) {
            throw new BridgeProcessFailException(BridgeErrorEnum.FILE_NOT_EXIST_ERROR);
        }
        // 排序
        fileList.sort((o1, o2) -> {
            if (o1.version < o2.version) {
                return 1;
            }
            if (o1.version.equals(o2.version)) {
                return 0;
            }
            return -1;
        });
        List<File> files = new ArrayList<>();
        fileList.forEach(item -> files.add(item.file));
        return files;
    }


    /**
     * 解析配置文件版本号，
     * 例如
     * test-sample-dev-20190101210955.properties，返回20190101210955
     * test-dev-20190101210955.properties，返回20190101210955
     *
     * @param fileName
     * @return 返回版本号
     */
    private static Long parsePropertiesFileVersion(String fileName, BridgeConfig bridgeConfig) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        String envPath = bridgeConfig.getAppName().concat(LINE).concat(bridgeConfig.getEnvEnum().name().toLowerCase());
        if (!fileName.startsWith(envPath.concat(LINE))) {
            return null;
        }
        if (!fileName.endsWith(PROPERTIES)) {
            return null;
        }
        String[] prefix = fileName.split("\\.");
        if (prefix.length != TWO) {
            return null;
        }
        String[] nameArray = prefix[0].split(LINE);
        if (nameArray.length < THREE) {
            return null;
        }
        // 是否为正整数
        if (!nameArray[nameArray.length - 1].matches(REGX)) {
            return null;
        }
        return Long.valueOf(nameArray[nameArray.length - 1]);
    }


    @Data
    static class FileProperties {

        private Long version;

        private File file;

        public FileProperties(Long version, File file) {
            this.version = version;
            this.file = file;
        }
    }


///    public static void main(String[] args) {
//        List<ConfigKeyNodeData> configKeyNodeDataList = new ArrayList<>();
//        int j = 20;
//        for (int i = 0; i < j; i++) {
//            ConfigKeyNodeData configKeyNodeData1 = new ConfigKeyNodeData();
//            configKeyNodeData1.setVersion(DateUtils.getVersion());
//            configKeyNodeData1.setKey("key" + DateUtils.getVersion());
//            configKeyNodeData1.setValue("value" + DateUtils.getVersion());
//            configKeyNodeDataList.add(configKeyNodeData1);
//        }
//        BridgeConfig bridgeConfig = new BridgeConfig();
//        bridgeConfig.setAppCode("xxx-xxx-xxx-xxx");
//        bridgeConfig.setServerUrl("127.0.0.1:8090/bridge");
//        bridgeConfig.setEnvEnum(EnvEnum.DEV);
//        bridgeConfig.setAppName("yjx");
//        loadProperties(bridgeConfig);
//        List<ConfigKeyNodeData> list = loadProperties(bridgeConfig);
//        System.out.println(JSON.toJSON(list));
//        bridgeConfig.setAppName("yunjixie");
//        downloadProperties(list, bridgeConfig);
//    }
}
