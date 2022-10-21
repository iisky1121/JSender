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

package com.iisky.jsender.sdk.weixin.qy;

import cn.hutool.core.util.StrUtil;
import com.iisky.jsender.cache.Cache;
import com.iisky.jsender.sdk.ApiRequest;
import com.iisky.jsender.sdk.weixin.WeiXin;
import com.iisky.jsender.utils.Resp;

import java.util.function.Supplier;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public class WxQyApi {

    private static WxQyAccessToken getAccessToken(String corpId, String corpSecret) {
        String token = Cache.get(WxQyAccessToken.class, corpSecret);
        if (StrUtil.isNotBlank(token)) {
            WxQyAccessToken result = new WxQyAccessToken(token);
            if (result != null && result.isAvailable()) {
                return result;
            }
        }
        return refreshAccessToken(corpId, corpSecret);
    }

    private final static String token_url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    synchronized static WxQyAccessToken refreshAccessToken(String corpId, String corpSecret) {
        // 获取token地址
        String url = token_url + String.format("?corpid=%s&corpsecret=%s", corpId, corpSecret);
        String response = ApiRequest.get(url);
        Cache.put(WxQyAccessToken.class, corpSecret, response, 7200);
        return new WxQyAccessToken(response);
    }

    private final static String msg_url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";

    public static Resp sendMsg(WxQyCfg cfg, String body) {
        return render(cfg, () -> {
            String url = msg_url + getAccessTokenStr(cfg);
            return ApiRequest.post(url, body);
        });
    }

    private static String getAccessTokenStr(WxQyCfg cfg) {
        return StrUtil.isBlank(cfg.getAccessToken()) ? getAccessToken(cfg.getCorpId(), cfg.getCorpSecret()).getAccessToken() : cfg.getAccessToken();
    }

    private static Resp render(WxQyCfg cfg, Supplier<String> supplier) {
        return ApiRequest.retry(1, () -> WeiXin.render(supplier.get()), errorCode -> {
            if (WeiXin.isAccessTokenInvalid(errorCode)) {
                refreshAccessToken(cfg.getCorpId(), cfg.getCorpSecret());
            }
        });
    }
}
