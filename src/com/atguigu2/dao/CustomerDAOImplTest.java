package com.atguigu2.dao;

import com.atguigu4.util.JDBCUtils;
import com.atguigu2.bean.Customer;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public class CustomerDAOImplTest {
    CustomerDAOImpl dao = new CustomerDAOImpl();
    @Test
    public  void testinsert(){
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            Customer cust = new Customer(1, "消费", "xiaofei@qq.com", new Date(13223l));
            dao.insert(conn,cust);
            System.out.println("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,null);
        }

    }

    @Test
    public  void testDeleteById(){
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();

            dao.deleteById(conn,25);

            System.out.println("删除成功");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,null);
        }

    }

    @Test
    public  void testUpdateConnectionCustomer(){
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            Customer cust = new Customer(24, "beiduofen", "beiduofen@qq.com", new Date(324242L));
            dao.update(conn,cust);

            System.out.println("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,null);
        }

    }

    @Test
    public  void testGetCustomerById(){
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection3();
            Customer cust = dao.getCustomerById(conn, 4);
            System.out.println(cust);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,null);
        }

    }

    @Test
    public  void testGetAll(){
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            List<Customer> list = dao.getAll(conn);
            list.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,null);
        }

    }

    @Test
    public  void testGetCount(){
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            Long count = dao.getCount(conn);
            System.out.println(count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,null);
        }

    }

    @Test
    public  void testGetMaxBirth(){
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            java.util.Date maxBirth = dao.getMaxBirth(conn);
            System.out.println(maxBirth);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResourse(conn,null);
        }

    }


}
