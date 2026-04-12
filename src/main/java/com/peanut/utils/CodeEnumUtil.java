package com.peanut.utils;

import com.peanut.enumPackage.CodeDescEnum;

/**
 * @author: peanut
 * @date: 2026/3/30
 * @version:1.0
 */
public class CodeEnumUtil {
    public static <E extends Enum<E>> E codeOf(Class<E> enumClass, String code) {
        for (E e : enumClass.getEnumConstants()) {
            if (e instanceof CodeDescEnum) {
                if (((CodeDescEnum) e).getCode().equals(code)) {
                    return e;
                }
            }
        }
        return null;
    }
}