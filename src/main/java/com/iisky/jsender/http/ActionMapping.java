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

package com.iisky.jsender.http;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public class ActionMapping {

    private static final Map<String, IAction> actionMapping = new HashMap<>();

    public static <T extends IAction> void add(String actionKey, T action) {
        if (StrUtil.isBlank(actionKey)) {
            throw new IllegalArgumentException("actionKey can not be blank.");
        }
        if (action == null) {
            throw new IllegalArgumentException("action can not be null.");
        }
        if (actionMapping.containsKey(actionKey)) {
            throw new ActionException(String.format("actionKey [%s] is already in use.", actionKey));
        }
        actionMapping.put(actionKey, action);
    }

    public static IAction get(String actionKey) {
        return actionMapping.get(actionKey);
    }

    public static Map<String, IAction> mapping() {
        return actionMapping;
    }

    public static <T extends IAction> void add(String actionKey, Class<T> actionClass) {
        T t = Singleton.get(actionClass);
        add(actionKey, t);
    }

    public synchronized static void scanAndMapping(String packageName) {
        Set<Class<?>> classSet = ClassUtil.scanPackageBySuper(packageName, IAction.class);
        for (Class<?> actionClass : classSet) {
            Router router = actionClass.getAnnotation(Router.class);
            if (router != null) {
                add(router.value(), (Class<IAction>) actionClass);
            }
        }
    }
}
