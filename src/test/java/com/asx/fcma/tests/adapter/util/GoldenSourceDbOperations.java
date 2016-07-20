package com.asx.fcma.tests.adapter.util;

import com.asx.fcma.tests.adapter.config.ConfigManager;

import java.sql.*;

/**
 * Created by auto_test on 25/01/2016.
 */
public class GoldenSourceDbOperations {
    public static void main(String args[]) throws ClassNotFoundException, SQLException, InterruptedException {
        String JDBC_DRIVER = "oracle.jdbc.OracleDriver";

        // Retrieving the Database url from the config file
        String db_url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=DOIP202)(PORT=60002)))(CONNECT_DATA=(SERVICE_NAME=FCMA)))";

        //  Retrieving the Database credentials from the environment file
        String userName = "ncoregc";
        String password= "fcmagc";

        Connection conn = null;
        Statement stmt = null;
        //STEP 2: Register JDBC driver
        Class.forName(JDBC_DRIVER);

        //STEP 3: Open a connection
        System.out.println("Connecting to GoldenSource database to insert and retrieve data from ...");
        conn = DriverManager.getConnection(db_url, userName, password);
        stmt = conn.createStatement();
        System.out.println("Connected to Golden Source DB");

        String sql = null;

        sql = "INSERT INTO FT_T_TRET (TRET_OID, XREF_TBL_ID, XREF_TBL_ROW_OID, TRN_ID, JOB_ID, RECORD_SEQ_NUM, CHG_IND, LAST_CHG_TMS, LAST_CHG_USR_ID)"+
        "SELECT NEW_OID, 'ISSU', instr_id, new_oid, null, null, 'U', sysdate, 'AUTOTEST'"+
        "from ft_t_isid where iss_id='ZAZ6' and id_ctxt_typ='ASX24CODE'";

        Thread.sleep(2000);
        stmt.setQueryTimeout(15);
        ResultSet rs = stmt.executeQuery(sql);
        stmt.close();
        conn.close();
    }

}
