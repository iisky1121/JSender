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

package com.iisky.jsender.http.action;

import com.alibaba.fastjson.JSONObject;
import com.iisky.jsender.http.IAction;
import com.iisky.jsender.http.Router;
import com.iisky.jsender.sender.JSender;
import com.iisky.jsender.utils.Resp;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
@Router("/send")
public class Send implements IAction {

    @Override
    public Resp action(JSONObject argJson) {
        return JSender.send(argJson);
    }
}
