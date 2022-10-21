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

package com.iisky.jsender;

import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import com.iisky.jsender.cache.Cache;
import com.iisky.jsender.cache.impl.RedisCache;
import com.iisky.jsender.db.RedisDb;
import com.iisky.jsender.http.ActionMapping;
import com.iisky.jsender.http.IAction;
import com.iisky.jsender.http.server.HttpServer;
import com.iisky.jsender.utils.P;
import com.iisky.jsender.utils.Redis;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public class Application {

    public static void main(String[] args) {
        Props props = P.use("app.properties");
        Application.create()
                .redis(new Setting("config/redis.setting"))
                .db(new Setting("config/db.setting"))
                .router(IAction.class.getPackage().getName())
                .start(props.getInt("http.port"), props.getStr("http.shutDownToken"));
    }

    protected static Application create() {
        return new Application();
    }

    protected Application db(Setting setting) {
        RedisDb.create(setting);
        return this;
    }

    protected Application redis(Setting setting) {
        Redis.create(setting);
        Cache.set(new RedisCache());
        return this;
    }

    protected Application router(String packageName) {
        ActionMapping.scanAndMapping(packageName);
        return this;
    }

    protected void start(int port, String shutDownToken) {
        HttpServer.start(port, token -> shutDownToken.equals(token));
    }
}
