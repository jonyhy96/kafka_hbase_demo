package cn.ghostcloud.demo.hbase;

import cn.ghostcloud.demo.model.Human;
import cn.ghostcloud.demo.model.Response;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class HbaseDao {
    // 声明静态配置
    private static Configuration conf;
    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "bd-node0,bd-node1,bd-node2");
        conf.set("hbase.zookeeper.property.clientPort","2181");
    }

    /*
     * 为表添加数据（适合知道有多少列族的固定表）
     *
     * @rowKey rowKey
     *
     * @tableName 表名
     *
     * @column 第一个列族列表
     *
     * @value 第一个列的值的列表
     *
     */
    public static void addData(String rowKey, String tableName,
                               String column, String value)
            throws IOException {
        Put put = new Put(Bytes.toBytes(rowKey));// 设置rowkey
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(TableName.valueOf(tableName));// 获取表
        Cell cell = CellUtil.createCell(Bytes.toBytes(rowKey),Bytes.toBytes("cf"),Bytes.toBytes(column),System.currentTimeMillis(), KeyValue.Type.Put.getCode(),Bytes.toBytes(value));
        put.add(cell);
        table.put(put);
        System.out.println("add data Success!");
        table.close();
    }

    /*
     * 根据rwokey查询
     *
     * @rowKey rowKey
     *
     * @tableName 表名
     */
    public static Human getResult(String tableName, String rowKey)
            throws IOException {
        final String[] quali = new String[1];
        Human human = new Human();
        Get get = new Get(Bytes.toBytes(rowKey));
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(TableName.valueOf(tableName));// 获取表
        Result result = table.get(get);
        Cell[] cells = result.rawCells();
        for(Cell cell:cells){
            quali[0] = Bytes.toString(
                    cell.getQualifierArray(),
                    cell.getQualifierOffset(),
                    cell.getQualifierLength());
            switch (quali[0]) {
                case "name":
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    human.setName(value);
                    break;
                case "age":
                    human.setAge(Integer.valueOf(Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength())));
                    break;
                case "job":
                    human.setJob(Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                    break;
            }
        }
        table.close();
        return human;
    }

    /*
     * 遍历查询hbase表
     *
     * @tableName 表名
     */
    public static ArrayList<Response> getResultScann(String tableName) throws IOException {
        Scan scan = new Scan();
        ResultScanner rs;
        ArrayList<Response> responses=new ArrayList<>();
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(TableName.valueOf(tableName));// 获取表
        rs = table.getScanner(scan);
        for(Result result:rs){
            Cell[] cells = result.rawCells();
            for(Cell cell:cells){
                Response response = new Response();
                response.setRow(Bytes.toString(cell.getRowArray(),cell.getRowOffset(),cell.getRowLength()));
                response.setFamily(Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength()));
                response.setQualifier(Bytes.toString(cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength()));
                response.setValue(Bytes.toString(cell.getValueArray(),cell.getValueOffset(),cell.getValueLength()));
                response.setTimestamp(cell.getTimestamp());
                responses.add(response);
            }
        }
        table.close();
        return responses;
    }

    /*
     * 查询表中的某一列
     *
     * @tableName 表名
     *
     * @rowKey rowKey
     */
    public static Map<String,String> getResultByColumn(String tableName, String rowKey,
                                         String familyName, String columnName) throws IOException {
        Map<String,String> map = new HashMap<>();
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(TableName.valueOf(tableName));// 获取表
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName)); // 获取指定列族和列修饰符对应的列
        Result result = table.get(get);
        Cell[] cells = result.rawCells();
        for(Cell cell:cells){
            map.put(Bytes.toString(
                    cell.getQualifierArray(),
                    cell.getQualifierOffset(),
                    cell.getQualifierLength()
                    ),
                    Bytes.toString(cell.getValueArray(),
                            cell.getValueOffset(),
                            cell.getValueLength()
                    )
            );
        }
        table.close();
        return map;
    }

    /*
     * 更新表中的某一列
     *
     * @tableName 表名
     *
     * @rowKey rowKey
     *
     * @familyName 列族名
     *
     * @columnName 列名
     *
     * @value 更新后的值
     */
    public static void updateTable(String tableName, String rowKey,
                                   String familyName, String columnName, String value)
            throws IOException {
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(TableName.valueOf(tableName));// 获取表
        Put put = new Put(Bytes.toBytes(rowKey));
        Cell cell = CellUtil.createCell(Bytes.toBytes(rowKey),Bytes.toBytes(familyName),Bytes.toBytes(columnName),System.currentTimeMillis(), KeyValue.Type.Put.getCode(),Bytes.toBytes(value));
        put.add(cell);
        table.put(put);
        System.out.println("update table Success!");
        table.close();
    }

    /*
     * 删除指定的列
     *
     * @tableName 表名
     *
     * @rowKey rowKey
     *
     * @familyName 列族名
     *
     * @columnName 列名
     */
    public static void deleteColumn(String tableName, String rowKey,
                                    String fameliName, String columnName) throws IOException {
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(TableName.valueOf(tableName));// 获取表
        Delete deleteColumn = new Delete(Bytes.toBytes(rowKey));
        deleteColumn.deleteColumn(Bytes.toBytes(fameliName),Bytes.toBytes(columnName));
        table.delete(deleteColumn);
        System.out.println(fameliName + ":" + columnName + " is deleted!");
        table.close();
    }

    /*
     * 删除指定的行
     *
     * @tableName 表名
     *
     * @rowKey rowKey
     */
    public static void deleteAllColumn(String tableName, String rowKey)
            throws IOException {
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(TableName.valueOf(tableName));// 获取表
        Delete deleteAll = new Delete(Bytes.toBytes(rowKey));
        table.delete(deleteAll);
        System.out.println("all columns are deleted!");
        table.close();
    }
}
