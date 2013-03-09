package dst.ass1.jpa.util;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

/**
 * ##########################
 * 
 * DO NOT CHANGE THIS CLASS!
 * 
 * ##########################
 * 
 * Contains various convenience methods for database access.
 * <p/>
 * <b>Note that the caller is responsible for dealing with possible exceptions as well as doing the connection
 * handling.</b><br/>
 * In other words, a connection will not be closed even if a fatal error occurs. However, other SQL resources i.e.,
 * {@link Statement Statements} and {@link ResultSet ResultSets} created within the methods, which are not returned to
 * the caller, are closed before the method returns.
 */
public final class JdbcHelper {
	private JdbcHelper() {
	}

	/**
	 * Checks if the named table can be accessed via the given {@link JdbcConnection}.
	 *
	 * @param tableName      the name of the table to find
	 * @param jdbcConnection the JDBC connection to use
	 * @return {@code true} if the database schema contains a table with the given name, {@code false} otherwise
	 * @throws SQLException if a database access error occurs
	 */
	public static boolean isTable(String tableName, JdbcConnection jdbcConnection) throws SQLException {
		String sql = "show tables";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String tbl = rs.getString(1);
				if (tbl.equalsIgnoreCase(tableName)) {
					return true;
				}
			}
		} finally {
			stmt.close();
		}

		return false;
	}

	/**
	 * Detects the type of table inheritance.
	 *
	 * @param jdbcConnection the JDBC connection to use
	 * @param tableName      the name of the table
	 * @return {@code 0} if the given table exists, {@code 1} otherwise.
	 * @throws SQLException
	 */
	public static int getInheritanceType(JdbcConnection jdbcConnection, String tableName) throws SQLException {
		return isTable(tableName, jdbcConnection) ? 0 : 1;
	}

	/**
	 * Checks whether a certain database table contains a column with the given name.
	 *
	 * @param tableName      the name of the table to check
	 * @param column         the name of the column to find
	 * @param jdbcConnection the JDBC connection to use
	 * @return {@code true} if the table contains the column, {@code false} otherwise
	 * @throws SQLException if a database access error occurs
	 */
	public static boolean isColumnInTable(String tableName, String column, JdbcConnection jdbcConnection) throws SQLException {
		String sql = "show columns FROM " + tableName + " WHERE Field='" + column + "'";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		try {
			return stmt.executeQuery(sql).next();
		} finally {
			stmt.close();
		}
	}

	/**
	 * Checks whether a certain table contains an index for the given column name.
	 *
	 * @param tableName      the name of the table to check
	 * @param indexName      the name of the column the index is created for
	 * @param nonUnique      {@code true} if the index is non unique, {@code false} otherwise
	 * @param jdbcConnection the JDBC connection to use
	 * @return {@code true} if the index exists, {@code false} otherwise
	 * @throws SQLException if a database access error occurs
	 */
	public static boolean isIndex(String tableName, String indexName, boolean nonUnique, JdbcConnection jdbcConnection) throws SQLException {
		String sql = String.format("show index FROM %s WHERE COLUMN_NAME='%s' AND non_unique='%s'", tableName, indexName, nonUnique ? "1" : "0");
		Statement stmt = jdbcConnection.getConnection().createStatement();
		try {
			return stmt.executeQuery(sql).next();
		} finally {
			stmt.close();
		}
	}

	/**
	 * Checks whether the given column of a certain table can contain {@code NULL} values.
	 *
	 * @param tableName      the name of the table to check
	 * @param columnName     the name of the column to check
	 * @param jdbcConnection the JDBC connection to use
	 * @return {@code true} if the column is nullable, {@code false} otherwise
	 * @throws SQLException if a database access error occurs
	 */
	public static boolean isNullable(String tableName, String columnName, JdbcConnection jdbcConnection) throws SQLException {
		DatabaseMetaData meta = jdbcConnection.getConnection().getMetaData();
		ResultSet rs = meta.getColumns(null, null, tableName, columnName);
		try {
			rs.next();
			return rs.getBoolean("NULLABLE");
		} finally {
			rs.close();
		}
	}
	
	/**
	 * Deletes all data from all tables that can be accessed via the given {@link JdbcConnection}.
	 *
	 * @param jdbcConnection the JDBC connection to use
	 * @throws Exception if a database access error occurs
	 */
	public static void clearExistingTables(JdbcConnection jdbcConnection) throws Exception {
		Statement stmt = jdbcConnection.getConnection().createStatement();
		String sql = "show tables";
		ResultSet rs = stmt.executeQuery(sql);
		ArrayList<String> tables = new ArrayList<String>();
		while (rs.next()) {
			tables.add(rs.getString(1));
		}
		rs.close();


		while (true) {
			int prev = tables.size();

			if (tables.isEmpty()) {
				break;
			}

			Iterator<String> it = tables.iterator();
			while (it.hasNext()) {
				String tableName = it.next();
				try {
					jdbcConnection.getConnection().createStatement().executeUpdate("DELETE FROM " + tableName);
					it.remove();
				} catch (MySQLIntegrityConstraintViolationException e) {
					// ok
				} catch (Exception e) {
					throw new Exception("I am unable to delete tables. This test cannot be performed correctly. Error is " + e.getMessage());
				}
			}

			if (tables.size() == prev) {
				throw new Exception("The test cannot be performed because tables cannot be cleared.");
			}
		}

	}

	/**
	 * Deletes all data from all tables that can be accessed via the given {@link JdbcConnection}.
	 *
	 * @param jdbcConnection the JDBC connection to use
	 * @throws Exception if a database access error occurs
	 */
	public static void cleanTables(JdbcConnection jdbcConnection)
			throws Exception {
		List<String> tables = getTables(jdbcConnection);

		Statement stmt = jdbcConnection.getConnection().createStatement();
		stmt.addBatch("SET FOREIGN_KEY_CHECKS=0");
		for (String table : tables) {
			if (table.toLowerCase().startsWith("hibernate"))
				continue;
			stmt.addBatch("truncate table " + table);
		}
		stmt.addBatch("SET FOREIGN_KEY_CHECKS=1");
		stmt.executeBatch();

		stmt.close();
	}
	
	/**
	 * Returns a list of all table-names for the given database/connection
	 * 
	 * @param jdbcConnection the JDBC connection to use
	 * @return List of table names
	 * @throws Exception if a database access error occurs
	 */
	public static List<String> getTables(JdbcConnection jdbcConnection)
			throws Exception {
		Statement stmt = jdbcConnection.getConnection().createStatement();
		String sql = "show tables";
		ResultSet rs = stmt.executeQuery(sql);
		ArrayList<String> tables = new ArrayList<String>();
		while (rs.next()) {
			tables.add(rs.getString(1));
		}
		rs.close();

		return tables;
	}

	/**
	 * Finishes all {@link dst.ass1.jpa.model.Job Job} currently executed or scheduled for execution.
	 *
	 * @param jdbcConnection the JDBC connection to use
	 * @throws SQLException if a database access error occurs
	 */
	public static void finishAllJobs(JdbcConnection jdbcConnection) throws SQLException {
		String sql = "UPDATE Execution SET END=now() , status='FINISHED'";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		try {
			stmt.executeUpdate(sql);
		} finally {
			stmt.close();
		}
	}

	/**
	 * Returns the amount of CPUs currently available in the given {@link dst.ass1.jpa.model.Grid Grid} for being
	 * assigned to {@link dst.ass1.jpa.model.Job Jobs}.
	 *
	 * @param jdbcConnection the JDBC connection to use
	 * @param gridId         the identifier of the requested grid
	 * @return the number of available CPUs for the given grid
	 * @throws SQLException if a database access error occurs
	 */
	public static int getAvailableCpuCount(JdbcConnection jdbcConnection, String gridId) throws SQLException {
		int result = 0;
		String sql = "SELECT SUM(cpus) FROM Computer WHERE cluster_id=(SELECT id FROM Cluster WHERE grid_id=" + gridId + " LIMIT 0,1)";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} finally {
			stmt.close();
		}

		return result;

	}

	/**
	 * Returns the identifiers of the {@link dst.ass1.jpa.model.Grid Grids}.
	 *
	 * @param jdbcConnection the JDBC connection to use
	 * @return the grid identifiers
	 * @throws SQLException if a database access error occurs
	 */
	public static List<Long> getGridIds(JdbcConnection jdbcConnection) throws SQLException {
		List<Long> gridIds = new ArrayList<Long>();
		String sql = "SELECT id FROM Grid";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				gridIds.add(rs.getLong(1));
			}
		} finally {
			stmt.close();
		}
		return gridIds;
	}
}
