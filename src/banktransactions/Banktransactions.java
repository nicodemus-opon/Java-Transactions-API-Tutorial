package banktransactions;

import db.MyXid;

import java.sql.*;
import javax.sql.*;
import java.util.*;
import javax.transaction.*;
import javax.transaction.xa.*;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXid;
import db.datasource;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Banktransactions {

    MysqlXADataSource xaDs;
    XAConnection xaCon;
    XAResource xaRes;
    Connection con;

    public void runRollBack() throws SQLException, XAException {
        xaDs = datasource.gettDataSource();
        xaCon = xaDs.getXAConnection();
        xaRes = xaCon.getXAResource();
        con = xaCon.getConnection();

        // For XA transactions, a transaction identifier is required.
        // An implementation of the XID interface is not included with the 
        Xid xid = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});

        // The connection from the XAResource can be used as any other 
        // JDBC connection.
        Statement stmt = con.createStatement();
        try {
            xaRes.start(xid, XAResource.TMNOFLAGS);
        } catch (XAException ex) {
            Logger.getLogger(Banktransactions.class.getName()).log(Level.SEVERE, null, ex);
        }
        stmt.executeUpdate("INSERT INTO account values('bg','b','3','d','e','f','g')");
        xaRes.end(xid, XAResource.TMSUCCESS);
        int ret = xaRes.prepare(xid);
        if (ret == XAResource.XA_OK) {
            xaRes.rollback(xid);
        }
    }


    public static void main(java.lang.String[] args) throws SQLException, XAException {
        Banktransactions test = new Banktransactions();

        test.runCommit();
        //test.runRollBack();
    }

    /**
     * This test uses JTA support to handle transactions.
     */
    public void runCommit() {
        Connection c = null;

        try {
            xaDs = datasource.gettDataSource();
            xaCon = xaDs.getXAConnection();
            xaRes = xaCon.getXAResource();
            con = xaCon.getConnection();

            // For XA transactions, a transaction identifier is required.
            // An implementation of the XID interface is not included with the 
            Xid xid = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});

            // The connection from the XAResource can be used as any other 
            // JDBC connection.
            Statement stmt = con.createStatement();

            // The XA resource must be notified before starting any 
            // transactional work.
            xaRes.start(xid, XAResource.TMNOFLAGS);

            // Standard JDBC work is performed.
            int count = stmt.executeUpdate("INSERT INTO account values('a','b','3','d','e','f','g')");

            // When the transaction work has completed, the XA resource must 
            // again be notified.
            xaRes.end(xid, XAResource.TMSUCCESS);

            // The transaction represented by the transaction ID is prepared
            // to be committed.
            int rc = xaRes.prepare(xid);

            // The transaction is committed through the XAResource.
            // The JDBC Connection object is not used to commit
            // the transaction when using JTA.
            xaRes.commit(xid, false);

        } catch (Exception e) {
            System.out.println("Something has gone wrong.");
            e.printStackTrace();
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                System.out.println("Note:  Cleaup exception.");
                e.printStackTrace();
            }
        }
    }
}
