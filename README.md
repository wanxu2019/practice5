[toc]


# 1. 项目管理API介绍 `v0.7`

## 1.1. 公共部分

### 1.1.1. 公共请求地址

| 环境    | HTTP请求                                              | HTTPS请求地址 |
|-------|-----------------------------------------------------|-----------|
| 服务器环境 | http://innovation.xjtu.edu.cn/projectManager/api/v1 | 暂无        |
| 本地环境  | /projectManager/api/v1/                             | 暂无        |

### 1.1.2. 公共请求参数

| 名称      | 类型     | 是否必须 | 描述                       |
|---------|--------|------|--------------------------|
| ~~appName~~**已废弃** |  ~~String~~ | ~~是~~    | ~~提供要查询的APP的记录~~             |
| format  | String | 否    | 响应格式。默认为json格式，目前只支持json |

### 1.1.3. 公共返回内容

``` json
{
    state: false,            //该次请求后台是否处理成功{true,false}
    error: "用户权限错误",    //如果请求失败，后台返回对应的失败信息
    content:{},              //如果请求成功，后台返回特定的对象 {Object}
    statecode: 0             //请求状态码，暂时未用
}
```
## 1.2. 项目操作部分

### 1.2.1. 数据库结构说明

**新版数据库操作说明**

`projectManager`最新版(`0.7`)采用自动建表的方式来维护数据。无须也不要手工去数据库建表，以免出现数据异常的现象。

目前自动建表的规则如下

1. `projectManager`会根据Http请求头中的`referer`识别对应的APP的`ContextPath`作为AppName。并进行规则处理后建立对应的表。比如开发者当前处于本地开发环境，所在的页面URL为`http://localhost:8080/fishbone/`。当使用`$.ajax`向`projectManager`发送请求时。请求头中的refer为`http://localhost:8080/fishbone/`，`projectManager`截取到`AppName`的为`fishbone`。后台就会建立对应的表`fishbone_project`。
2. `AppName`->`数据库表名`的映射规则为：
    - 基础是从`驼峰命名法`转变化`下划线命名法`。比如`fishbone`-->`fishbone_project`。
    - 如果`AppName`为全大写，那么认为`AppName`为多个单词的首字母缩写。所以**全部转化为小写**。比如`MSA`-->`msa_project`。
    - 如果某些AppName不规范。不能保证建表的正确性。目前只针**对首字母也大写**的AppName进行了适配。比如`ApplicationStyle`-->'application_style_project'


Note: 因为数据库的变动导致部分APP目前查询不到数据，需要开发者在建表后自行将数据迁移到新的表中。
自动建表功能导致请求的`AppName`字段废弃。希望大家在开发的时候按照最新的规则进行。之前的请求如果携带`AppName`,后台不会识别，所以无需修改。

<del> 
`xxx_project` 代表的是  App的的项目管理表,可以通过替换下面的fishbone为自己的appName,通过sql语句创建自己的表.当然应该在`innovation_project_manager`数据库内.

``` sql
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for fishbone_project
-- ----------------------------
DROP TABLE IF EXISTS `fishbone_project`;
CREATE TABLE `fishbone_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '当前项目ID',
  `projectName` varchar(255) DEFAULT NULL COMMENT '当前项目名，可以重复',
  `createDate` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '当前项目创建时间',
  `username` varchar(255) DEFAULT NULL COMMENT '当前用户名',
  `memo` varchar(255) DEFAULT NULL COMMENT '项目备注',
  `appResult` mediumtext COMMENT '项目报告结果',
  `domain` varchar(255) DEFAULT NULL COMMENT '项目所属群组',
  `appContent` text COMMENT '当前项目的内容',
  `reservation` text COMMENT '预留字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

```
`xxx_link` 代表的是App项目与模板项目的关联表,可以替换下面的fishbone为自己的appName,通过sql语句创建自己的表.当然应该在innovation_project_manager数据库内.

``` sql
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for fishbone_link
-- ----------------------------
DROP TABLE IF EXISTS `fishbone_link`;
CREATE TABLE `fishbone_link` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `tempProjectID` varchar(255) DEFAULT NULL COMMENT '绑定的模板项目ID',
  `projectID` int(11) DEFAULT NULL COMMENT '绑定的APP项目ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


```
</del>
### 1.2.2. 请求地址

公共路径后添加project
>HTTP请求地址	

| 环境    | HTTP请求                                                      | HTTPS请求地址 |
|-------|-------------------------------------------------------------|-----------|
| 服务器环境 | http://innovation.xjtu.edu.cn/projectManager/api/v1/project | 暂无        |
| 本地环境  | /projectManager/api/v1/project                              | 暂无        |

>后台返回的错误提示内容

``` java
    public final static String PARAMS_ERROR = "参数错误";
    public final static String NORESULT_ERROR = "没有对应的记录";
    public final static String DB_ERROR = "数据库连接错误";
    public final static String PERMISSION_ERROR = "用户权限错误";
```

### 1.2.3. `GET请求（SELECT）`：从服务器取出资源（一项或多项）

#### 1.2.3.1. 查询具体项目记录 

>支持的参数

| 名称  | 类型  | 是否必须 | 描述                            |
|-----|-----|------|-------------------------------|
| id  | int | 是    | APP项目ID,代表单个项目，使用该参数后其余参数不会生效 |

>请求示例

``` javascript
$.ajax({
    url:"/projectManager/api/v1/project",
    type:"get",
    data:{
        id:1
    },
    success:function(result){
        if(result.state){
            //请求正确
            console.log(result.content)
        }else{
            //请求错误
            console.log(result.error)
        }
    }
})
```

>返回的内容举例
``` json
{
    state: true,                   //该次请求后台是否处理成功{true,false}
    error: "",           //如果请求失败，后台返回对应的失败信息
    content:{                       //内容区域
        "appResult":"123",           //片段
        "createDate":792069612000,   //创建时间
        "domain":"1",                //所属群组
        "memo":"123",                //备注
        "id":1,               //项目ID
        "projectName":"123",         //项目名
        "username":"123",             //记录所属的用户名
        "appContent":"",              //app用来保存项目数据的字段,如果是JSON或者其他文本类型的数据可以保存到当前字段,要注意该字段对应的数据库格式为text,请注意大小.
        "reservation":""              //预留字段,app可以用来保存自定的文本数据,也是text大小的
        },                           //如果请求成功，后台返回特定的对象 {Object}
    statecode: 0                     //请求状态码，暂时未用
}
```
**以下功能因为需求变动全部删除。都是泪啊。**

<del>
#### 1.2.3.2. 查询所有记录

>请求示例

``` javascript
$.ajax({
    url:"/projectManager/api/v1/project",
    type:"get",
    //可以传递username查询特定用户的
    success:function(result){
          if(result.state){
            //请求正确
            console.log(result.content)
        }else{
            //请求错误
            console.log(result.error)
        }
    }
})
```
>返回的内容

``` json
{
    "content":[{
        "appContent":"",
        "appResult":"",
        "createDate":1517230574000,
        "domain":"1",
        "id":28,
        "memo":"asdf",
        "projectName":"asdf",
        "reservation":"",
        "username":"111"
        },{"appContent":"",
        "appResult":"",
        "createDate":1517231164000,
        "domain":"1",
        "id":29,
        "memo":"ASDFASDF",
        "projectName":"ADFSD",
        "reservation":"",
        "username":"111"
        },{
        "appContent":"",
        "appResult":"",
        "createDate":1517245344000,
        "domain":"1",
        "id":30,
        "memo":"asdf",
        "projectName":"asdf",
        "reservation":"",
        "username":"111"
        }],
    "state":true,
    "statecode":0
}
```

#### 1.2.3.3. 查询模板ID对应的记录 `user`,`admin`和`superAdmin`

>支持的参数

| 名称            | 类型     | 是否必须 | 描述     |
|---------------|--------|------|--------|
| tempProjectID | String | 是    | 查询模板ID |

>请求示例

``` javascript
$.ajax({
    url:"/projectManager/api/v1/project",
    type:"get",
    //填写模板ID
    data:{
        appName:"fishbone",
        tempProjectID:"DMAIC"
    },
    success:function(result){
        ...
    }
})
```

>返回的内容举例(同ID查询)

``` json
{
    state: true,                   //该次请求后台是否处理成功{true,false}
    error: "",           //如果请求失败，后台返回对应的失败信息
    content:{                       //内容区域
        "appResult":"123",           //片段
        "createDate":792069612000,   //创建时间
        "domain":"1",                //所属群组
        "memo":"123",                //备注
        "id":1,               //项目ID
        "projectName":"123",         //项目名
        "username":"123",             //记录所属的用户名
        "appContent":"",              //app用来保存项目数据的字段,如果是JSON或者其他文本类型的数据可以保存到当前字段,要注意该字段对应的数据库格式为text,请注意大小.
        "reservation":""   
        },                           //如果请求成功，后台返回特定的对象 {Object}
    statecode: 0                     //请求状态码，暂时未用
}
```
#### 1.2.3.4. 查询当前群组内的所有记录，`admin`和`superAdmin`

>支持的参数

| 名称     | 类型  | 是否必须 | 描述                                        |
|--------|-----|------|-------------------------------------------|
| domain | int | 是    | 所要查询的群组ID，-1代表查询当前群组(对于超级管理员来说-1代表全域内的记录) |

>请求示例

``` javascript
$.ajax({
    url:"/projectManager/api/v1/project",
    type:"get",
    //群组ID
    data:{
        appName:"fishbone",
        domain:1
    },
    success:function(result){
          if(result.state){
            //请求正确
            console.log(result.content)
        }else{
            //请求错误
            console.log(result.error)
        }
    }
})

```

>返回的内容举例

``` json
{
    state: true,                       //该次请求后台是否处理成功{true,false}
    // error: "用户权限错误",           //如果请求失败，后台返回对应的失败信息
    content:[{                         //返回结果以Array形式
            "appResult":"123",
            "createDate":792069612000,
            "domain":"1",
            "memo":"123",
            "id":1,
            "projectName":"123",
            "username":"123",
            "appContent":"",              //app用来保存项目数据的字段,如果是JSON或者其他文本类型的数据可以保存到当前字段,要注意该字段对应的数据库格式为text,请注意大小.
            "reservation":""
        },{
            "appResult":"123",
            "createDate":792069612000,
            "domain":"1",
            "memo":"123",
            "id":2,
            "projectName":"1233",
            "username":"123",
            "appContent":"",              //app用来保存项目数据的字段,如果是JSON或者其他文本类型的数据可以保存到当前字段,要注意该字段对应的数据库格式为text,请注意大小.
            "reservation":""
            }],                          //如果请求成功，后台返回特定的对象 {Object}
    statecode: 0                         //请求状态码，暂时未用
}
```
</del>

### 1.2.4. `POST请求（CREATE）`：在服务器新建一个资源。
支持的参数
| 名称            | 类型     | 是否必须 | 描述      |
|---------------|--------|------|---------|
| projectName   | String | 是    | APP项目名称 |
| memo          | String | 否    | 项目的备注   |
| appResult     | String | 否    | 项目报告    |
| tempProjectID | String | 否    | 模板ID    |
| appContent    | String | 否    | app的数据  |
| reservation   | String | 否    | 保留字段    |

>请求示例

``` javascript
$.ajax({
    url:"/projectManager/api/v1/project",
    type:"post",
    //群组ID
    data:{
        projectName:'鱼骨头1',
        memo:'今天我画了一个鱼骨图',
        appResult:'这里是word编辑区的内容',
        tempProjectID:"这是绑定的内容"
    },
    success:function(result){
          if(result.state){
            //请求正确
            console.log(result.content)
        }else{
            //请求错误
            console.log(result.error)
        }
    }
})

```

>相应示例

``` json
{
    "content":{
        "appResult":"这里是word编辑区的内容",
        "domain":"0",
        "id":20,
        "memo":"今天我画了一个鱼骨图",
        "projectName":"鱼骨头1",
        "username":"admin",
        "appContent":"",
        "reservation":""
    },
    "state":true,
    "statecode":0
}
```

### 1.2.5. `PUT请求（UPDATE）`：在服务器更新资源（客户端提供改变后的完整资源）。
支持的参数

| 名称          | 类型     | 是否必须 | 描述      |
|-------------|--------|------|---------|
| id          | int    | 是    | APP项目ID |
| projectName | String | 否    | 项目名     |
| memo        | String | 否    | 项目的备注   |
| appResult   | String | 否    | 项目报告    |
| appContent  | String | 否    | app的数据  |
| reservation | String | 否    | 保留字段    |

>请求示例

``` javascript
$.ajax({
    url:"/projectManager/api/v1/project",
    type:"put",
    //群组ID
    data:{
        id:9,
        projectName:'鱼骨头1',
        memo:'今天我画了一个鱼骨图',
        appResult:'这里是word编辑区的内容',
        tempProjectID:"这是绑定的内容",
        appContent:"",
        reservation:""
    },
    success:function(result){
          if(result.state){
            //请求正确
            console.log(result.content)
        }else{
            //请求错误
            console.log(result.error)
        }
    }
})
```
*出现错误*
``` json
{
    "error":"用户权限错误",
    "state":false,
    "statecode":0
}
```
*请求成功*
``` json
{
    "state":true,
    "statecode":0
}
```
### 1.2.6. `DELETE请求（DELETE）`：从服务器删除资源。
| 名称  | 类型  | 是否必须 | 描述      |
|-----|-----|------|---------|
| id  | int | 是    | APP项目ID |

>请求示例

``` javascript
$.ajax({
    url:"/projectManager/api/v1/project",
    type:"delete",
    //群组ID
    data:{
        id:9,
    },
    success:function(result){
        if(result.state){
            //请求正确
            console.log(result.content)
        }else{
            //请求错误
            console.log(result.error)
        }
    }
})
```
>响应示例

*出现错误*
``` json
{
    "error":"用户权限错误",
    "state":false,
    "statecode":0
}
```
*请求成功*
``` json
{
    "state":true,
    "statecode":0
}
```

