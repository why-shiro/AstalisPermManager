package net.neostellar.astalisPermManager.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseProvider {
    void init();
    Connection getConnection() throws SQLException;
    void close();
}
