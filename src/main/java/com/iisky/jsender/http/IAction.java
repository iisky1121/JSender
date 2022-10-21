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

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.PageResult;
import com.alibaba.fastjson.JSONObject;
import com.iisky.jsender.db.DbTableBean;
import com.iisky.jsender.utils.Resp;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public interface IAction {
    Resp action(JSONObject argJson);

    static Resp page(JSONObject argJson, BiFunction<Integer, Integer, PageResult<?>> function) {
        int pageNumber = argJson.getIntValue("pageNumber");
        int pageSize = argJson.getIntValue("pageSize");
        PageResult<?> page = function.apply(pageNumber - 1, pageSize);
        return Resp.success(new JSONObject() {{
            put("totalRow", page.getTotal());
            put("pageNumber", page.getPage() + 1);
            put("lastPage", page.isLast());
            put("firstPage", page.isFirst());
            put("totalPage", page.getTotalPage());
            put("pageSize", page.getPageSize());
            put("list", page);
        }});
    }

    static Resp emptyCheck(JSONObject argJson, String[] attrs, Supplier<Resp> supplier) {
        Supplier<Resp> check = () -> {
            for (String attr : attrs) {
                if (attr.contains("|")) {
                    String args[] = attr.split("[|]");
                    if (isBlank(argJson, args)) {
                        return Resp.failure(String.format("Attribute [%s] can not be blank.", StrUtil.join(" or ", args)));
                    }
                } else if (isBlank(argJson, attr)) {
                    return Resp.failure(String.format("Attribute [%s] can not be blank.", attr));
                }
            }
            return Resp.success();
        };
        Resp resp = check.get();
        if (!resp.succeed()) {
            return resp;
        }
        return supplier.get();
    }

    static boolean isBlank(Map<String, ?> map, String attr) {
        Object value = map.get(attr);
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return StrUtil.isBlank((String) value);
        }
        return false;
    }

    static boolean isBlank(Map<String, ?> map, String[] attrs) {
        for (String arg : attrs) {
            if (!isBlank(map, arg)) {
                return false;
            }
        }
        return true;
    }

    static Resp render(DbTableBean bean) {
        if (bean == null) {
            return Resp.failure("Data does not exist");
        }
        return Resp.success(bean);
    }

    static Resp render(boolean result) {
        return result ? Resp.success() : Resp.failure();
    }
}
