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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson.JSONObject;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public interface ITest {

    JSONObject basicJson = new JSONObject() {{
        put("title", "离线告警");
        put("deviceCode", "cs079");
        put("deviceName", "cs079");
        put("time", DateUtil.now());
    }};

    static String getTemplateStr(Class clazz) {
        return getTemplateStr(clazz.getSimpleName() + ".json");
    }

    static String getTemplateStr(String fileName) {
        FileReader fileReader = new FileReader(fileName);
        return fileReader.readString();
    }

    public static void main(String[] args) {
        System.out.println(basicJson.toJSONString());
    }
}
