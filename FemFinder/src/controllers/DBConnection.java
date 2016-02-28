package controllers;

import java.sql.*;

/**
 * Created by ellenlouie on 2/26/16.
 */
public class DBConnection {

    //TODO: correct query to get nonprofit info based on keywords compared to those from article
    public void getNonprofits() {
        final String jdbc_driver = "com.mysql.jdbc.Driver";
        final String db_url = "jdbc:mysql://localhost/femfinder";

        final String user = "root";
        final String pass = "";

        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName(jdbc_driver);

            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(db_url, user, pass);

            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql = "SELECT * FROM Nonprofit";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                int nid = rs.getInt("nid");
                String name = rs.getString("name");
                String webpage = rs.getString("webpage");

                System.out.println("nid: " + nid);
                System.out.println("name: " + name);
                System.out.println("donation webpage url: " + webpage);
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
    }
}
