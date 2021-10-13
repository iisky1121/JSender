#### 1. 定义公共模板参数data
```
{
  "data": {
    "deviceCode": "cs079",
    "time": "2021-09-16 15:40:04",
    "title": "离线告警",
    "deviceName": "cs079"
  }
}
```
#### 2. 定义模板内容
```
{
  "WxQySender": {
    "appId": "APPID",
    "level": 1,
    "template": {
      "agentid": "1000003",
      "touser": "${user.userId}",
      "textcard": {
        "description": "\n机器编号：${deviceCode}\n \n机器名称：${deviceName}\n \n离线时间: ${time}\n \n",
        "title": "您的监控设备触发【${title}】规则",
        "url": "https://baidu.com?deviceCode=${deviceCode}"
      },
      "msgtype": "textcard"
    }
  },
  "DingTalkSender": {
    "appId": "APPID",
    "level": 1,
    "template": {
      "msgtype": "markdown",
      "markdown": {
        "title": "${title}",
        "text": "### ${title}\n> - 机器编号：${deviceCode}\n> - 机器名称：${deviceName}\n> - 离线时间: ${time}\n [[查看详情]](https://baidu.com)"
      }
    }
  },
  "JavaMailSender": {
    "appId": "APPID",
    "level": 0,
    "template": {
      "subject": "您的监控设备触发【${title}】规则",
      "recipient": "${user.email}",
      "content": "\n机器编号：${deviceCode}\n \n机器名称：${deviceName}\n \n离线时间：${time}\n \n"
    }
  }
}
```
#### 3. 往data添加推送用户的值，如email、phone、openId、userId等
```
{
  "data": {
    "deviceCode": "cs079",
    "time": "2021-09-16 15:40:04",
    "title": "离线告警",
    "deviceName": "cs079",
    "user": {
      "userId": ["userId1", "userId2", "userId3", "userId4"],
      "email": ["email1", "email2"]
    }
  }
}
```

#### 4. 调用模板发送接口 end