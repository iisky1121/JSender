SET FOREIGN_KEY_CHECKS=0;

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

-- ----------------------------
-- Table structure for api_config
-- ----------------------------
CREATE TABLE `api_config` (
  `id` varchar(32) NOT NULL,
  `appId` varchar(16) DEFAULT NULL,
  `cfgName` varchar(16) DEFAULT NULL,
  `config` text,
  `httpConfig` text,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of api_config
-- ----------------------------
INSERT INTO `jsender`.`api_config` (`id`, `appId`, `cfgName`, `config`, `httpConfig`, `remark`) VALUES ('AliYunCfg-demo', 'demo', 'AliYunCfg', '{\"accessKeyId\":\"\",\"accessSecret\":\"\"}', NULL, NULL);
INSERT INTO `jsender`.`api_config` (`id`, `appId`, `cfgName`, `config`, `httpConfig`, `remark`) VALUES ('DingTalkCfg-demo', 'demo', 'DingTalkCfg', '{\"appId\":\"\",\"appSecret\":\"\"}', NULL, NULL);
INSERT INTO `jsender`.`api_config` (`id`, `appId`, `cfgName`, `config`, `httpConfig`, `remark`) VALUES ('JavaMailCfg-demo', 'demo', 'JavaMailCfg', '{\"account\":\"\",\"password\":\"\",\"host\":\"\"}', NULL, NULL);
INSERT INTO `jsender`.`api_config` (`id`, `appId`, `cfgName`, `config`, `httpConfig`, `remark`) VALUES ('WxMpCfg-demo', 'demo', 'WxMpCfg', '{\"appId\":\"\",\"appSecret\":\"\"}', NULL, NULL);
INSERT INTO `jsender`.`api_config` (`id`, `appId`, `cfgName`, `config`, `httpConfig`, `remark`) VALUES ('WxQyCfg-demo', 'demo', 'WxQyCfg', '{\"corpId\":\"\",\"corpSecret\":\"\"}', NULL, NULL);

-- ----------------------------
-- Table structure for api_template
-- ----------------------------
CREATE TABLE `api_template` (
  `id` varchar(32) NOT NULL,
  `config` text,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of api_template
-- ----------------------------
INSERT INTO `api_template` (`id`, `config`, `remark`) VALUES ('demo', '{\"WxQySender\":{\"appId\":\"APPID\",\"level\":1,\"template\":{\"agentid\":\"1000003\",\"touser\":\"${user.userId}\",\"textcard\":{\"description\":\"\\n机器编号：${deviceCode}\\n \\n机器名称：${deviceName}\\n \\n离线时间: ${time}\\n \\n\",\"title\":\"您的监控设备触发【${title}】规则\",\"url\":\"https://baidu.com?deviceCode=${deviceCode}\"},\"msgtype\":\"textcard\"}},\"DingTalkSender\":{\"appId\":\"APPID\",\"level\":1,\"template\":{\"msgtype\":\"markdown\",\"markdown\":{\"title\":\"${title}\",\"text\":\"### ${title}\\n> - 机器编号：${deviceCode}\\n> - 机器名称：${deviceName}\\n> - 离线时间: ${time}\\n [[查看详情]](https://baidu.com)\"}}},\"JavaMailSender\":{\"appId\":\"APPID\",\"level\":0,\"template\":{\"subject\":\"您的监控设备触发【${title}】规则\",\"recipient\":\"${user.email}\",\"content\":\"\\n机器编号：${deviceCode}\\n \\n机器名称：${deviceName}\\n \\n离线时间：${time}\\n \\n\"}}}', NULL);
