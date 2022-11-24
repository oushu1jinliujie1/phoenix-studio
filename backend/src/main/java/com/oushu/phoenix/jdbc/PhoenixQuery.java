package com.oushu.phoenix.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.gson.JsonObject;
import com.oushu.config.Studio;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PhoenixQuery {

    @Autowired
    private Studio studio;

    public DruidDataSource pds;

    public void queryTestJdbc(Integer qID){
        long start = System.currentTimeMillis();
        String sql = "SELECT * FROM \"EMPLOYEE\" where \"REGION\" = ?";
        Map parBoundMap = new HashMap();
        parBoundMap.put(1, qID.toString());
        JsonObject rsJson = executeQuery(sql,parBoundMap);
        long end = System.currentTimeMillis();
        System.out.println(String.format("%s Elapsed: %d ms",rsJson,end - start));
    }

    // 获取单行结果
    public int execute(String sql, Map<Integer,Object> parBoundMap){
        long startTime = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int result = 0;
        try {
            conn = pds.getConnection();
            pstmt = conn.prepareStatement(sql);

            for (Map.Entry<Integer,Object> bound:parBoundMap.entrySet()){

                if (bound.getValue() instanceof Integer) {
                    pstmt.setInt(bound.getKey(),((Integer) bound.getValue()).intValue());
                } else {
                    pstmt.setString(bound.getKey(),(String)bound.getValue());
                }

            }
            result = pstmt.executeUpdate();

        }catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            closeRes(conn, pstmt, rs);
        }
        log.warn("执行时间： " + (System.currentTimeMillis()-startTime)
                + ", sql: " + sql
                + "; param: " + parBoundMap
                + "; result: " + result);
        return result;
    }

    // 获取单行结果
    public JsonObject executeQuery(String sql, Map<Integer,Object> parBoundMap){
        long startTime = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        JsonObject elements = null;
        String columnName, columnValue = null;
        try {
            conn = pds.getConnection();
            pstmt = conn.prepareStatement(sql);

            for (Map.Entry<Integer,Object> bound:parBoundMap.entrySet()){

                if (bound.getValue() instanceof Integer) {
                    pstmt.setInt(bound.getKey(),((Integer) bound.getValue()).intValue());
                } else {
                    pstmt.setString(bound.getKey(),(String)bound.getValue());
                }

            }
            rs = pstmt.executeQuery();
            rsmd = rs.getMetaData();

            while (rs.next()) {
                elements = new JsonObject();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    columnName = rsmd.getColumnName(i + 1);
                    columnValue = rs.getString(columnName);
                    elements.addProperty(columnName, columnValue);
                }
            }

        }catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            closeRes(conn, pstmt, rs);
        }
        log.warn("执行时间： " + (System.currentTimeMillis()-startTime) + ", sql: " + sql + "; param: " + parBoundMap);
        return elements;
    }

    // 获取多行结果
    public List<JsonObject> getList(String sql, Map<Integer,Object> parBoundMap){
        long startTime = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        List<JsonObject> result = new ArrayList<>();
        String columnName, columnValue = null;
        try {
            conn = pds.getConnection();
            pstmt = conn.prepareStatement(sql);

            for (Map.Entry<Integer,Object> bound:parBoundMap.entrySet()){

                if (bound.getValue() instanceof Integer) {
                    pstmt.setInt(bound.getKey(),((Integer) bound.getValue()).intValue());
                } else {
                    pstmt.setString(bound.getKey(),(String)bound.getValue());
                }

            }

            rs = pstmt.executeQuery();
            rsmd = rs.getMetaData();

            while (rs.next()) {
                JsonObject elements = new JsonObject();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    columnName = rsmd.getColumnName(i + 1);
                    columnValue = rs.getString(columnName);
                    elements.addProperty(columnName, columnValue);
                }
                result.add(elements);
            }

        }catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            closeRes(conn, pstmt, rs);
        }
        log.warn("执行时间： " + (System.currentTimeMillis()-startTime) + ", sql: " + sql + "; param: " + parBoundMap);
        return result;
    }

    // 获取多行结果
    public List<Map<String, Object>> getListMap(String sql, Map<Integer,Object> parBoundMap){
        long startTime = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        List<Map<String, Object>> result = new ArrayList<>();
        String columnName = null;
        Object columnValue = null;
        try {
            conn = pds.getConnection();
            pstmt = conn.prepareStatement(sql);
            for (Map.Entry<Integer,Object> bound:parBoundMap.entrySet()){
                if (bound.getValue() instanceof Integer) {
                    pstmt.setInt(bound.getKey(),((Integer) bound.getValue()).intValue());
                } else {
                    pstmt.setString(bound.getKey(),(String)bound.getValue());
                }
            }
            rs = pstmt.executeQuery();
            rsmd = rs.getMetaData();

            while (rs.next()) {
                Map<String, Object> elements = new HashMap<>();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    columnName = rsmd.getColumnName(i + 1);
                    columnValue = rs.getObject(columnName);
                    elements.put(columnName, columnValue);
                }
                result.add(elements);
            }

        }catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            closeRes(conn, pstmt, rs);
        }
        log.warn("执行时间： " + (System.currentTimeMillis()-startTime) + ", sql: " + sql + "; param: " + parBoundMap);
        return result;
    }

    /**
     * 关闭资源
     *
     * @param conn
     * @param statement
     * @param rs
     */
    public void closeRes(Connection conn, Statement statement, ResultSet rs) {
        try {
            if (conn != null) {
                conn.close();
            }
            if (statement != null)
                statement.close();
            if (rs != null)
                rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
