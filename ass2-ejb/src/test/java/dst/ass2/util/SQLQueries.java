package dst.ass2.util;

public class SQLQueries {

    // --- GRID ---
    protected final static String SQL_GET_ALL_GRID_IDS = "SELECT id FROM Grid order by id ASC";
    protected final static String SQL_GET_ALL_PAID_JOBS_FOR_USER = "SELECT id, isPaid FROM Job j "
            + "WHERE j.user_id = (SELECT id FROM User WHERE username=?) AND isPaid = 1 order by id ASC";

    // --- PRICE ---
    protected final static String SQL_PRICE_TABLE_NAME = "Price";
    protected final static String SQL_IS_PRICE_AVAILABLE = "SELECT COUNT(*) FROM Price WHERE nrOfHistoricalJobs = ? AND price = ?";
    protected final static String SQL_COUNT_PRICES = "SELECT COUNT(*) FROM Price";

    // --- JOB ---
    protected final static String SQL_JOB_TABLE_NAME = "Job";
    protected final static String SQL_COUNT_JOBS = "SELECT COUNT(*) FROM Job";
    protected final static String SQL_GET_ALL_JOBS = "SELECT j.id, env.workflow, ex.start, u.username FROM Job j, Environment env, Execution ex, User u "
            + "WHERE j.environment_id = env.id AND j.execution_id = ex.id AND j.user_id = u.id";
    protected final static String SQL_GET_PARAMS_FOR_JOB = "SELECT ep.params  FROM Job j , Environment e, Environment_params ep "
            + "WHERE j.id = ? and e.id = j.environment_id and e.id=ep.Environment_id";

    // --- EXECUTION ---
    protected final static String SQL_COUNT_FINISHED_EXECUTION = "SELECT COUNT(*) FROM Execution e "
            + "WHERE e.start IS NOT NULL AND e.end IS NOT NULL AND e.status = 'FINISHED'";

    // --- ADMIN ---
    protected final static String SQL_COUNT_ADMIN_1 = "SELECT COUNT(*) FROM Admin a, Person p "
            + "WHERE a.id = p.id AND p.city = ? AND p.street = ? AND p.zipCode = ? AND p.firstName = ? AND p.lastname = ?";
    protected final static String SQL_COUNT_ADMIN_2 = "SELECT COUNT(*) FROM Admin "
            + "WHERE city = ? AND street = ? AND zipCode = ? AND firstName = ? AND lastname = ?";

    // --- Cluster ---
    protected final static String SQL_COUNT_Cluster = "SELECT COUNT(*) FROM Cluster "
            + "WHERE lastService IS NOT NULL AND nextService IS NOT NULL AND name = ?";

    // --- Computer ---
    protected final static String SQL_COUNT_COMPUTER = "SELECT COUNT(*) from Computer "
            + "WHERE cpus = ? AND creation IS NOT NULL AND lastUpdate IS NOT NULL AND location = ? AND name = ?";

    // --- Environment ---
    protected final static String SQL_COUNT_ENVIRONMENT = "SELECT COUNT(*) FROM Environment WHERE workflow = ?";

    // --- Execution ---
    protected final static String SQL_COUNT_EXECUTION = "SELECT COUNT(*) FROM Execution WHERE start IS NOT NULL";

    // --- Grid ---
    protected final static String SQL_COUNT_GRID = "SELECT COUNT(*) FROM Grid WHERE costsPerCPUMinute = ? AND location = ? AND name = ?";

    // --- Job ---
    protected final static String SQL_COUNT_JOB = "SELECT COUNT(*) FROM Job WHERE isPaid = ?";

    // --- Membership ---
    protected final static String SQL_COUNT_MEMBERSHIP = "SELECT COUNT(*) FROM Membership "
            + "WHERE discount = ? AND registration IS NOT NULL";

    // --- User ---
    protected final static String SQL_COUNT_USER_1 = "SELECT COUNT(*) FROM User u, Person p "
            + "WHERE u.id = p.id AND p.city = ? AND p.street = ? AND p.zipCode = ? AND p.firstName = ? "
            + "AND p.lastName = ? AND u.accountNo = ? AND u.bankCode = ? AND u.username = ? AND u.password = ?";

    protected final static String SQL_COUNT_USER_2 = "SELECT COUNT(*) FROM User "
            + "WHERE city = ? AND street = ? AND zipCode = ? AND firstName = ? "
            + "AND lastName = ? AND accountNo = ? AND bankCode = ? AND username = ? AND password = ?";

}
