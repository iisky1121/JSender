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

package com.iisky.jsender.db;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;
import com.alibaba.fastjson.JSONObject;
import com.iisky.jsender.utils.Redis;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class RedisDb {

    private static Db client;

    public synchronized static Db create(Setting setting) {
        if (setting == null) {
            client = Db.use(DSFactory.get());
        } else {
            DSFactory dsFactory = DSFactory.create(setting);
            client = Db.use(dsFactory.getDataSource());
        }
        return client;
    }

    public static Db get() {
        if (client == null) {
            throw new RuntimeException("db client not config");
        }
        return client;
    }

    public static int insert(Entity entity) throws SQLException {
        return get().insert(entity);
    }

    public static int update(String pk, Entity entity) throws SQLException {
        Object idValue = entity.get(pk);
        entity.remove(pk);
        int i = get().update(entity, Entity.create(entity.getTableName()).set(pk, idValue));
        delFromRedis(entity.getTableName(), idValue);
        return i;
    }

    public static int deleteById(String tableName, String pk, Object idValue) throws SQLException {
        int i = get().del(tableName, pk, idValue);
        delFromRedis(tableName, idValue);
        return i;
    }

    public static Entity findById(String tableName, String pk, Object idValue) {
        return getSetRedis(tableName, idValue, () -> {
            try {
                List<Entity> list = get().findBy(tableName, pk, idValue);
                if (list == null || list.isEmpty()) {
                    return null;
                }
                return list.get(0);
            } catch (SQLException e) {
                return null;
            }
        });
    }

    private static void delFromRedis(String tableName, Object idValue) {
        String key = getRedisKey(tableName, idValue);
        if (StrUtil.isNotBlank(key)) {
            Redis.del(key);
        }
    }

    private static Entity getSetRedis(String tableName, Object idValue, Supplier<Entity> supplier) {
        String key = getRedisKey(tableName, idValue);
        if (StrUtil.isNotBlank(key)) {
            String cache = Redis.getSetEx(key, DEFAULT_TIME, () -> {
                Entity entity = supplier.get();
                if (entity == null) {
                    return null;
                }
                return JSONObject.toJSONString(entity);
            }, DEFAULT_EMPTY_TIME);
            return toEntity(tableName, cache);
        }
        return supplier.get();
    }

    private static Entity toEntity(String tableName, String str) {
        if (StrUtil.isBlank(str)) {
            return null;
        }
        Entity entity = Entity.create(tableName);
        entity.putAll(JSONObject.parseObject(str));
        return entity;
    }

    private final static int DEFAULT_TIME = 24 * 60 * 60;
    private final static int DEFAULT_EMPTY_TIME = 60;

    private static String getRedisKey(String tableName, Object idValue) {
        if (Redis.get() != null) {
            return tableName + ":" + idValue;
        }
        return null;
    }
}
