package dst.ass1.jpa.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * ##########################
 * 
 * DO NOT CHANGE THIS CLASS!
 * 
 * ##########################
 * 
 * Establishes a JDBC connection to the database.
 * <p/>
 * The connection settings are:
 * <ul>
 * <li>URL: jdbc:mysql://localhost:3306/dst</li>
 * <li>user: root</li>
 * <li>password: "" (an empty string)</li>
 * </ul>
 */
public class JdbcConnection {

	private final String SERVER_NAME = "localhost";
	private final String DBMS = "mysql";
	private final String PORT_NUMBER = "3306";
	private final Object USER_NAME = "root";
	private final Object PASSWORD = "";
	private final String SCHEMA = "dst";

	private Connection jdbcConnection = null;

	public JdbcConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot load MySQL JDBC driver", e);
		}
	}

	/**
	 * Attempts do close the current connection and create a new one with the same parameters.
	 *
	 * @return the new established connection
	 * @throws SQLException if a database access error occurs
	 * @see #disconnect()
	 * @see #connect()
	 */
	public Connection getConnection() throws SQLException {
		try {
			disconnect();
		} catch (Exception e) {
			// ignore any exception of a broken connection
		}
		connect();
		return jdbcConnection;
	}

	/**
	 * Establishes a new database connection.
	 *
	 * @throws SQLException if a database access error occurs
	 */
	private void connect() throws SQLException {
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.USER_NAME);
		connectionProps.put("password", this.PASSWORD);

		String url = String.format("jdbc:%s://%s:%s/%s", this.DBMS, this.SERVER_NAME, this.PORT_NUMBER, SCHEMA);
		jdbcConnection = DriverManager.getConnection(url, connectionProps);
	}

	/**
	 * Closes the current database connection if available.
	 *
	 * @throws SQLException if a database access error occurs
	 */
	public void disconnect() throws SQLException {
		if (null != jdbcConnection) {
			jdbcConnection.close();
			jdbcConnection = null;
		}
	}

}
