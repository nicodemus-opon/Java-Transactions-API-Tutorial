/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

/**
 *
 * @author nico
 */
public class datasource {

    public static MysqlXADataSource gettDataSource() {
        MysqlXADataSource xaDs;
        XAConnection xaCon;
        XAResource xaRes;
        Connection con;
        
        xaDs = new MysqlXADataSource();
        xaDs.setUrl("jdbc:mysql://localhost:3306/bankapp");
        xaDs.setUser("root");
        xaDs.setPassword("");
        xaDs.setDatabaseName("bankapp");
        return xaDs;
    }

}
