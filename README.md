#### JSender
JSender 统一多渠道推送消息格式，支持http接口模板推送和批量推送，适用服务告警通知、设备异常通知等场景。

#### 项目背景
物联网项目，设备异常、离线等告警等场景需要通过不同的消息渠道通知不同运营商的用户，并且每个消息渠道主体归属于运营商。
功能可能并不复杂，但是重复的代码极其难于管理，而且多种品类设备往往需要对接不同的渠道，所以现在把这部分功能整合成JSender项目。

#### 支持消息
-  微信公众号模板消息【[WxMpSender](https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Template_Message_Interface.html)】
-  微信小程序统一服务消息【[WxMiniAppSender](https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/uniform-message/uniformMessage.send.html)】
-  企业微信应用消息【[WxQySender](https://work.weixin.qq.com/api/doc/90000/90135/90236)】
-  钉钉群机器人【[DingTalkSender](https://developers.dingtalk.com/document/robots/custom-robot-access)】
-  Email(JavaMail+STMP)【[JavaMailSender](https://javaee.github.io/javamail/)】
-  阿里云短信服务(国内+国际)【AliSmsSender】[[国内](https://www.aliyun.com/product/sms)] [[国际](https://www.alibabacloud.com/help/zh/product/44282.html)]
-  阿里云邮件推送【[AliDmsSender](https://www.aliyun.com/product/directmail)】
-  阿里云语音服务【[AliVmsSender](https://www.aliyun.com/product/vms)】

#### 环境依赖
1.  JDK1.8及以上环境
2.  Netty作为HttpServer
3.  MySql作为配置信息存储和模板信息存储
4.  Redis作为Token存储和MySql配置缓存（非必须）
5.  使用正则表达式解析模板参数
6.  Hutool作为db、Jedis等工具类SDK

#### 使用说明
1. **`工作原理`**：通过正则表达式从模板提取变量，从入参里面获取对应的模板参数集合，把带参数的模板渲染成字符串作为入参传递给第三方系统完成消息发送
2. 复杂的业务需求建议使用模板发送需求，在模板设置通用的前提下，后续增加消息渠道理论上只需要修改模板内容即可实现该渠道消息发送功能，且无需修改调用方代码。
3. 如需控制控制模板中已配置的消息渠道，可以通过`level`（默认0，不建议）或者模板中的`$Sender.level`（建议）控制，`$Sender.level>level` 则发送
4. 【[使用说明参考](doc/use.md)】
5. 数据库中与代码中的`appId`参数，与渠道的`appId`无关系（不是微信公众号的`appId`参数）

#### 消息发送-自定义
> 接口： `/send`

##### 请求参数说明

| 参数名称     | 参数类型        | 是否必填          | 说明          |
|----------|-------------|---------------|-------------|
| data    | JSON      | 否 | 公共模板参数值    |
| appId    | String    | `appId`、`$.appId`和`$.cfg`必填一个    | 配置信息，优先级低    | 
| $Sender.appId    | String      | `appId`、`$.appId`和`$.cfg`必填一个 | 配置信息，优先级中    |
| $Sender.cfg      | JSON        | `appId`、`$.appId`和`$.cfg`必填一个 | 配置信息，优先级高        |
| $Sender.template | String或JSON | 是             | 发送内容模板，可带参数 |
| $Sender.data     | JSON        | 否             | 模板内容参数值，相同key覆盖data参数值     |


#####  单渠道单用户例子1
>  直接传入`cfg`参数
```
{
  "JavaMailSender": {
    "cfg": {
      "account": "xxx",
      "host": "xxx",
      "password": "xxx"
    },
    "template": {
      "subject": "您的监控设备触发【${title}】规则",
      "recipient": "${email}",
      "content": "\n机器编号：${deviceCode}\n \n机器名称：${deviceName}\n \n离线时间：${time}\n \n"
    },
    "data": {
      "email": "xxx@163.com",
      "deviceCode": "cs079",
      "time": "2021-09-16 15:40:04",
      "title": "离线告警",
      "deviceName": "cs079"
    }
  }
}
```
#####   单渠道单用户例子2
>  传入`$.appId`参数，通过系统获取配置
```
{
  "data": {
    "deviceCode": "cs079",
    "time": "2021-09-16 15:40:04",
    "title": "离线告警",
    "deviceName": "cs079"
  },
  "JavaMailSender": {
    "appId": "demo",
    "template": {
      "subject": "您的监控设备触发【${title}】规则",
      "recipient": "${email}",
      "content": "\n机器编号：${deviceCode}\n \n机器名称：${deviceName}\n \n离线时间：${time}\n \n"
    },
    "data": {
      "email": "xxx@163.com"
    }
  }
}
```
#####   多渠道多用户例子1
>  多用户发送，传入`$.appId`和数组格式参数，如下面配置：`JavaMailSender.data.email`和`WxQySender.data.userId`
```
{
  "data": {
    "deviceCode": "cs079",
    "time": "2021-09-16 15:40:04",
    "title": "离线告警",
    "deviceName": "cs079"
  },
  "JavaMailSender": {
    "appId": "demo",
    "template": {
      "subject": "您的监控设备触发【${title}】规则",
      "recipient": "${email}",
      "content": "\n机器编号：${deviceCode}\n \n机器名称：${deviceName}\n \n离线时间：${time}\n \n"
    },
    "data": {
      "email": ["xxx1@163.com", "xxx2@163.com"]
    }
  },
  "WxQySender": {
    "appId": "demo",
    "template": {
      "agentid": "1000003",
      "touser": "${userId}",
      "textcard": {
        "description": "\n机器编号：${deviceCode}\n \n机器名称：${deviceName}\n \n离线时间：${time}\n \n",
        "title": "您的监控设备触发【${title}】规则",
        "url": "#"
      },
      "msgtype": "textcard"
    },
    "data": {
      "userId": ["userId1", "userId2"]
    }
  }
}
```
#####   多渠道多用户例子2
>  多用户发送，传入`appId`和数组格式参数，如下面配置：`data.email`和`data.userId`
```
{
  "appId": "demo",
  "data": {
    "deviceCode": "cs079",
    "time": "2021-09-16 15:40:04",
    "title": "离线告警",
    "deviceName": "cs079",
    "email": ["xxx1@163.com", "xxx2@163.com"],
    "userId": ["userId1", "userId2"]
  },
  "JavaMailSender": {
    "template": {
      "subject": "您的监控设备触发【${title}】规则",
      "recipient": "${email}",
      "content": "\n机器编号：${deviceCode}\n \n机器名称：${deviceName}\n \n离线时间：${time}\n \n"
    }
  },
  "WxQySender": {
    "template": {
      "agentid": "1000003",
      "touser": "${userId}",
      "textcard": {
        "description": "\n机器编号：${deviceCode}\n \n机器名称：${deviceName}\n \n离线时间：${time}\n \n",
        "title": "您的监控设备触发【${title}】规则",
        "url": "#"
      },
      "msgtype": "textcard"
    }
  }
}
```

#### 消息发送-模板
> 接口： `/template/send`

##### 请求参数说明

| 参数名称     | 参数类型        | 是否必填          | 说明          |
|----------|-------------|---------------|-------------|
| templateId    | String      | 是 | 模板ID    |
| level    | Integer      | 否 | （不建议）默认0，小于模板$Sender.level才触发该渠道    |  
| data    | JSON      | 否 | 公共模板参数值    |
| appId    | String    | `appId`、`$.appId`和`$.cfg`必填一个    | 配置信息，优先级低    | 
| $Sender.appId    | String      | `appId`、`$.appId`和`$.cfg`必填一个 | 配置信息，优先级中    |
| $Sender.cfg      | JSON        | `appId`、`$.appId`和`$.cfg`必填一个 | 配置信息，优先级高        |
| $Sender.data     | JSON        | 否             | 模板内容参数值，相同key覆盖data参数值     |

##### 发送例子1
```
{
  "templateId": "",
  "appId": "demo",
  "data": {
    "deviceCode": "cs079",
    "time": "2021-09-16 15:40:04",
    "title": "离线告警",
    "deviceName": "cs079"
  },
  "JavaMailSender": {
    "data": {
      "email": "xxx@163.com"
    }
  },
  "WxQySender": {
    "data": {
      "userId": "@all"
    }
  }
}
```
##### 发送例子2
```
{
  "templateId": "",
  "appId": "demo",
  "data": {
    "deviceCode": "cs079",
    "time": "2021-09-16 15:40:04",
    "title": "离线告警",
    "deviceName": "cs079",
    "user": {
      "email": "xxx@163.com",
      "userId": "@all"
    }
  }
}
```

#### 系统配置-第三方参数
#####  直接读取数据库配置
>  `api_config` 表 `config` 字段

#####  从第三方系统获取
>  `api_config` 表 `http` 字段
- `POST` `application/json` 请求，接口返回参数值需为`json`或者`xml`格式（注意）
- 配置参数项 {url, request, response}
- `request`配置值作为接口请求内容体，支持变量`${appId}`，如果`appId`参数为虚拟值，则需要写死正确值
- `response`配置值作为最终配置参数模板，接口返回值作为模板参数

>  例如: 
```
{
  "url": "http://url.com",
  "request": {
    "appId": "${appId}"
  },
  "response": {
    "appSecret": "${result.appSecret}"
  }
}
```
#####  自定义方式
>  修改`ISender`接口实现类, `cfg(String appId)` 方法

#### 系统配置-模板配置
#####  模板配置
>  `api_template` 表 `config` 字段
#####  例子
```
{
  "WxQySender": {
    "appId": "xxxx",
    "level": 1,
    "template": {
      "agentid": "1000003",
      "touser": "${userId}",
      "textcard": {
        "description": "\n机器编号：${deviceCode}\n \n机器名称：${deviceName}\n \n离线时间：${time}\n \n",
        "title": "您的监控设备触发【${title}】规则",
        "url": "https://baidu.com"
      },
      "msgtype": "textcard"
    }
  },
  "DingTalkSender": {
    "level": 2,
    "template": {
      "msgtype": "markdown",
      "markdown": {
        "title": "${title}",
        "text": "### ${title}\n> - 机器编号：${deviceCode}\n> - 机器名称：${deviceName}\n> - 离线时间：${time}\n [[查看详情]](https://baidu.com)"
      }
    }
  }
}
```

#### 部署
#####  初始化数据库脚本
>  [doc/JSender.sql](doc/JSender.sql)

#####  修改数据库、Redis等其他配置
>  参考Application.java文件

#### 开发
##### 代码结构
```
cache -- 缓存
db    -- 数据库
http  -- Http相关
      -- action  -- http接口定义
      -- server  -- http server
sdk   -- 对接第三方系统api SDK
sender-- 消息发送定义
      -- impl -- 各种消息发送实现
      -- service -- 获取配置和模板业务类
      -- JSender.java 核心功能
util  -- 工具包
```
##### 开发说明
1.  在sdk包下面实现对应消息发送api对接
2.  在 `sender.impl` 包下面定义消息服务名称并实现ISender接口相关功能
3.  获取配置业务查看 `ApiCfgService.java`
4.  获取模板业务查看 `ApiTemplateService.java`
5.  发送等其他功能查看 `JSender.java`

#### 更新日志
#####  v1.2 (2022-10-21)
````
1.  微信小程序和微信公众号Fix；
2.  区分SMS国内和国际版本；
3.  DbTable增加list和page；
 ```` 
#####  v1.1 (2021-12-23)
````
1.  支持xml格式；
2.  发送接口支持动态appId参数传入，并通过appId参数获取配置信息；
3.  加载默认配置，解决例如公众号token全部从第三方系统获取，不需要单独配置；
4.  修复$.user.userId，层级json参数无法获取的问题；
5.  修改数据库脚本文件，移除模板默认appId参数值配置；
6   WxQyCfg增加agentId属性值；
 ```` 