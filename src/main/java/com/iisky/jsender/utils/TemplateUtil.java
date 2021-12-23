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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class TemplateUtil {
    final static Pattern regex = Pattern.compile("\\$\\{([^}]*)\\}");

    public static String render(String template, Map<String, Object> paras) {
        return render(template, paras, false);
    }

    public static String render(String template, Map<String, Object> paras, boolean autoCheck) {
        if (autoCheck && !checkTemplateKeys(template, paras.keySet())) {
            //如果模板中的参数未传入值，直接返回null
            return null;
        }
        if (paras != null) {
            for (Map.Entry<String, Object> entry : paras.entrySet()) {
                template = template.replaceAll("\\$\\{" + entry.getKey() + "\\}", entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }
        return template;
    }

    public static Set<String> getTemplateParams(String template) {
        Set<String> set = new HashSet<>();
        Matcher matcher = regex.matcher(template);
        while (matcher.find()) {
            set.add(matcher.group(1));
        }
        return set;
    }

    public static boolean checkTemplateKeys(String template, Set<String> keys) {
        Set<String> set = getTemplateParams(template);
        set.removeAll(keys);
        return set.size() == 0;
    }
}
