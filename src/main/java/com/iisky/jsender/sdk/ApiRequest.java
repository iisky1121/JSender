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

package com.iisky.jsender.sdk;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONObject;
import com.iisky.jsender.utils.Resp;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class ApiRequest {

    protected static int timeout = 5000;

    public static String get(String url) {
        return request(url, null, () -> HttpRequest.get(url).timeout(timeout).execute().body());
    }

    public static String post(String url, String body) {
        return post(url, null, body);
    }

    public static String post(String url, Map<String, String> headers, String body) {
        return request(url, body, () -> HttpRequest.post(url).headerMap(headers, true).timeout(timeout).body(body).execute().body());
    }

    private static String request(String url, String input, Supplier<String> supplier) {
        long start = System.currentTimeMillis();
        String output = null;
        try {
            if (StrUtil.isBlank(input) && url.contains("?")) {
                int index = url.indexOf("?");
                url = url.substring(0, index);
                input = url.substring(index);
            }
            output = supplier.get();
        } catch (Exception e) {
            StaticLog.error(e);
        } finally {
            StringBuilder builder = new StringBuilder();
            builder.append("\n");
            builder.append("接口地址: ");
            builder.append(url);
            builder.append("\n");
            builder.append("接口耗时: ");
            builder.append(System.currentTimeMillis() - start);
            builder.append("ms");
            builder.append("\n");
            builder.append("接口参数: ");
            builder.append(input);
            builder.append("\n");
            builder.append("返回结果: ");
            builder.append(output);
            StaticLog.info(builder.toString());
        }
        return output;
    }

    public static Resp retry(int retryCount, Supplier<Resp> retry, Consumer<String> redo) {
        Resp v = null;
        for (int i = 0; i < retryCount; i++) {
            try {
                v = retry.get();
            } catch (Exception e) {
            }
            if (v.succeed()) {
                break;
            }
            redo.accept(v.getErrorCode());
        }
        return v;
    }

    public static Resp jsonRender(String response, Function<JSONObject, Resp> function) {
        try {
            JSONObject json = JSONObject.parseObject(response);
            if (json == null || json.isEmpty()) {
                return Resp.failure();
            }
            return function.apply(json);
        } catch (Exception e) {
            return Resp.failure().setCause(response);
        }
    }
}
