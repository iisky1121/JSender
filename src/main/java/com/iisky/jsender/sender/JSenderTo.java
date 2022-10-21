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

import cn.hutool.core.util.StrUtil;
import com.iisky.jsender.sdk.IApiCfg;
import com.iisky.jsender.utils.Resp;
import com.iisky.jsender.utils.ThreadPool;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
class JSenderTo {

    private List<To> list = new ArrayList<>();

    public static JSenderTo create() {
        return new JSenderTo();
    }

    public JSenderTo add(ISender sender, IApiCfg cfg, String body) {
        if (sender == null || cfg == null || StrUtil.isBlank(body)) {
            return this;
        }
        list.add(new To(sender, cfg, body));
        return this;
    }

    public Resp send() {
        if (list.isEmpty()) {
            return Resp.failure();
        }
        if (list.size() == 1) {
            return send(list.get(0));
        }
        return send(list);
    }

    private static Resp send(List<To> list) {
        try {
            ThreadPool.execute(list, to -> send(to));
            return Resp.success();
        } catch (Exception e) {
            return Resp.failure().setCause(e.getMessage());
        }
    }

    private static Resp send(To to) {
        Resp resp = to.getSender().send(to.getCfg(), to.getBody());
        //StaticLog.info("[{}] [{}]，内容：{}", to.getSender().getClass().getSimpleName(), resp.succeed() ? "成功" : "失败", to.getBody());
        return resp;
    }

    static class To<T extends IApiCfg> {
        private ISender<T> sender;
        private T cfg;
        private String body;

        public ISender<T> getSender() {
            return sender;
        }

        public void setSender(ISender<T> sender) {
            this.sender = sender;
        }

        public T getCfg() {
            return cfg;
        }

        public void setCfg(T cfg) {
            this.cfg = cfg;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public To(ISender<T> sender, T cfg, String body) {
            this.sender = sender;
            this.cfg = cfg;
            this.body = body;
        }
    }
}
