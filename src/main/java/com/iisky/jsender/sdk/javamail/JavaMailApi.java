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

package com.iisky.jsender.sdk.javamail;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.iisky.jsender.utils.Resp;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class JavaMailApi {

    private static Session getSession(JavaMailCfg cfg) {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", StrUtil.isBlank(cfg.getProtocol()) ? "smtp" : cfg.getProtocol());
        props.setProperty("mail.smtp.host", cfg.getHost());
        props.setProperty("mail.smtp.auth", "true");
        if (cfg.getPort() != null) {
            props.setProperty("mail.smtp.port", cfg.getPort().toString());
        }
        return Session.getDefaultInstance(props);
    }

    public static Resp sendMsg(JavaMailCfg cfg, String body) {
        Transport transport = null;
        try {
            Session session = getSession(cfg);
            JavaEmailBean bean = JSONObject.parseObject(body).toJavaObject(JavaEmailBean.class);
            MimeMessage message = createMessage(session, cfg, bean);
            transport = session.getTransport();
            transport.connect(cfg.getAccount(), cfg.getPassword());
            transport.sendMessage(message, message.getAllRecipients());
            return Resp.success();
        } catch (Exception e) {
            return Resp.failure("系统异常").setCause(e.getMessage());
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                }
            }
        }
    }

    static MimeMessage createMessage(Session session, JavaMailCfg cfg, JavaEmailBean bean) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(cfg.getAccount()));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(bean.getRecipient()));
        message.setSubject(bean.getSubject());
        message.setContent(bean.getContent(), "text/html;charset=UTF-8");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    static class JavaEmailBean {
        private String recipient;
        private String subject;
        private String content;

        public String getRecipient() {
            return recipient;
        }

        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
