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

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
class WxQyAccessToken implements Serializable {

    private static final long serialVersionUID = -822464425433824314L;

    private String access_token;    // 正确获取到 access_token 时有值
    private Long expires_in;        // 正确获取到 access_token 时有值
    private String error_code;        // 出错时有值
    private String error_msg;            // 出错时有值

    private Long expired_time;        // 正确获取到 access_token 时有值，存放过期时间
    private String json;

    public WxQyAccessToken(String jsonStr) {
        this.json = jsonStr;

        try {
            JSONObject temp = JSONObject.parseObject(jsonStr);
            access_token = temp.getString("access_token");
            expires_in = temp.getLong("expires_in");
            error_code = temp.getString("errcode");
            error_msg = temp.getString("errmsg");

            if (expires_in != null) {
                expired_time = System.currentTimeMillis() + ((expires_in - 60 * 60 * 24 * 7) * 1000);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getJson() {
        return json;
    }

    public boolean isAvailable() {
        if (expired_time == null) {
            return false;
        }
        if (!"0".equals(error_code)) {
            return false;
        }
        if (expired_time < System.currentTimeMillis()) {
            return false;
        }
        return access_token != null;
    }

    public String getAccessToken() {
        return access_token;
    }

    public Long getExpiresIn() {
        return expires_in;
    }

    public String getErrorCode() {
        return error_code;
    }

    public String getErrorMsg() {
        return error_msg;
    }
}
