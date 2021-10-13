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

package com.iisky.jsender.sender.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.iisky.jsender.db.table.ApiConfigTable;
import com.iisky.jsender.sdk.ApiRequest;
import com.iisky.jsender.sdk.IApiCfg;
import com.iisky.jsender.utils.TemplateUtil;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class ApiCfgService {

    public static <T extends IApiCfg> T getApiConfig(String appId, Class<T> clazz) {
        if (StrUtil.isBlank(appId)) {
            return null;
        }
        ApiConfigTable.Bean bean = ApiConfigTable.dao.findById(clazz.getSimpleName(), appId);
        if (bean == null) {
            return null;
        }
        if (StrUtil.isNotBlank(bean.getConfig())) {
            return toCfg(appId, clazz, bean.getConfig());
        }
        return requestToCfg(appId, clazz, bean.getHttpConfig());
    }

    private static <T extends IApiCfg> T requestToCfg(String appId, Class<T> clazz, String body) {
        if (StrUtil.isBlank(body)) {
            return null;
        }
        HttpRequestBean bean = JSONObject.parseObject(body).toJavaObject(HttpRequestBean.class);
        if (StrUtil.isAllBlank(bean.getUrl(), bean.getRequest(), bean.getResponse())) {
            return null;
        }
        String requestBody = TemplateUtil.render(bean.getRequest(), new JSONObject() {{
            put("appId", appId);
        }});
        if (StrUtil.isBlank(requestBody)) {
            return null;
        }
        String responseBody = ApiRequest.post(bean.getUrl(), requestBody);
        if (StrUtil.isBlank(responseBody)) {
            return null;
        }
        JSONObject responseJson = JSONObject.parseObject(responseBody);
        String cfgBody = TemplateUtil.render(bean.getResponse(), responseJson);
        return toCfg(appId, clazz, cfgBody);
    }

    private static <T extends IApiCfg> T toCfg(String appId, Class<T> clazz, String body) {
        if (StrUtil.isBlank(body)) {
            return null;
        }
        T t = JSONObject.parseObject(body).toJavaObject(clazz);
        if (StrUtil.isBlank(t.getAppId())) {
            t.setAppId(appId);
        }
        return t;
    }

    private static String getObjectStr(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            return (String) object;
        }
        if (object instanceof JSONObject) {
            return ((JSONObject) object).toJSONString();
        }
        return object.toString();
    }

    static class HttpRequestBean {
        private String url;
        private Object request;
        private Object response;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getRequest() {
            return getObjectStr(request);
        }

        public void setRequest(Object request) {
            this.request = request;
        }

        public String getResponse() {
            return getObjectStr(response);
        }

        public void setResponse(Object response) {
            this.response = response;
        }
    }
}
