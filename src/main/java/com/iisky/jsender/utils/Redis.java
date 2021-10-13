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

package com.iisky.jsender.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import cn.hutool.setting.Setting;

import java.util.function.Supplier;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class Redis {
    private static RedisDS client;

    public synchronized static RedisDS create(Setting setting) {
        if (setting == null) {
            client = RedisDS.create();
        } else {
            client = RedisDS.create(setting, null);
        }
        return client;
    }

    public static RedisDS get() {
        if (client == null) {
            throw new RuntimeException("redis client not config");
        }
        return client;
    }

    public static String get(String key) {
        String value = client.getStr(key);
        if (StrUtil.isBlank(value) || "null".equals(value)) {
            return null;
        }
        return value;
    }

    public static Long del(String key) {
        return client.del(key);
    }

    public static String setEx(String key, int seconds, String value) {
        return client.getJedis().setex(key, seconds, value);
    }

    public static String getSetEx(String key, int seconds, Supplier<String> supplier) {
        return getSetEx(key, seconds, supplier, 0);
    }

    public static String getSetEx(String key, int seconds, Supplier<String> supplier, int emptySeconds) {
        String lockKey = key.intern();
        synchronized (lockKey) {
            String value = get(key);
            if (StrUtil.isNotBlank(value)) {
                return value;
            }
            value = supplier.get();
            if (StrUtil.isNotBlank(value)) {
                setEx(key, seconds, value);
            } else if (emptySeconds > 0) {
                setEx(key, emptySeconds, "");
            }
            return value;
        }
    }
}
