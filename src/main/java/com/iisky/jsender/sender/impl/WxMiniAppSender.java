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

package com.iisky.jsender.sender.impl;

import com.iisky.jsender.sdk.weixin.mp.WxMpApi;
import com.iisky.jsender.sdk.weixin.mp.WxMpCfg;
import com.iisky.jsender.sender.ISender;
import com.iisky.jsender.utils.Resp;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public class WxMiniAppSender implements ISender<WxMpCfg> {

    @Override
    public Resp send(WxMpCfg cfg, String body) {
        return WxMpApi.sendMsg(cfg, body);
    }

}
