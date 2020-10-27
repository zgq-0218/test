package com.atguigu1.transaction;

import com.atguigu1.util.JDBCUtils;
import jdk.nashorn.internal.scripts.JD;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;


/*
    1.什么叫数据库事务
    事务：一组逻辑操作单元，使数据从一种状态变换到另一种状态
        >一组逻辑操作单元：一个或多个DML（数据库操作语言：增删改查）操作
    2.事务处理原则：保证所有事务都作为一个工作单元来执行，即使出现了故障，都不能改变这种执行方式。
                  当在一个事务中执行多个操作时，要么所有的事务都被提交(commit)，那么这些修改就永久地保存下来；
                  要么数据库管理系统将放弃所作的所有修改，整个事务回滚(rollback)到最初状态。
    3.数据一旦提交，就不可回滚
    4.哪些操作会导致数据的自动提交
       <1>DDL（数据库定义语言：CREATE、ALTER、DROP等）操作一旦执行，都会自动提交
            >set autocommit = false 对DDL无效
       <2>DML(数据库操作语言：增删改查)操作 在默认情况下，一旦执行就会自动提交
            >但我们可以通过set autocommit = false 的方式取消DML操作的自动提交
       <3>默认在关闭连接时，会自动提交

 */
public class TransactionTest {

    /*
        针对user_table表
        AA用户给BB用户转账100
        update user_table set balance = balance - 100 where user ='AA'
        update user_table set balance = balance + 100 where user ='BB'

     */


    // ************未考虑数据库事务情况下的转账(改)操作*************
    @Test
    public void testUpdate(){
        String sql1 = "update user_table set balance = balance - 100 where user =?";
        update(sql1,"AA");

        //模拟网络异常
//        System.out.println(10/0);

        String sql2 = "update user_table set balance = balance + 100 where user =?";
        update(sql2,"BB");

        System.out.println("转账成功");

    }

    //通用的增删改操作    ---version1.0
    public int update(String sql,Object ...args) {     //sql中占位符的个数与可变参数的长度相同
        Connection conn =null;
        PreparedStatement ps = null;
        try {
            //1.获取数据库连接
            conn = JDBCUtils.getConnection();
            //2.预编译sql语句，返回Preparement的实例
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            for (int i = 0 ; i < args.length ; i++){         //占位符不知道有几个，所以用循环
                ps.setObject(i+1,args[i]);     //注意：sql中的索引从1开始，java中的数组args索引从0开始
            }
            //4.执行
            return ps.executeUpdate();     //返回影响了几行数据
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.资源的关闭
            JDBCUtils.closeResourse(conn,ps);
        }
        return 0;
    }



    //***************考虑数据库事务的转账（改）操作*******************
    @Test
    public void testUpdateWithTx(){
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();

            //取消数据的自动提交
            conn.setAutoCommit(false);

            String sql1 = "update user_table set balance = balance - 100 where user =?";
            update(conn,sql1,"AA");

            //模拟网络异常
            System.out.println(10/0);

            String sql2 = "update user_table set balance = balance + 100 where user =?";
            update(conn,sql2,"BB");

            System.out.println("转账成功");

            //提交数据
            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            //回滚数据
            try {
                conn.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            JDBCUtils.closeResourse(conn,null);
        }

    }

    //通用的增删改操作    ---version2.0 （考虑事务）
    public int update(Connection conn,String sql,Object ...args) {     //sql中占位符的个数与可变参数的长度相同

        PreparedStatement ps = null;
        try {

            //1.预编译sql语句，返回Preparement的实例
            ps = conn.prepareStatement(sql);
            //2.填充占位符
            for (int i = 0 ; i < args.length ; i++){         //占位符不知道有几个，所以用循环
                ps.setObject(i+1,args[i]);     //注意：sql中的索引从1开始，java中的数组args索引从0开始
            }
            //3.执行
            return ps.executeUpdate();     //返回影响了几行数据
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //4.资源的关闭
            JDBCUtils.closeResourse(null,ps);
        }
        return 0;
    }


    //**************************考虑事务的查询操作*******************************
    //查询操作
    @Test
    public void testTransactionSelect() throws Exception {
        Connection conn = JDBCUtils.getConnection();
        //获取当前事务的隔离级别
        System.out.println(conn.getTransactionIsolation());    //默认为4：读已提交
//        设置数据库的隔离级别
//        conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

        //取消自动提交
        conn.setAutoCommit(false);
        String sql = "select user,password,balance from user_table where user = ?";
        User user = getInstance(conn, User.class, sql, "CC");
        System.out.println(user);
    }
    //修改操作
    @Test
    public void testTransactionUpdate() throws Exception {
        Connection conn = JDBCUtils.getConnection();
        //获取当前事务的隔离级别
        System.out.println(conn.getTransactionIsolation());
        //设置数据库的隔离级别
        //conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

        //取消自动提交
        conn.setAutoCommit(false);
        String sql = "update user_table set balance = ? where user = ?";
        update(conn,sql,5000,"CC");
        Thread.sleep(15000);
        System.out.println("修改结束");

    }

    //通用的查询操作，返回一条记录 (version 2.0 ：考虑事务)
    public <T> T getInstance(Connection conn,Class<T> clazz,String sql,Object ...args){
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            //2.预编译sql语句，返回prepareStatement实例
            ps = conn.prepareStatement(sql);
            //3.填充占位符
            for(int i = 0 ;i < args.length ; i++){
                ps.setObject(i+1,args[i]);
            }
            //4.执行，并返回结果集
            rs = ps.executeQuery();
            //获取结果集的元数据:ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集中的数据
            int columnCount = rsmd.getColumnCount();  //通过元数据得到结果集的列数

            if(rs.next()){
                T t = clazz.getConstructor().newInstance();

//                T t = clazz.newInstance();   //利用反射构建对象

                //处理结果集一行数据中的每一列
                for(int i = 0 ;i < columnCount ; i++){
                    Object columnValue = rs.getObject(i + 1);     //获取结果集每个列的值
//                    String columnName = rsmd.getColumnName(i + 1);           //获取结果集每个列的列名 （不推荐使用）
                    String columnLabel = rsmd.getColumnLabel(i + 1);          //获取结果集每个列的别名（没有别名就是列名）
                    //给t对象指定的columnName属性，赋值为columnValue ：通过反射
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t,columnValue);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.关闭资源
            JDBCUtils.closeResourse(null,ps,rs);
        }

        return null ;
    }







}
