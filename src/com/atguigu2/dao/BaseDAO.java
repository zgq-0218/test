package com.atguigu2.dao;

import com.atguigu1.util.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
    封装了针对于数据表的通用的操作
 */
public abstract class BaseDAO {

    //通用的增删改操作    ---version2.0 （考虑事务）
    public int update(Connection conn, String sql, Object ...args) {     //sql中占位符的个数与可变参数的长度相同

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


    //通用查询操作,返回多条记录
    public <T> List<T> getForList(Connection conn,Class<T> clazz, String sql, Object ...args){
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

            //创建集合对象
            ArrayList<T> list = new ArrayList<T>();
            while (rs.next()){
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
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.关闭资源
            JDBCUtils.closeResourse(null,ps,rs);
        }

        return null ;
    }


    //用于查询特殊值的通用方法
    public <E> E getValue(Connection conn,String sql,Object ...args){
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0 ; i < args.length ; i++){
                ps.setObject(i+1,args[i]);
            }
            rs = ps.executeQuery();
            if (rs.next()){
                return (E) rs.getObject(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.closeResourse(null,ps,rs);
        }
        return null;

    }

}
