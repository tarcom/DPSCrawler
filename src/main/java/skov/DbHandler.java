package skov;

import java.sql.*;

public class DbHandler {

    static Connection conn = null;
    static PreparedStatement prepareStat = null;

    public DbHandler() {
        makeJDBCConnection();
    }

    public static void main(String[] argv) throws Exception {
        try {
            DbHandler dbHandler = new DbHandler();

            //addDataToDB("p1-test", "dom-test", "cluster-test", "p0 - Last check 11.06.2021 16:41:36", 1);

            getDataFromDB();

            conn.close(); // connection close

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void makeJDBCConnection() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Congrats - Seems your MySQL JDBC Driver Registered!");
        } catch (ClassNotFoundException e) {
            System.out.println("Sorry, couldn't found JDBC driver. Make sure you have added JDBC Maven Dependency Correctly");
            e.printStackTrace();
            return;
        }

        try {
            // DriverManager: The basic service for managing a set of JDBC drivers.
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dpscrawler", "root", "");
            if (conn != null) {
                System.out.println("Connection Successful! Enjoy. Now it's time to push data");
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            System.out.println("MySQL Connection Failed!");
            e.printStackTrace();
            return;
        }
    }

    public static void addDataToDB(String env, String domain, String cluster, String lastcheck, int status, Timestamp timestamp) throws Exception {
        try {
            String insertQueryStatement = "INSERT INTO dpscrawler (env, domain, cluster, lastcheck, status, timestamp) VALUES  (?,?,?,?,?,?)";

            //System.out.println("preparing statement: " + insertQueryStatement);
            //System.out.println("env=" + env + ", domain=" + domain + ", cluster=" + cluster + ", lastcheck=" + lastcheck + ", status=" + status);

            prepareStat = conn.prepareStatement(insertQueryStatement);
            prepareStat.setString(1, env);
            prepareStat.setString(2, domain);
            prepareStat.setString(3, cluster);
            prepareStat.setString(4, lastcheck);
            prepareStat.setInt(5, status);
            prepareStat.setTimestamp(6, timestamp);

            // execute insert SQL statement
            prepareStat.executeUpdate();
            //System.out.println(env + " added successfully");
            prepareStat.close();

        } catch (SQLException e) {
            System.out.println(e);
            //  e.printStackTrace();
            throw e;
        }
    }

    public void cleanupDb() throws Exception {
        String sql = "delete from dpscrawler where timestamp IN (\n" +
                "    select timestamp from (\n" +
                "        SELECT timestamp, count(*) as count\n" +
                "        FROM `dpscrawler`\n" +
                "        group by timestamp\n" +
                "        order by timestamp\n" +
                "    ) taadaa1\n" +
                "    where count != 26\n" +
                ")";

        prepareStat = conn.prepareStatement(sql);
        int i = prepareStat.executeUpdate();
        System.out.println("Cleanup deleted # records=" + i);
        //System.out.println(env + " added successfully");
        prepareStat.close();
    }

    public static void getDataFromDB() throws Exception {
        try {
            // MySQL Select Query Tutorial
            String getQueryStatement = "SELECT * FROM dpscrawler";

            prepareStat = conn.prepareStatement(getQueryStatement);

            // Execute the Query, and get a java ResultSet
            ResultSet rs = prepareStat.executeQuery();

            // Let's iterate through the java ResultSet
            while (rs.next()) {
                String timestamp = rs.getString("timestamp");
                String env = rs.getString("env");
                String domain = rs.getString("domain");
                String cluster = rs.getString("cluster");
                String lastcheck = rs.getString("lastcheck");
                int status = rs.getInt("status");

                // Simply Print the results
                System.out.format("%s, %s, %s, %s, %s, %s\n", timestamp, env, domain, cluster, lastcheck, status);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

    }

}