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

package com.iisky.jsender.http.server;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONObject;
import com.iisky.jsender.http.ActionMapping;
import com.iisky.jsender.http.IAction;
import com.iisky.jsender.utils.Resp;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import org.slf4j.MDC;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
class HttpBuilder {

    public static FullHttpResponse request(FullHttpRequest request) {
        String uri = request.uri();
        if ("/health".equals(uri)) {
            return defaultSuccessResponse();
        }
        if (uri.startsWith("/shutdown?token=")) {
            JSONObject argJson = getUrlArgJson(uri);
            boolean b = HttpServer.close(argJson.getString("token"));
            if (b) {
                return render("Shutting down, bye...");
            }
            return defaultErrorResponse();
        }
        if (request.method() != HttpMethod.POST) {
            return defaultErrorResponse();
        }
        String requestId = System.currentTimeMillis() + "" + RandomUtil.randomNumbers(8);
        return render(requestId, action(requestId, request));
    }

    private final static String RequestId = "RequestId";

    private static Resp action(String requestId, FullHttpRequest request) {
        String uri = request.uri();
        long start = System.currentTimeMillis();
        JSONObject input = null;
        Resp output = null;
        try {
            MDC.put(RequestId, requestId);
            input = getArgJson(request);
            IAction action = ActionMapping.get(uri);
            if (action == null) {
                output = Resp.failure(String.format("接口:%s不存在", uri));
            } else {
                output = action.action(input);
            }
            return output;
        } catch (Exception e) {
            StaticLog.error(e);
            output = Resp.failure("系统异常").setCause(e.getMessage());
            return output;
        } finally {
            StringBuilder builder = new StringBuilder();
            builder.append("\n");
            builder.append("接口地址: ");
            builder.append(uri);
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
            MDC.remove(RequestId);
        }
    }

    private static JSONObject getArgJson(FullHttpRequest request) {
        try {
            String content = request.content().toString(CharsetUtil.UTF_8);
            if (StringUtil.isNullOrEmpty(content)) {
                return new JSONObject();
            }
            return JSONObject.parseObject(content);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private static JSONObject getUrlArgJson(String uri) {
        JSONObject json = new JSONObject();
        String url = uri.replace("?", ";");
        if (!url.contains(";")) {
            return json;
        }
        String args[] = url.split(";");
        if (args.length < 1) {
            return json;
        }
        String[] arr = args[1].split("&");
        String tempArgs[];
        for (String s : arr) {
            tempArgs = s.split("=");
            if (tempArgs.length < 2) {
                continue;
            }
            json.put(tempArgs[0], tempArgs[1]);
        }
        return json;
    }

    private static FullHttpResponse defaultErrorResponse() {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
    }

    private static FullHttpResponse defaultSuccessResponse() {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
    }

    private static FullHttpResponse render(String requestId, Resp resp) {
        return render(new JSONObject() {{
            put("requestId", requestId);
            put("code", resp.getCode());
            put("msg", resp.getMsg());
            if (null != resp.getResult()) {
                put("result", resp.getResult());
            }
            if (null != resp.getErrorCode()) {
                put("errorCode", resp.getErrorCode());
            }
            if (null != resp.getCause()) {
                put("cause", resp.getCause());
            }
        }}.toJSONString());
    }

    private static FullHttpResponse render(String body) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(body.getBytes()));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        return response;
    }

}
