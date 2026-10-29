# R2YC dynamic-datasource
基于SpringBoot开发的轻量级动态数据源框架, 支持Springboot3.x, Springboo4.x，支持Seata数据源。目前仅支持HikariCP连接池以及其数据源。
# 如何在Springboot项目中使用
## pom引入依赖
```
<dependency>
    <groupId>io.github.r2yc</groupId>
    <artifactId>r2yc-dynamic-datasource-spring-boot-starter</artifactId>
    <version>4.1.2.1</version>
</dependency>
```
## application.yml中增加配置
```
spring:
  ...
  datasource:
    r2yc: # 必须
      dynamic: # 必须
        primary: test1 # 设置主库是哪个数据源名称
        seata: false
        health-indicator: true
        dbs:
          test1: #数据源名称，其它配置项与原springboot配置相同
            url: jdbc:mysql://127.0.0.1:3306/db1?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
            driver-class-name: com.mysql.cj.jdbc.Driver
            username: root
            password: 123456
            hikari:
              minimum-idle: 2
              maximum-pool-size: 8
              idle-timeout: 300000
              connection-timeout: 20000
           test2: #数据源名称，其它配置项与原springboot配置相同
            url: jdbc:mysql://127.0.0.1:3306/db2?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
            driver-class-name: com.mysql.cj.jdbc.Driver
            username: root
            password: 123456
            hikari:
              minimum-idle: 2
              maximum-pool-size: 8
              idle-timeout: 300000
              connection-timeout: 20000

```

# Mybatis Mapper中增加注解 @MDS("test1") 
```java
package com.xxx.xxx.api.web.infrastructure.mapper.support;

import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.annotation.MDS;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 翻译统计 Mapper — 查询 kairos-support 库
 *
 * @author zhanghaoran
 */
@MDS("test1") #与数据源名称相同
@Mapper
public interface StatTranslationMapper {

    /**
     * 翻译统计原始计数：dayTotal, dayGoogle, dayCache, monthTotal, monthGoogle, monthCache
     */
    Map<String, Object> selectTranslationCounts();
}

```

# 业务Service中增加
```
@Service
public class TestService {
    private final TagMapper tagMapper;

    public TagService(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @DS("test1")
    @Transactional(rollbackFor = Exception.class)
    public Integer saveTagInfo(List<TagInfoCommand> tagInfoCommands, OmsUser omsUser) {
        ...
    }

    @DS("test2")
    @Transactional(rollbackFor = Exception.class)
    public Integer saveTagInfo(List<TagInfoCommand> tagInfoCommands, OmsUser omsUser) {
        ...
    }
```



  
