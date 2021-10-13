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

package com.iisky.jsender.sdk.weixin;

import com.iisky.jsender.sdk.ApiRequest;
import com.iisky.jsender.utils.Resp;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class WeiXin {

    public static Resp render(String response) {
        return ApiRequest.jsonRender(response, json -> {
            int errorCode = json.getIntValue("errcode");
            String errorMsg = json.getString("errmsg");
            if (errorCode == 0) {
                return Resp.success(errorMsg).setResult(json);
            }
            return Resp.failure(errorMsg).setErrorCode(String.valueOf(errorCode)).setResult(json);
        });
    }

    public static boolean isAccessTokenInvalid(String errorCode) {
        if (errorCode == null) {
            return false;
        }
        Integer ec = Integer.valueOf(errorCode);
        return ec != null && (ec == 40001 || ec == 42001 || ec == 42002 || ec == 40014);
    }
}
