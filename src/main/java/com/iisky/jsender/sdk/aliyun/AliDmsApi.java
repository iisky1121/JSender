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

package com.iisky.jsender.sdk.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.iisky.jsender.utils.Resp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class AliDmsApi {
    final static String DEFAULT_REGION = "cn-hangzhou";

    final static Map<String, Map<String, String>> regionMap = new HashMap<String, Map<String, String>>() {{
        put(DEFAULT_REGION, new HashMap<String, String>() {{
            put("host", "dm.aliyuncs.com");
            put("version", "2015-11-23");
        }});
        put("ap-southeast-1", new HashMap<String, String>() {{
            put("host", "dm.ap-southeast-1.aliyuncs.com");
            put("version", "2017-06-22");
        }});
        put("ap-southeast-2", new HashMap<String, String>() {{
            put("host", "dm.ap-southeast-2.aliyuncs.com");
            put("version", "2017-06-22");
        }});
    }};

    public static Resp sendMsg(AliYunCfg cfg, String body) {
        JSONObject templateJson = JSONObject.parseObject(body);
        String regionId = templateJson.getOrDefault("RegionId", DEFAULT_REGION).toString();
        Map<String, String> regionConfigMap = regionMap.get(regionId);
        return Aliyun.request(cfg.getAccessKeyId(), cfg.getAccessSecret(), regionConfigMap.get("host"), map -> {
            map.put("Action", "SingleSendMail");
            map.put("Version", regionConfigMap.get("version"));
            map.put("RegionId", regionId);
            map.put("AddressType", "1");
            map.put("ReplyToAddress", "false");
            for (Map.Entry<String, Object> entry : templateJson.entrySet()) {
                map.put(entry.getKey(), (String) entry.getValue());
            }
        });
    }
}
