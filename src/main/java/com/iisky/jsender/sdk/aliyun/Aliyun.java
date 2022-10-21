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

package com.iisky.jsender.sdk.aliyun;

import cn.hutool.core.util.StrUtil;
import com.iisky.jsender.sdk.ApiRequest;
import com.iisky.jsender.utils.Resp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author iisky1121
 * @date 2021-09-01
 */
class Aliyun {

    public static String getSingQueryString(String accessKeyId, String accessSecret, Consumer<Map<String, String>> consumer) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));// 这里一定要设置GMT时区
        Map<String, String> paras = new HashMap<>();
        // 1. 系统参数
        paras.put("SignatureMethod", "HMAC-SHA1");
        paras.put("SignatureNonce", UUID.randomUUID().toString());
        paras.put("AccessKeyId", accessKeyId);
        paras.put("SignatureVersion", "1.0");
        paras.put("Timestamp", df.format(new Date()));
        paras.put("Format", "json");
        // 2. 业务API参数
        consumer.accept(paras);
        // 3. 去除签名关键字Key
        if (paras.containsKey("Signature"))
            paras.remove("Signature");
        // 4. 参数KEY排序
        TreeMap<String, String> sortParas = new TreeMap<>();
        sortParas.putAll(paras);
        // 5. 构造待签名的字符串
        Iterator<String> it = sortParas.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(paras.get(key)));
        }
        String sortedQueryString = sortQueryStringTmp.substring(1);// 去除第一个多余的&符号
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append("GET").append("&");
        stringToSign.append(specialUrlEncode("/")).append("&");
        stringToSign.append(specialUrlEncode(sortedQueryString));
        String sign = sign(accessSecret + "&", stringToSign.toString());
        // 6. 签名最后也要做特殊URL编码
        String signature = specialUrlEncode(sign);
        // 最终打印出合法GET请求的URL
        return "Signature=" + signature + sortQueryStringTmp;
    }

    private static String specialUrlEncode(String value) throws Exception {
        return URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    private static String sign(String accessSecret, String stringToSign) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(accessSecret.getBytes("UTF-8"), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(signData);
    }

    public static Resp request(String accessKeyId, String accessSecret, String host, Consumer<Map<String, String>> consumer) {
        try {
            String fullUrl = String.format("http://%s/?%s", host, getSingQueryString(accessKeyId, accessSecret, consumer));
            String response = ApiRequest.get(fullUrl);
            return render(response);
        } catch (Exception e) {
            return Resp.failure("接口请求失败").setCause(e.getMessage());
        }
    }

    public static Resp render(String response) {
        return ApiRequest.render(response, json -> {
            String code = json.getString("Code");
            String errorMsg = json.getString("Message");
            if ("OK".equalsIgnoreCase(code)) {
                return Resp.success(errorMsg).setResult(json);
            }
            if (StrUtil.isNotBlank(json.getString("EnvId"))) {
                return Resp.success("OK").setResult(json);
            }
            return Resp.failure(errorMsg).setResult(json);
        });
    }
}
