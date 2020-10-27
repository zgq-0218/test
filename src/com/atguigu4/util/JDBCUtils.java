package com.atguigu4.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JDBCUtils {


    /*
    使用C3P0的数据库连接池技术
     */
    private static ComboPooledDataSource cpds = new ComboPooledDataSource("hellc3p0");   //放在外面，得到一个池子就够

    public static Connection getConnection1() throws SQLException {

        Connection conn = cpds.getConnection();
        return conn;
    }




    /*
     //使用dbcp的数据库连接池技术
     */

    //使用静态代码块的方式执行一次
    private static DataSource source;
    static{
        try {
            Properties pros = new Properties();
            //获得流方式一
//        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");
            //获得流方式二
            FileInputStream is = new FileInputStream(new File("src/dbcp.properties"));
            pros.load(is);
            source = BasicDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection2() throws Exception {

        Connection conn = source.getConnection();
        return conn;

    }




    /*
        使用Druid的数据库连接池技术
     */

    private static DataSource source1;
    static {
        try {
            Properties pros = new Properties();
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
            pros.load(is);
            source1 = DruidDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection3() throws Exception {

        Connection conn = source1.getConnection();
        return conn;
    }




















    /*
           获取数据库的连接（普通获得连接）
   */
    public static Connection getConnection() throws Exception {
        //1.读取配置文件中的四个基本信息
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");

        Properties pros = new Properties();
        pros.load(is);
        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //2.加载驱动
        Class.forName(driverClass);

        //3.获取连接
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
    }

    /*
       关闭连接和Statement的操作
    */
    public static void closeResourse(Connection conn, Statement ps){
        try {
            if(ps != null)
                ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            if(conn !=null)
                conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /*
        关闭资源操作（加查询的resultset）
     */
    public static void closeResourse(Connection conn, Statement ps, ResultSet rs){
        try {
            if(ps != null)
                ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            if(conn !=null)
                conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            if(rs !=null)
                rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /*
       使用dbutils.jar中提供的DbUtils工具类，实现关闭资源操作（加查询的resultset）
    */
    public static void closeResourse1(Connection conn, Statement ps, ResultSet rs){
        //方式一：使用DbUtils.close()
//        try {
//            DbUtils.close(conn);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        try {
//            DbUtils.close(ps);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        try {
//            DbUtils.close(rs);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }

        //方式二：使用DbUtils.closeQuietly()
        DbUtils.closeQuietly(conn);
        DbUtils.closeQuietly(ps);
        DbUtils.closeQuietly(rs);




    }


}
