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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @author iisky1121
 * @date 2021-10-11
 */
public interface IDbTable {

    String tableName();

    String pk();

    static <T extends DbTableBean> T toBean(JSONObject argJson, Class<T> clazz) {
        if (argJson == null || clazz == null) {
            return null;
        }
        T t = argJson.toJavaObject(clazz);
        t.keys(argJson.keySet());
        return t;
    }

    static <T extends DbTableBean> Entity toEntry(T t, String tableName) {
        if (t == null) {
            return null;
        }
        Map<String, Object> map = BeanUtil.beanToMap(t);
        Entity entity = Entity.create(tableName);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null || (t.keys() != null && t.keys().contains(entry.getKey()))) {
                entity.set(entry.getKey(), entry.getValue());
            }
        }
        return entity;
    }
}
