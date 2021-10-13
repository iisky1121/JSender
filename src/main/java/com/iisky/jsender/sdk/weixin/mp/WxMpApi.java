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

package com.iisky.jsender.sdk.weixin.mp;

import cn.hutool.core.util.StrUtil;
import com.iisky.jsender.cache.Cache;
import com.iisky.jsender.sdk.ApiRequest;
import com.iisky.jsender.sdk.weixin.WeiXin;
import com.iisky.jsender.utils.Resp;

import java.util.function.Supplier;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class WxMpApi {

    private static WxMpAccessToken getAccessToken(String corpId, String corpSecret) {
        String token = Cache.get(WxMpAccessToken.class, corpSecret);
        if (StrUtil.isNotBlank(token)) {
            WxMpAccessToken result = new WxMpAccessToken(token);
            if (result != null && result.isAvailable()) {
                return result;
            }
        }
        return refreshAccessToken(corpId, corpSecret);
    }

    private final static String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    synchronized static WxMpAccessToken refreshAccessToken(String appId, String appSecret) {
        // 获取token地址
        String url = token_url + String.format("&appid=%s&secret=%s", appId, appSecret);
        String response = ApiRequest.get(url);
        Cache.put(WxMpAccessToken.class, appId, response, 7200);
        return new WxMpAccessToken(response);
    }

    private static String msg_url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";

    public static Resp sendMsg(WxMpCfg cfg, String body) {
        return render(cfg, () -> {
            String url = msg_url + getAccessTokenStr(cfg);
            return ApiRequest.post(url, body);
        });
    }

    private static String mini_app_msg_url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/uniform_send?access_token=";

    public static Resp sendMiniAppMsg(WxMpCfg cfg, String body) {
        return render(cfg, () -> {
            String url = mini_app_msg_url + getAccessTokenStr(cfg);
            return ApiRequest.post(url, body);
        });
    }

    private static String getAccessTokenStr(WxMpCfg cfg) {
        return StrUtil.isBlank(cfg.getAccessToken()) ? getAccessToken(cfg.getAppId(), cfg.getAppSecret()).getAccessToken() : cfg.getAccessToken();
    }

    private static Resp render(WxMpCfg cfg, Supplier<String> supplier) {
        return ApiRequest.retry(1, () -> WeiXin.render(supplier.get()), errorCode -> {
            if (WeiXin.isAccessTokenInvalid(errorCode)) {
                refreshAccessToken(cfg.getAppId(), cfg.getAppSecret());
            }
        });
    }
}
