package com.careerconnect.dao;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

class DBConnectionTest {
    @Test
    void connectionLookupAndCloseAreSafeWhenDatabaseIsUnavailable() {
        Connection connection = assertDoesNotThrow(DBConnection::getConnection);

        assertDoesNotThrow(() -> DBConnection.closeConnection(connection));
        assertDoesNotThrow(() -> DBConnection.closeConnection(null));
    }
}
