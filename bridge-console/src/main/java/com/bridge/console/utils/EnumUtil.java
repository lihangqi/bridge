package com.bridge.console.utils;

import java.util.*;

/**
 * @description:Enum enum工具类
 * @author: Jay
 * @date: 2017/12/14 09:48
 * @version: V1.0
 */
public class EnumUtil {

    private EnumUtil() {

    }

    /**
     * 根据key找到对应的Enum值
     *
     * @param key enum 的key
     * @param clz 对应的enum类型
     * @param <T> 泛型
     * @return 返回enum
     */
    public static <T extends Enum<T> & KeyedNamed> T getEnum(int key, Class<T> clz) {
        T[] values = clz.getEnumConstants();
        for (T value : values) {
            if (value.getKey() == key) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据key找到对应的name值，,多用于根据状态值找名字
     *
     * @param key key
     * @param clz enum类
     * @param <T> 泛型
     * @return 名字
     */
    public static <T extends Enum<T> & KeyedNamed> String getName(int key, Class<T> clz) {
        T t = getEnum(key, clz);
        if (t != null) {
            return (String) t.getName();
        }
        return null;
    }

    /**
     * 获取key值列表
     *
     * @param clz enum类
     * @param <T> 泛型
     * @return 所有的KEY
     */
    public static <T extends Enum<T> & KeyedNamed> List<Integer> getKeys(Class<T> clz) {
        List<Integer> keys = new LinkedList<>();
        T[] values = clz.getEnumConstants();
        for (T value : values) {
            keys.add(value.getKey());
        }
        return keys;
    }

    /**
     * 获取name值列表
     *
     * @param clz enum类
     * @param <T>
     * @return
     */
    public static <T extends Enum<T> & KeyedNamed> List<String> getNames(Class<T> clz) {
        List<String> keys = new LinkedList<>();
        T[] values = clz.getEnumConstants();
        for (T value : values) {
            keys.add(value.getName());
        }
        return keys;
    }

    /**
     * 获取enum列表
     *
     * @param clz enum类
     * @param <T>
     * @return enum集合
     */
    public static <T extends Enum<T> & KeyedNamed> List<T> getEnumList(Class<T> clz) {
        return new ArrayList<>(Arrays.asList(clz.getEnumConstants()));
    }

    /**
     * @param key     要判断的值
     * @param enumObj 要比较的枚举
     * @param <T>
     * @return 如果key等于enumObj的key值则返回true
     */
    public static <T extends Enum<T> & KeyedNamed> boolean keyEquals(Integer key, T enumObj) {
        return key != null && key == enumObj.getKey();
    }


    /**
     * @param key   要判断的值
     * @param enums 要判断的枚举
     * @return 如果key等于e1的key或者在enums的key值列表里则返回true
     */
    public static <T extends Enum<T> & KeyedNamed> boolean keyIn(Integer key, T... enums) {
        if (key == null) {
            return false;
        }
        for (T enumObj : enums) {
            if (key == enumObj.getKey()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param key   要判断的值
     * @param enums 要判断的枚举
     * @param <T>
     * @return 如果key在enums的key值列表里则返回true
     */
    public static <T extends Enum<T> & KeyedNamed> boolean keyIn(Integer key, Collection<T> enums) {
        if (key == null) {
            return false;
        }
        for (T enumObj : enums) {
            if (key == enumObj.getKey()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回name列表
     *
     * @param enums 枚举集合
     * @param <T>
     * @return 返回name列表
     */
    public static <T extends Enum<T> & KeyedNamed> List<String> getNames(Collection<T> enums) {
        List<String> list = new ArrayList<>();
        for (T enumObj : enums) {
            list.add(enumObj.getName());
        }
        return list;
    }

    /**
     * 返回key列表
     *
     * @param enums
     * @param <T>
     * @return 返回key列表
     */
    public static <T extends Enum<T> & KeyedNamed> List<Integer> getKeys(T... enums) {
        List<Integer> list = new ArrayList<>();
        for (T enumObj : enums) {
            list.add(enumObj.getKey());
        }
        return list;
    }

    /**
     * 返回key列表
     *
     * @param enums 枚举集合
     * @param <T>
     * @return 返回key列表
     */
    public static <T extends Enum<T> & KeyedNamed> List<Integer> getKeys(Collection<T> enums) {
        List<Integer> list = new ArrayList<>();
        for (T enumObj : enums) {
            list.add(enumObj.getKey());
        }
        return list;
    }
}
