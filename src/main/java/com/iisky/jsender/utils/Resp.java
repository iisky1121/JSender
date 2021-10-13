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

import com.alibaba.fastjson.JSONObject;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class Resp {
    final static String SUCCESS_CODE = "200";
    final static String SUCCESS_MSG = "操作成功";
    final static String FAILURE_CODE = "400";
    final static String FAILURE_MSG = "操作失败";

    private String code;
    private String msg;
    private String errorCode;
    private Object result;
    private String cause;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Resp setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Resp setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public Object getResult() {
        return result;
    }

    public Resp setResult(Object result) {
        this.result = result;
        return this;
    }

    public String getCause() {
        return cause;
    }

    public Resp setCause(String cause) {
        this.cause = cause;
        return this;
    }

    Resp(String code, String msg, String errorCode, Object result, String cause) {
        this.code = code;
        this.msg = msg;
        this.errorCode = errorCode;
        this.result = result;
        this.cause = cause;
    }

    /**
     * 成功
     */
    public static Resp success() {
        return success(SUCCESS_MSG);
    }

    public static Resp success(String msg) {
        return success(msg, null);
    }

    public static Resp success(Object result) {
        return success(SUCCESS_MSG, result);
    }

    public static Resp success(String msg, Object result) {
        return new Resp(SUCCESS_CODE, msg, null, result, null);
    }

    /**
     * 失败
     */
    public static Resp failure() {
        return failure(FAILURE_MSG);
    }

    public static Resp failure(String msg) {
        return failure(msg, null);
    }

    public static Resp failure(String msg, Object result) {
        return failure(msg, null, result);
    }

    public static Resp failure(String msg, String error_code, Object result) {
        return new Resp(FAILURE_CODE, msg, error_code, result, null);
    }

    /**
     * 是否成功
     */
    public boolean succeed() {
        return getCode() != null && getCode().equals(SUCCESS_CODE);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
