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

package com.iisky.jsender.sender;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iisky.jsender.sdk.IApiCfg;
import com.iisky.jsender.sender.template.ITemplateSender;
import com.iisky.jsender.sender.template.impl.TemplateSender;
import com.iisky.jsender.utils.Resp;
import com.iisky.jsender.utils.TemplateUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class JSender {

    private final static String DATA_KEY = "data";
    private final static String LEVEL_KEY = "level";

    public static class Template {
        private static ITemplateSender templateSender = new TemplateSender();

        public static Resp send(String templateId, JSONObject argJson) {
            JSONObject templateJson = templateSender.template(templateId);
            return send(argJson, templateJson);
        }

        public static Resp send(JSONObject argJson, JSONObject templateJson) {
            if (templateJson == null || templateJson.isEmpty()) {
                return Resp.failure("模板为空");
            }
            JSONObject basicData = argJson.getJSONObject(DATA_KEY);
            int level = argJson.getIntValue(LEVEL_KEY);
            return JSender.send(basicData, senderName -> {
                if (!templateJson.containsKey(senderName)) {
                    return null;
                }
                SenderBean templateBean = templateJson.getJSONObject(senderName).toJavaObject(SenderBean.class);
                //template level大于传入level才触发该渠道的推送，template level不配置默认推送
                if (templateBean.getLevel() != null && templateBean.getLevel() > level) {
                    return null;
                }
                if (argJson.containsKey(senderName)) {
                    SenderBean requestBean = argJson.getJSONObject(senderName).toJavaObject(SenderBean.class);
                    if (StrUtil.isNotBlank(requestBean.getAppId())) {
                        templateBean.setAppId(requestBean.getAppId());
                    }
                    templateBean.setCfg(requestBean.getCfg());
                    templateBean.setData(requestBean.getData());
                }
                return templateBean;
            });
        }
    }

    public static Resp send(JSONObject argJson) {
        JSONObject basicData = argJson.getJSONObject(DATA_KEY);
        return send(basicData, senderName -> {
            if (!argJson.containsKey(senderName)) {
                return null;
            }
            return argJson.getJSONObject(senderName).toJavaObject(SenderBean.class);
        });
    }

    public static <T extends IApiCfg, M extends ISender<T>> Resp send(Class<M> clazz, T cfg, JSONObject data, String template) {
        ISender<T> sender = SenderFactory.getSender(clazz);
        return send(sender, cfg, data, template);
    }

    public static <T extends IApiCfg, M extends ISender<T>> Resp send(Class<M> clazz, String appId, JSONObject data, String template) {
        ISender sender = SenderFactory.getSender(clazz);
        IApiCfg cfg = getSenderCfg(sender, appId, null);
        return send(sender, cfg, data, template);
    }

    private static Resp send(JSONObject baseData, Function<String, SenderBean> function) {
        Map<String, ISender> senderMapping = SenderFactory.getSenderMapping();
        JSenderTo jSenderTo = JSenderTo.create();
        for (Map.Entry<String, ISender> entry : senderMapping.entrySet()) {
            String senderName = entry.getKey();
            SenderBean requestBean = function.apply(senderName);
            if (requestBean == null) {
                continue;
            }
            if (StrUtil.isBlank(requestBean.getAppId()) && (requestBean.getCfg() == null || requestBean.getCfg().isEmpty())) {
                return Resp.failure(senderName + "配置参数appId和cfg最少需要一个");
            }
            IApiCfg cfg = getSenderCfg(entry.getValue(), requestBean.getAppId(), requestBean.getCfg());
            if (cfg == null) {
                return Resp.failure(senderName + "配置不存在");
            }

            Resp resp = addSenderTo(jSenderTo, entry.getValue(), cfg, baseData, requestBean.getData(), requestBean.getTemplate());
            if (!resp.succeed()) {
                StaticLog.warn(resp.getMsg());
            }
        }
        return jSenderTo.send();
    }

    private static Resp send(ISender sender, IApiCfg cfg, JSONObject data, String template) {
        JSenderTo jSenderTo = JSenderTo.create();
        Resp resp = addSenderTo(jSenderTo, sender, cfg, null, data, template);
        if (!resp.succeed()) {
            return resp;
        }
        return jSenderTo.send();
    }

    private static Resp addSenderTo(JSenderTo jSenderTo, ISender sender, IApiCfg cfg, JSONObject basicData, JSONObject data, String template) {
        if (StrUtil.isBlank(template)) {
            return Resp.failure("模板为空");
        }

        Set<String> keys = TemplateUtil.getTemplateParams(template);
        JSONObject json = getTemplateJson(keys, cfg, basicData, data);
        if (json.isEmpty()) {
            jSenderTo.add(sender, cfg, template);
            return Resp.success();
        }

        String arrayKey = null;
        String senderName = SenderFactory.getSenderName(sender);
        for (String key : keys) {
            if (!json.containsKey(key)) {
                return Resp.failure(senderName + "缺少参数:" + key);
            }

            Object object = json.get(key);
            if (Collection.class.isAssignableFrom(object.getClass())) {
                if (arrayKey == null) {
                    arrayKey = key;
                } else {
                    return Resp.failure(senderName + "只能传入一个数组参数");
                }
            }
        }

        if (arrayKey == null) {
            String body = TemplateUtil.render(template, json);
            jSenderTo.add(sender, cfg, body);
            return Resp.success();
        }
        //批量发送参数传入
        for (Object o : ((Collection) json.get(arrayKey)).toArray()) {
            json.put(arrayKey, o);
            String body = TemplateUtil.render(template, json);
            jSenderTo.add(sender, cfg, body);
        }
        return Resp.success();
    }

    /**
     * 根据template变量重新构建一个json，解决多个数组参数冲突的问题
     *
     * @param keys
     * @param cfg
     * @param basicData
     * @param data
     * @return
     */
    private static JSONObject getTemplateJson(Set<String> keys, IApiCfg cfg, JSONObject basicData, JSONObject data) {
        JSONObject json = new JSONObject();
        if (keys.isEmpty()) {
            return json;
        }
        for (String key : keys) {
            if (data != null && data.get(key) != null) {
                json.put(key, data.get(key));
                continue;
            }
            if (basicData != null && basicData.get(key) != null) {
                json.put(key, basicData.get(key));
            }
        }
        JSONObject resultJson = new JSONObject();
        resultJson.putAll(formatData(null, json));
        //模板文件可以通过参数获取值，例如:${cfg.appId}
        JSONObject cfgJson = (JSONObject) JSON.toJSON(cfg);
        for (Map.Entry<String, Object> entry : cfgJson.entrySet()) {
            if (entry.getValue() != null) {
                resultJson.put("cfg." + entry.getKey(), entry.getValue());
            }
        }
        return resultJson;
    }

    /**
     * 转换argJson
     * 格式化data参数和各个sender data参数值
     * json{key1}转换成 json.key1 的方式
     *
     * @param data
     */
    private static Map<String, Object> formatData(String prefix, Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        prefix = StrUtil.isBlank(prefix) ? "" : (prefix + ".");
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                if (Map.class.isAssignableFrom(value.getClass())) {
                    Map<String, Object> valueMap = (Map<String, Object>) value;
                    map.putAll(formatData(prefix + entry.getKey(), valueMap));
                    continue;
                }
                map.put(prefix + entry.getKey(), value);
            }
        }
        return map;
    }

    private static IApiCfg getSenderCfg(ISender sender, String appId, JSONObject cfgJson) {
        Supplier<IApiCfg> supplier = () -> {
            if (cfgJson == null || cfgJson.isEmpty()) {
                return sender.cfg(appId);
            }
            Class<? extends IApiCfg> clazz = SenderFactory.getSenderCfgClass(sender);
            return cfgJson.toJavaObject(clazz);
        };
        IApiCfg cfg = supplier.get();
        if (cfg != null && StrUtil.isBlank(cfg.getAppId())) {
            cfg.setAppId(appId);
        }
        return cfg;
    }

    static class SenderBean {
        private String appId;
        private Integer level;
        private Object template;
        private JSONObject cfg;
        private JSONObject data;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public String getTemplate() {
            if (template == null) {
                return null;
            }
            if (template instanceof String) {
                return (String) template;
            }
            if (template instanceof JSONObject) {
                return ((JSONObject) template).toJSONString();
            }
            return template.toString();
        }

        public void setTemplate(Object template) {
            this.template = template;
        }

        public JSONObject getCfg() {
            return cfg;
        }

        public void setCfg(JSONObject cfg) {
            this.cfg = cfg;
        }

        public JSONObject getData() {
            return data;
        }

        public void setData(JSONObject data) {
            this.data = data;
        }
    }
}
