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
import com.iisky.jsender.sdk.weixin.mp.WxMpCfg;
import com.iisky.jsender.sender.JSender;
import com.iisky.jsender.sender.impl.WxMiniAppSender;

/**
 * 信小程序 - 模板消息推送到公众号
 *
 * @author iisky1121
 * @date 2021-09-01
 */
public class WxMiniAppTest {
    public static void main(String[] args) {
        JSender.send(WxMiniAppSender.class, new WxMpCfg() {{
            setAppId("");
            setAppSecret("");
        }}, new JSONObject() {{
            putAll(ITest.basicJson);
            //小程序的openId
            put("openId", "oxvMQ5QjnVe4_ngiTf-03Ffkf6UM");
        }}, ITest.getTemplateStr(WxMiniAppTest.class));
    }
}
