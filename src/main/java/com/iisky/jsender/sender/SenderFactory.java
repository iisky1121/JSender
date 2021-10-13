/*
 * Copyright (c) 2021 JSender Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iisky.jsender.sender;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ClassUtil;
import com.iisky.jsender.sdk.IApiCfg;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
class SenderFactory {

    private final static Map<String, ISender> senderMapping = new ConcurrentHashMap<>();
    private final static Map<String, Class<?>> cfgMapping = new ConcurrentHashMap<>();

    static {
        Set<Class<?>> classSet = ClassUtil.scanPackageBySuper(ISender.class.getPackage().getName(), ISender.class);
        for (Class<?> clazz : classSet) {
            if (Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }
            senderMapping.put(getSenderName(clazz), Singleton.get((Class<ISender>) clazz));
        }
    }

    static Map<String, ISender> getSenderMapping() {
        return senderMapping;
    }

    static ISender getSender(String senderName) {
        return getSenderMapping().get(senderName);
    }

    static ISender getSender(Class<? extends ISender> clazz) {
        return getSender(getSenderName(clazz));
    }

    static String getSenderName(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    static String getSenderName(ISender sender) {
        return getSenderName(sender.getClass());
    }

    static <T extends IApiCfg> Class<T> getSenderCfgClass(ISender<T> sender) {
        Class<?> senderClass = sender.getClass();
        synchronized (senderClass) {
            String keyName = getSenderName(senderClass);
            Class<T> cfgClass = (Class<T>) cfgMapping.get(keyName);
            if (cfgClass != null) {
                return cfgClass;
            }
            Type t = senderClass.getGenericSuperclass();
            if (t == null) {
                t = senderClass.getGenericInterfaces()[0];
            }
            Type[] params = ((ParameterizedType) t).getActualTypeArguments();
            cfgClass = (Class<T>) params[0];
            cfgMapping.put(keyName, cfgClass);
            return cfgClass;
        }
    }
}
