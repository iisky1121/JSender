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

package com.iisky.jsender.cache.impl;

import com.iisky.jsender.cache.ICache;
import com.iisky.jsender.utils.Redis;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public class RedisCache implements ICache {
    @Override
    public String get(String key) {
        return Redis.get(key);
    }

    @Override
    public void put(String key, int seconds, String value) {
        Redis.setEx(key, seconds, value);
    }

    @Override
    public void remove(String key) {
        Redis.del(key);
    }
}
