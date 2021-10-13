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

package com.iisky.jsender.db.table;

import cn.hutool.core.util.StrUtil;
import com.iisky.jsender.db.DbTable;
import com.iisky.jsender.db.DbTableBean;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class ApiConfigTable extends DbTable<ApiConfigTable.Bean> {

    public final static ApiConfigTable dao = new ApiConfigTable();

    @Override
    public String tableName() {
        return "api_config";
    }

    @Override
    public String pk() {
        return "id";
    }

    @Override
    protected Class<Bean> beanClass() {
        return Bean.class;
    }

    @Override
    protected boolean beforeSave(Bean bean) {
        if (StrUtil.isBlank(bean.getId())) {
            bean.setId(getIdValue(bean.getCfgName(), bean.getAppId()));
        }
        return super.beforeSave(bean);
    }

    public boolean saveOrUpdate(Bean bean) {
        if (StrUtil.isBlank(bean.getId()) && (StrUtil.isBlank(bean.getCfgName()) || StrUtil.isBlank(bean.getAppId()))) {
            return false;
        }
        if (StrUtil.isBlank(bean.getId())) {
            bean.setId(getIdValue(bean.getCfgName(), bean.getAppId()));
        }
        if (StrUtil.isBlank(bean.getCfgName()) || StrUtil.isBlank(bean.getAppId())) {
            String args[] = bean.getId().split("-");
            bean.setCfgName(args[0]);
            bean.setAppId(args[1]);
        }
        return saveOrUpdate(bean.getId(), bean);
    }

    public Bean findById(String cfgName, String appId) {
        return findById(getIdValue(cfgName, appId));
    }

    private static String getIdValue(String cfgName, String appId) {
        return cfgName + "-" + appId;
    }

    public static class Bean extends DbTableBean {
        private String id;
        private String appId;
        private String cfgName;
        private String config;
        private String httpConfig;
        private String remark;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getCfgName() {
            return cfgName;
        }

        public void setCfgName(String cfgName) {
            this.cfgName = cfgName;
        }

        public String getConfig() {
            return config;
        }

        public void setConfig(String config) {
            this.config = config;
        }

        public String getHttpConfig() {
            return httpConfig;
        }

        public void setHttpConfig(String httpConfig) {
            this.httpConfig = httpConfig;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
