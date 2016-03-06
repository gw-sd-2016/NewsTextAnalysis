package controllers;

import models.Nonprofit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ellenlouie on 2/26/16.
 */
public class DBConnection {

    public List getNonprofits(List<String> listOfArticleKeywords) {
        final String jdbc_driver = "com.mysql.jdbc.Driver";
        final String db_url = "jdbc:mysql://localhost/femfinder";

        final String user = "root";
        final String pass = "";

        Connection conn = null;
        Statement stmt = null;

        List<Nonprofit> nonprofits = new ArrayList<>();

        try {
            Class.forName(jdbc_driver);

            //connecting to database
            conn = DriverManager.getConnection(db_url, user, pass);

            //creating query statement
            stmt = conn.createStatement();
            String sql = createSqlQuery(listOfArticleKeywords);
            ResultSet rs = stmt.executeQuery(sql);

            //get query results
            if(rs != null) {
                while(rs.next()) {
                    Nonprofit nonprofit = new Nonprofit();
                    nonprofit.setName(rs.getString("name"));
                    nonprofit.setDonationLink(rs.getString("webpage"));
                    nonprofits.add(nonprofit);
                }
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(stmt != null) {
                    stmt.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            try {
                if(conn != null) {
                    conn.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return nonprofits;
    }

    public String createSqlQuery(List<String> keywordList) {
        String keyword1;
        String keyword2;
        String keyword3;
        String keyword4;
        String keyword5;
        String sql = null;

        if(keywordList.size() >= 5) {
            keyword1 = keywordList.get(0);
            keyword2 = keywordList.get(1);
            keyword3 = keywordList.get(2);
            keyword4 = keywordList.get(3);
            keyword5 = keywordList.get(4);

            sql = "SELECT n.name, n.webpage " +
                    "FROM Nonprofit n, Keyword k, Keyword_map m " +
                    "WHERE k.kid = m.keyword_id " +
                    "AND (k.word IN ('" + keyword1 + "', '" + keyword2 + "', '" + keyword3 + "', '" + keyword4 + "', '" + keyword5 + "')) " +
                    "AND n.nid = m.nonprofit_id";
        } else if(keywordList.size() == 4) {
            keyword1 = keywordList.get(0);
            keyword2 = keywordList.get(1);
            keyword3 = keywordList.get(2);
            keyword4 = keywordList.get(3);

            sql = "SELECT n.name, n.webpage " +
                    "FROM Nonprofit n, Keyword k, Keyword_map m " +
                    "WHERE k.kid = m.keyword_id " +
                    "AND (k.word IN ('" + keyword1 + "', '" + keyword2 + "', '" + keyword3 + "', '" + keyword4 + "')) " +
                    "AND n.nid = m.nonprofit_id";
        } else if(keywordList.size() == 3) {
            keyword1 = keywordList.get(0);
            keyword2 = keywordList.get(1);
            keyword3 = keywordList.get(2);

            sql = "SELECT n.name, n.webpage " +
                    "FROM Nonprofit n, Keyword k, Keyword_map m " +
                    "WHERE k.kid = m.keyword_id " +
                    "AND (k.word IN ('" + keyword1 + "', '" + keyword2 + "', '" + keyword3 + "')) " +
                    "AND n.nid = m.nonprofit_id";
        } else if(keywordList.size() == 2) {
            keyword1 = keywordList.get(0);
            keyword2 = keywordList.get(1);

            sql = "SELECT n.name, n.webpage " +
                    "FROM Nonprofit n, Keyword k, Keyword_map m " +
                    "WHERE k.kid = m.keyword_id " +
                    "AND (k.word IN ('" + keyword1 + "', '" + keyword2 + "')) " +
                    "AND n.nid = m.nonprofit_id";
        } else if(keywordList.size() == 1) {
            keyword1 = keywordList.get(0);

            sql = "SELECT n.name, n.webpage " +
                    "FROM Nonprofit n, Keyword k, Keyword_map m " +
                    "WHERE k.kid = m.keyword_id " +
                    "AND (k.word IN ('" + keyword1 + "')) " +
                    "AND n.nid = m.nonprofit_id";
        }
        return sql;
    }
}
