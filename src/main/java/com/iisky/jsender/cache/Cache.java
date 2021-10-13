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

package com.iisky.jsender.cache;

import com.iisky.jsender.cache.impl.MemoryCache;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class Cache {
    private volatile static ICache DEFAULT_CACHE = new MemoryCache();

    public static void set(ICache cache) {
        DEFAULT_CACHE = cache;
    }

    public static ICache get() {
        return DEFAULT_CACHE;
    }

    public static String getCacheKey(Class<?> clazz, String key) {
        return clazz.getName().concat("_").concat(key);
    }

    public static String get(Class<?> clazz, String key) {
        return get().get(getCacheKey(clazz, key));
    }

    public static void put(Class<?> clazz, String key, String value, int seconds) {
        get().put(getCacheKey(clazz, key), seconds, value);
    }

    public static void remove(Class<?> clazz, String key) {
        get().remove(getCacheKey(clazz, key));
    }
}
