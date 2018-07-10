# Kafka_hbase_demo

------

本工程是对kafka和hbase操作的一个简单的demo，其中包括以下功能

> * 向kafka生产数据
> * 从kafka读取数据
> * 向hbase增加数据
> * 删除hbase中的数据
> * 修改hbase中的数据
> * 查询hbase中的数据


## Kafka

kafka关键配置为：
```xml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:192.168.34.40:9092}
    consumer:
      group-id: defaultConsumerGroup
    producer:
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```
> * bootstrap-servers声明kafka服务地址和端口
> * consumer.group-id声明kafka消费者ID
> * producer.value-serializer声明kafka生产数据时数据的序列化方式

kafka生产接口：/demo/produce
```
POST: {
	"name":"jony",
	"age":12,
	"job":"coder"
}
```

## Hbase
hbase的关键配置为：
```xml
hbase:
  zookeeper:
    quorum: ${ZOOKEEPER_QUORUM:bd-node0,bd-node1,bd-node2}
    property:
      clientPort: ${ZOOKEEPER_CLIENT_PORT:2181}
  rootdir: ${ZOOKEEPER_ROOT_DIR:hdfs://bd-node0:9000/hbase}
```
> * zookeeper.quorum声明hbase所依赖的zookeeper服务地址
> * zookeeper.property.clientPort声明zookeeper服务端口
> * rootdir声明hbase向zookeeper注册时的根目录地址

hbase增加数据接口：/demo/add
```
POST: {
	"name":"jony",
	"age":12,
	"job":"coder"
}
```
hbase删除指定rowkey数据接口：/demo/deleteall
```
POST: {
	"name":"jony"
}
```
hbase删除指定rowkey,column数据接口：/demo/delete
```
POST: {
	"name":"jony",
	"column":"age"
}
```
hbase更新数据接口：/demo/update
```
POST: {
	"name":"jony",
	"age":12,
	"job":"coder"
}
```
hbase查询指定rowkey数据接口：/demo/findall
```
POST: {
	"name":"jony"
}
```
hbase查询指定rowkey,clolumn数据接口：/demo/find
```
POST: {
	"name":"jony",
	"column":"age"
}
```
hbase遍历表内所有数据：/demo/scan
```
Get
```
在查询hbase表时可以设置拦截器对数据进行筛选，常用的拦截器有：
> * SingleColumnValueFilter 

SingleColumnValueFilter 用于测试值的情况（相等，不等，范围 )
下面一个检测列族 family 下的列 qualifier 的列值和字符串 "my-value" 相等的部分示例代码 : 
```
    Scan scan = new Scan();
    SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes("family"),Bytes.toBytes("qualifier"), CompareOp.EQUAL, Bytes.toBytes("my-value"));
    scan.setFilter(filter);
```
> * SingleColumnValueExcludeFilter

跟 SingleColumnValueFilter 功能为查询出不满足条件的列值。
下面的代码就会查询出 family 列族下 qualifier 列不为"my-value"的值:
```
Scan scan = new Scan();
SingleColumnValueExcludeFilter filter = new SingleColumnValueExcludeFilter(Bytes.toBytes("family"), Bytes.toBytes("qualifier"), CompareOp.EQUAL, Bytes.toBytes("my-value"));
scan.setFilter(filter);
```
> * FamilyFilter

用于过滤列族（通常在 Scan 过程中通过设定某些列族来实现该功能，而不是直接使用该过滤器）。
```
    Scan scan = new Scan();
    FamilyFilter filter = new FamilyFilter(CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("my-family"))); // 列族为 my-family
    scan.setFilter(filter);
```

> * ColumnPrefixFilter 

用于列名（Qualifier）前缀过滤，即包含某个前缀的所有列名。
```
    Scan scan = new Scan();
    ColumnPrefixFilter filter = new ColumnPrefixFilter(Bytes.toBytes("my-prefix")); // 前缀为 my-prefix
    scan.setFilter(filter);
```
更多filter参见[官方网址](https://hbase.apache.org/apidocs/org/apache/hadoop/hbase/filter/package-summary.html)