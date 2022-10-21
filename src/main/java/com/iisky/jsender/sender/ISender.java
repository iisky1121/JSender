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

import com.iisky.jsender.sdk.IApiCfg;
import com.iisky.jsender.sender.service.ApiCfgService;
import com.iisky.jsender.utils.Resp;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public interface ISender<T extends IApiCfg> {
    Resp send(T cfg, String body);

    default T cfg(String appId) {
        return ApiCfgService.getApiConfig(appId, SenderFactory.getSenderCfgClass(this));
    }
}
