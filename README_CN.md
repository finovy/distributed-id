分布式ID


<p align="center">
  <a href="https://github.com/finovy/distributed-id"><img src="https://img.shields.io/github/stars/finovy/distributed-id?style=flat-square&logo=github"></a>
  <a href="https://github.com/finovy/distributed-id/network/members"><img src="https://img.shields.io/github/forks/finovy/distributed-id?style=flat-square&logo=GitHub"></a>
  <a href="https://github.com/finovy/distributed-id/blob/main/LICENSE"><img src="https://img.shields.io/github/license/finovy/distributed-id.svg?style=flat-square"></a>
</p>


中文版 | [English](README.md)

- [项目介绍](#项目介绍)
- [运行环境](#运行环境)
- [功能特性](#功能特性)
- [项目结构](#项目结构)
- [实现原理](#实现原理)
    - [Redis](#redis)
    - [Segment](#segment)
    - [**Snowflake**](#snowflake)
- [快速开始](#快速开始)
    - [项目启动](#项目启动)
    - [配置参考](#配置参考)
    - [客户端如何对接](#客户端如何对接)
        - [Http方式:](#http方式)
        - [Grpc方式:](#grpc方式)
        - [Rpc方式(Dubbo):](#rpc方式dubbo)
- [压测记录](#压测记录)
- [常见问题](#常见问题)

## 项目介绍

该项目以Redis、Segment和Snowflake作为主要的分布式ID生成方案，旨在降低对外部依赖的程度，同时规范团队内部的分布式ID生成流程。项目支持三种访问方式：Http、Rpc和Grpc，以最大程度地满足内部多语言项目的需求。

## 运行环境

- Java 17 +
- Spring Cloud 4.0.5
- Spring Cloud Alibaba 2022.0.0.0-RC2
- Redis
- Zookeeper
- Mysql
- Sentinel 1.8.6
- Nacos 2.3.2
- Dubbo 3.2.11

## 功能特性

- Redis中使用Lua脚本实现自增ID，并将其持久化到数据库。
- 集成了高效的Segment内存发号方案。
- 集成了Snowflake ID生成方案。
- 高并发情况下采用弹性缓存ID，提升了系统的扩展能力。

## 项目结构

```java
distributed-id
├─distributed-id-api            : 对外api包，用于dubbo集成
├─distributed-id-bootstrap      : 项目启动目录(对外接口层)
├─distributed-id-common         : 系统常量及异常定义
├─distributed-id-core           : 分布式ID实现核心逻辑
├─distributed-id-datasource     : 数据库访问层
└─script
    └─mysql                     : Mysql数据库表初始化脚本
```

## 实现原理

### Redis

**简述:** 使用 redis在初始化时向数据库申请一段数据，最大值使用max标识，value标识当前值。后续向Redis获取值时，通过Lua脚本进行判断，当前号段是否用尽。当然基于Redis是单线程读写的形式，我们会有定时任务判断号段是否达到阈值而进行补充。

**流程:**

![redis_example](/docs/source/image/redis_example.png)

![redis_principle](/docs/source/image/redis_principle.png)

### Segment

**简述:** 参考美团Leaf实现方案。

![segment_principle](/docs/source/image/segment_principle.png)

### **Snowflake**

**简述:** 参考美团Snowflake改良方案。

## 快速开始

#### 项目启动

1. 在配置中心配置 distributed-id.yaml，配置相应的环境信息。
2. (可选，开启Redis方式时必须配置)在配置中心配置 framework-core-redis，填写相应的sentinel Redis配置。
3. 在Mysql数据库执行script/mysqk/table_init.sql,初始化表。
4. 克隆该项目，将 distributed-id-bootstrap/src/main/resource/enviroment/application.yaml 相关配置中心信息替换，或者在项目环境变量配置对应的信息。
5. 启动 tech.finovy.distributed.id.DistributedApplication。

#### 配置参考

1. distributed-id.yaml

作用: 用于项目基础配置

```yaml
distributed:
  # 数据库相关配置，如果只开启了snowflake则可不配置此项
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/distributed_id?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT%2B0
    password: username
    username: password
  redis:
    # 控制redis方式是否开启，开启则需要framework-core-redis配置
    enable: true
  segment:
    # 控制号段模式是否开启
    enable: true
  snowflake:
    # 控制雪花模式是否开启
    enable: true
    zookeeper:
      # 开启时必需此项配置(集群模式，各IP使用逗号区分)
      address: 127.0.0.1:2181
```



#### 客户端如何对接

##### Http方式:

**接口描述**: 获取分布式ID

**请求方式**: GET

**请求路径**:

- /distributed/{type}/{key}  (获取单个不拼接前缀)
- /distributed/{type}/{key}/{batch} (获取多个不拼接前缀)
- /distributed/{type}/{key}/{prefix}/{length} (获取单个拼接前缀，补齐长度)
- /distributed/{type}/{key}/{batch}/{prefix}/{length}  (获取多个个拼接前缀，补齐长度)

**测试地址**：NONE

**参数说明**:

| 参数名 | 参数类型 | 是否必传 | 参数位置 | 参数描述                                                     |
| ------ | -------- | -------- | -------- | ------------------------------------------------------------ |
| type   | string   | 是       | path     | 代表分布式id的实现类型，可选: redis,snowflake,segment        |
| key    | string   | 是       | path     | 标识业务唯一字符串                                           |
| batch  | int      | 否       | path     | 代表数量，不传时，默认请求单个分布式id，传递时取批量id，     |
| prefix | string   | 否       | path     | 拼接前缀，比如: SN                                           |
| length | int      | 否       | path     | 限制整个分布式id的长度，>0,且大于 prefix(长度) + id(原始长度)，则对id前置补0。 例: SN0001>0,且小于 prefix(长度) + id(原始长度)，则直接返回拼接后的数据，不补0。例：SN123456<=0, 直接拼接返回，不补0  (建议直接传递 -1) |

**响应类型**：application/json

**响应参数:**

| 参数名  | 参数类型 | 参数描述                               |
| ------- | -------- | -------------------------------------- |
| code    | int      | 0代表成功，其余代表失败                |
| success | boolean  | code为0则响应true，其余响应false       |
| data    | long     | 请求单个则响应单个，请求多个则响应列表 |
| msg     | string   | 对响应code的详细描述                   |

**正常示例:**

```JSON
单个:
{
    "code": 0,
    "success": true,
    "data": 406,
    "msg": "success"
}

{
    "code": 0,
    "success": true,
    "data": "SN5102",
    "msg": "success"
}
批量:
{
    "code": 0,
    "success": true,
    "data": [
        400,
        401
    ],
    "msg": "success"
}

{
    "code": 0,
    "success": true,
    "data": [
        "SN5092",
        "SN5093",
        "SN5094",
        "SN5095",
        "SN5096",
        "SN5097",
        "SN5098",
        "SN5099",
        "SN5100",
        "SN5101"
    ],
    "msg": "success"
}
```

**异常示例**:

```JSON
{
    "code": 500,
    "success": false,
    "data": null,
    "msg": "distributed-id server error"
}
```

##### Grpc方式:

proto文件:

```ProtoBuf
syntax = "proto3";

option java_multiple_files = false;
option java_package = "tech.finovy.distributed.id.grpc";
option java_outer_classname = "DistributedIdProto";

service Segment {
  rpc getId(IdRequest) returns (IdResponse) {}
  rpc getIds(IdListRequest) returns (IdListResponse) {}
}

service Redis {
  rpc getId(IdRequest) returns (IdResponse) {}
  rpc getIds(IdListRequest) returns (IdListResponse) {}
}

service Snowflake {
  rpc getId(IdRequest) returns (IdResponse) {}
  rpc getIds(IdListRequest) returns (IdListResponse) {}
}

message IdRequest {
  string key = 1;
}

message IdResponse {
  int32  code = 1;
  int64 data = 2;
  string message = 3;
}

message IdListRequest {
  string key = 1;
  int32 batch = 2;
}

message IdListResponse {
  int32 code = 1;
  repeated int64 data = 2;
  string message = 3;
}
```

![grpc_example](/docs/source/image/grpc_example.png)

响应示例:

```JSON
单个
{
    "ids": [
        "119041"
    ],
    "code": 0,
    "message": "success"
}
批量
{
    "code": 0,
    "id": "407",
    "message": "success"
}
```

##### Rpc方式(Dubbo):

**引入依赖:**

```XML
<dependency>
   <groupId>tech.finovy</groupId>
   <artifactId>distributed-id-api</artifactId>
</dependency>
```

**使用示例:**

```Java
@DubboReference(group = "redis")
DistributedIdService idService;
//
Long id = idService.getId("distributed-id-key");
List<Long> ids = idService.getIds("distributed-id-key",100);

String id = idService.getId("distributed-id-key", "SN", 99);
List<String> ids = idService.getIds("distributed-id-key", 100, "SN", 99);
```

备注: Group 必填, 但可选: redis,snowflake,segment,分别代表不同的实现方式，各自的使用场景有差别。



## 压测记录

## 常见问题
