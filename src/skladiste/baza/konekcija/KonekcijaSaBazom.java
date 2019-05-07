/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skladiste.baza.konekcija;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import util.Podesavanja;

/**
 *
 * @author user
 */
public class KonekcijaSaBazom {
    private final Connection connection;
    private static KonekcijaSaBazom instance;

    private KonekcijaSaBazom() throws SQLException {
        String url = Podesavanja.getInstance().getProperty("url");
        String dbuser = Podesavanja.getInstance().getProperty("user");
        String dbpassword = Podesavanja.getInstance().getProperty("password");
        connection = DriverManager.getConnection(url, dbuser, dbpassword);
        connection.setAutoCommit(false);
    }

    public static KonekcijaSaBazom getInstance() throws SQLException {
        if (instance == null) {
            instance = new KonekcijaSaBazom();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }
}
