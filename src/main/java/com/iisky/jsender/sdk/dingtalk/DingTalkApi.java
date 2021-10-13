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

package com.iisky.jsender.sdk.dingtalk;

import cn.hutool.core.util.StrUtil;
import com.iisky.jsender.sdk.ApiRequest;
import com.iisky.jsender.utils.Resp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class DingTalkApi {

    private final static String send_url = "https://oapi.dingtalk.com/robot/send?access_token=";

    public static Resp sendMsg(DingTalkCfg cfg, String body) {
        String url = send_url + cfg.getAppId();
        if (StrUtil.isNotBlank(cfg.getAppSecret())) {
            long timestamp = System.currentTimeMillis();
            String sign = sign(cfg.getAppSecret(), timestamp);
            if (StrUtil.isBlank(sign)) {
                return Resp.failure("签名错误");
            }
            url += String.format("&timestamp=%s&sign=%s", timestamp, sign);
        }
        String response = ApiRequest.post(url, body);
        return render(response);
    }

    private static String sign(String secret, long timestamp) {
        String stringToSign = timestamp + "\n" + secret;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            return URLEncoder.encode(Base64.getEncoder().encodeToString(signData), "utf-8");
        } catch (Exception e) {
            return null;
        }
    }

    private static Resp render(String response) {
        return ApiRequest.jsonRender(response, json -> {
            int errorCode = json.getIntValue("errcode");
            String errorMsg = json.getString("errmsg");
            if (errorCode == 0) {
                return Resp.success(errorMsg).setResult(json);
            }
            return Resp.failure(errorMsg).setResult(json);
        });
    }
}
