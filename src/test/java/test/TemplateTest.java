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

package test;

import com.alibaba.fastjson.JSONObject;
import com.iisky.jsender.sender.JSender;
import com.iisky.jsender.sender.impl.WxMpSender;
import com.iisky.jsender.sender.impl.WxQySender;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class TemplateTest {
    public static void main(String[] args) {
        JSONObject templateJson = JSONObject.parseObject(ITest.getTemplateStr("template.json"));
        JSender.Template.send(new JSONObject() {{
            put("data", ITest.basicJson);
            put(WxQySender.class.getSimpleName(), new JSONObject() {{
                //当模板里面没有配置wxQy.appId参数时，可以指定appId
                put("appId", "wxe448173ee6c6efxx");
                put("data", new JSONObject() {{
                    put("userId", "MengHuanPaoYing");
                }});
            }});
            put(WxMpSender.class.getSimpleName(), new JSONObject() {{
                //当模板里面没有配置wxMp.appId参数时，可以指定appId
                put("data", new JSONObject() {{
                    put("openId", "oKofB1Pq1BT-Ze9oS_oMYPZ9WzQA");
                }});
            }});
        }}, templateJson);
    }
}
