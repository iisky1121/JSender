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

package com.iisky.jsender.utils;

import cn.hutool.setting.dialect.Props;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public class P {
    final static String DEFAULT_ENCODING = "utf-8";
    private static Props props = null;

    private P() {
    }

    public synchronized static Props use(String fileName) {
        return use(fileName, DEFAULT_ENCODING);
    }

    public synchronized static Props use(String fileName, String encoding) {
        if (props == null) {
            props = Props.getProp(fileName, encoding);
        }
        return props;
    }

    public static Props getProps() {
        if (props == null) {
            throw new IllegalArgumentException("props can not be null");
        }
        return props;
    }
}
