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

package com.iisky.jsender.db;

import cn.hutool.db.Entity;
import cn.hutool.db.PageResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
public abstract class DbTable<T extends DbTableBean> implements IDbTable {

    protected abstract Class<T> beanClass();

    private Entity toEntity(T bean) {
        return IDbTable.toEntry(bean, tableName());
    }

    public boolean save(T bean) {
        try {
            if (!beforeSave(bean)) {
                return false;
            }
            return RedisDb.insert(toEntity(bean)) > 0;
        } catch (Exception e) {
            throw new DbException(e.getMessage());
        }
    }

    public boolean update(T bean) {
        try {
            if (!beforeUpdate(bean)) {
                return false;
            }
            return RedisDb.update(pk(), toEntity(bean)) > 0;
        } catch (Exception e) {
            throw new DbException(e.getMessage());
        }
    }

    public boolean deleteById(Object idValue) {
        if (idValue == null) {
            return false;
        }
        try {
            return RedisDb.deleteById(tableName(), pk(), idValue) > 0;
        } catch (Exception e) {
            throw new DbException(e.getMessage());
        }
    }

    public T findById(Object idValue) {
        if (idValue == null) {
            return null;
        }
        try {
            Entity entity = RedisDb.findById(tableName(), pk(), idValue);
            if (entity == null) {
                return null;
            }
            return entity.toBean(beanClass());
        } catch (Exception e) {
            throw new DbException(e.getMessage());
        }
    }

    public PageResult<T> page(int pageNumber, int pageSize, Entity where) {
        try {
            where.setTableName(tableName());
            PageResult<Entity> page = RedisDb.get().page(where, pageNumber, pageSize);
            if (page == null) {
                return new PageResult<T>();
            }
            return new PageResult<T>(page.getPage(), page.getPageSize(), page.getTotal()) {{
                for (Entity entity : page) {
                    add(entity.toBean(beanClass()));
                }
            }};
        } catch (Exception e) {
            throw new DbException(e.getMessage());
        }
    }

    public List<T> list(Entity where) {
        try {
            where.setTableName(tableName());
            List<T> list = RedisDb.get().find(where,beanClass());
            if (list == null) {
                return new ArrayList<>();
            }
            return list;
        } catch (Exception e) {
            throw new DbException(e.getMessage());
        }
    }

    protected boolean saveOrUpdate(Object idValue, T bean) {
        T t = findById(idValue);
        if (t == null) {
            return save(bean);
        }
        return update(bean);
    }

    protected boolean beforeSave(T bean) {
        return true;
    }

    protected boolean beforeUpdate(T bean) {
        return true;
    }
}
