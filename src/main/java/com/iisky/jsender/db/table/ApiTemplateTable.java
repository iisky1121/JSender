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

import com.iisky.jsender.db.DbTable;
import com.iisky.jsender.db.DbTableBean;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public class ApiTemplateTable extends DbTable<ApiTemplateTable.Bean> {

    public final static ApiTemplateTable dao = new ApiTemplateTable();

    @Override
    public String tableName() {
        return "api_template";
    }

    @Override
    public String pk() {
        return "id";
    }

    @Override
    protected Class beanClass() {
        return Bean.class;
    }

    public boolean saveOrUpdate(Bean bean) {
        return saveOrUpdate(bean.getId(), bean);
    }

    public static class Bean extends DbTableBean {
        private String id;
        private String config;
        private String remark;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getConfig() {
            return config;
        }

        public void setConfig(String config) {
            this.config = config;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
