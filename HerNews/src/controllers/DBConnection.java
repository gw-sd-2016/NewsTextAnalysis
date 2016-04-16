package controllers;

import models.Nonprofit;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ellenlouie on 2/26/16.
 */
public class DBConnection {

    public List getNonprofits(List<String> listOfArticleKeywords, List<String> listOfArticleLocations) {
        final String jdbc_driver = "com.mysql.jdbc.Driver";
        final String db_url = "jdbc:mysql://localhost/HerNews";

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
            String sql = createSqlQuery(listOfArticleKeywords, listOfArticleLocations);
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

    private String createSqlQuery(List<String> keywordList, List<String> locationList) {
        String keywords = formatSqlLists(keywordList);
        String locations = formatSqlLists(locationList);

        return "SELECT n.name, n.webpage " +
                "FROM Nonprofit n, Keyword k, Keyword_map m " +
                "WHERE k.kid = m.keyword_id " +
                "AND (k.word IN (" + keywords + ")) " +
                "AND n.nid = m.nonprofit_id " +
                "AND (n.location IN ('International', " + locations + ")) " +
                "GROUP BY n.nid";
    }

    private String formatSqlLists(List<String> list) {
        String sqlListOfKeywords = "";

        for(int i = 0; i < list.size(); i++) {
            if(i == list.size()-1) {
                sqlListOfKeywords += "'" + list.get(i) + "'";
            } else {
                sqlListOfKeywords += "'" + list.get(i) + "', ";
            }
        }
        return sqlListOfKeywords;
    }

    public List<Nonprofit> getNRandomNonprofits(List<Nonprofit> list, int n) {
        List<Nonprofit> copy = new LinkedList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, n);
    }
}
