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
INSERT INTO `jsender`.`api_config` (`id`, `appId`, `cfgName`, `config`, `httpConfig`, `remark`) VALUES ('WxQyCfg-demo', 'demo', 'WxQyCfg', '{\"agentId\":\"\",\"corpId\":\"\",\"corpSecret\":\"\"}', NULL, NULL);

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
INSERT INTO `api_template` (`id`, `config`, `remark`) VALUES ('demo', '{\"WxQySender\":{\"level\":1,\"template\":{\"agentid\":\"${cfg.agentId}\",\"touser\":\"${user.userId}\",\"textcard\":{\"description\":\"\\n???????????????${deviceCode}\\n \\n???????????????${deviceName}\\n \\n????????????: ${time}\\n \\n\",\"title\":\"???????????????????????????${title}?????????\",\"url\":\"https://baidu.com?deviceCode=${deviceCode}\"},\"msgtype\":\"textcard\"}},\"DingTalkSender\":{\"level\":1,\"template\":{\"msgtype\":\"markdown\",\"markdown\":{\"title\":\"${title}\",\"text\":\"### ${title}\\n> - ???????????????${deviceCode}\\n> - ???????????????${deviceName}\\n> - ????????????: ${time}\\n [[????????????]](https://baidu.com)\"}}},\"JavaMailSender\":{\"level\":0,\"template\":{\"subject\":\"???????????????????????????${title}?????????\",\"recipient\":\"${user.email}\",\"content\":\"\\n???????????????${deviceCode}\\n \\n???????????????${deviceName}\\n \\n???????????????${time}\\n \\n\"}}}', NULL);
