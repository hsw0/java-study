package io.syscall.commons.module.persistence.jdbc.support;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.AbstractDataSource;

/**
 * 사용할 수 없는 {@link DataSource}
 */
public class InvalidDataSource extends AbstractDataSource {

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection("", "");
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLException("InvalidDataSource", "08003");
    }
}
