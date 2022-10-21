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

package com.iisky.jsender.sender.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.iisky.jsender.db.table.ApiTemplateTable;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public class ApiTemplateService {

    public static JSONObject getTemplateJson(String templateId) {
        if (StrUtil.isBlank(templateId)) {
            return null;
        }
        ApiTemplateTable.Bean bean = ApiTemplateTable.dao.findById(templateId);
        if (bean == null) {
            return null;
        }
        String template = bean.getConfig();
        if (StrUtil.isBlank(template)) {
            return null;
        }
        return JSONObject.parseObject(template);
    }
}
