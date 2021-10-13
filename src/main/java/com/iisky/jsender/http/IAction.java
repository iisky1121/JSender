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
import com.alibaba.fastjson.JSONObject;
import com.iisky.jsender.db.DbTableBean;
import com.iisky.jsender.utils.Resp;

import java.util.function.Supplier;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public interface IAction {
    Resp action(JSONObject argJson);

    static Resp emptyCheck(JSONObject argJson, String[] attrs, Supplier<Resp> supplier) {
        Supplier<String> check = () -> {
            for (String attr : attrs) {
                Object object = argJson.get(attr);
                if (object == null || (object instanceof String && StrUtil.isBlank((String) object))) {
                    return attr;
                }
            }
            return null;
        };
        String emptyAttr = check.get();
        if (StrUtil.isNotBlank(emptyAttr)) {
            return Resp.failure(String.format("参数[%s]不能为空值", emptyAttr));
        }
        return supplier.get();
    }

    static Resp render(DbTableBean bean) {
        if (bean == null) {
            return Resp.failure("数据不存在");
        }
        return Resp.success(bean);
    }

    static Resp render(boolean result) {
        return result ? Resp.success() : Resp.failure();
    }
}
