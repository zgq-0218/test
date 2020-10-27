package com.atguigu5.dbutils;

import com.atguigu2.bean.Customer;
import com.atguigu4.util.JDBCUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/*
    commons-dbutils 是Apache组织提供的一个开源JDBC工具类库，封装了针对数据库的增删改查操作
 */
public class QueryRunnerTest {

    //使用QueryRunner进行插入操作
    @Test
    public void testInsert() throws Exception {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();   //用Druid连接池技术获取连接
            String sql = "insert into customers(name,email,birth) values(?,?,?)";
            int insertCount = runner.update(conn, sql, "caixukun", "caixukun@qq.com", "1978-03-23");
            System.out.println("添加了"+insertCount+"条记录");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JDBCUtils.closeResourse(conn,null);
    }

    //使用QueryRunner进行查询操作（一条记录）
    @Test
    public void testQuery1(){
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();
            String sql = "select id,name,email,birth from customers where id =?";
            BeanHandler<Customer> handler = new BeanHandler<>(Customer.class);     //BeanHandler:是ResultSetHandler接口的实现类，用于封装表中的一条记录
            Customer customer = runner.query(conn, sql, handler, 16);
            System.out.println(customer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,null);
        }

    }


    //使用QueryRunner进行查询操作（多条记录）
    @Test
    public void testQuery2(){
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();
            String sql = "select id,name,email,birth from customers where id <?";
            BeanListHandler<Customer> handler = new BeanListHandler<>(Customer.class);    //BeanListHandler:是ResultSetHandler接口的实现类，用于封装表中的多条记录
            List<Customer> list = runner.query(conn, sql, handler, 16);
            list.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,null);
        }

    }

    //使用QueryRunner进行查询操作（一条记录） (使用MapHandler)
    //将字段及相应字段的值作为map中的key和value
    @Test
    public void testQuery3(){
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();
            String sql = "select id,name,email,birth from customers where id =?";
            MapHandler handler = new MapHandler();        //MapHandler:是ResultSetHandler接口的实现类，用于封装表中的一条记录 ; 将字段及相应字段的值作为Map中的key和value
            Map<String, Object> map = runner.query(conn, sql, handler, 16);
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,null);
        }

    }


    //ScalarHandler:用于查询特殊值
    @Test
    public void testQuery5(){
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();
            String sql = "select count(*) from customers";
            ScalarHandler handler = new ScalarHandler();
            Long count = (Long) runner.query(conn, sql, handler);
            System.out.println(count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,null);
        }

    }

    //自定义ResultSetHandler的实现类
    @Test
    public void testQuery7(){
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection3();
            String sql = "select id,name,email,birth from customers where id =?";

            //通过匿名实现类产生 ResultSetHandler对象
            ResultSetHandler<Customer> handler = new ResultSetHandler<Customer>() {

                @Override
                public Customer handle(ResultSet rs) throws SQLException {
                    if(rs.next()){
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        Date birth = rs.getDate("birth");
                        Customer customer = new Customer(id, name, email, birth);
                        return customer;
                    }
                    return null;
                }
            };
            Customer customer = runner.query(conn, sql, handler, 13);
            System.out.println(customer);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,null);
        }

    }



}
