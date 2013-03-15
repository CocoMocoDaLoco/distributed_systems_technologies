package dst.ass1.jpa.interceptor;

import java.util.regex.Pattern;

import org.hibernate.EmptyInterceptor;

public class SQLInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 3894614218727237142L;

    private static int selectCount;
    private static final Object gate = new Object();

    @Override
    public String onPrepareStatement(String sql) {
        if (Pattern.matches("select [ a-zA-Z0-9._,]+ from .*(Computer|Execution).*", sql)) {
            synchronized (gate) {
                selectCount++;
            }
        }

        return sql;
    }

    public static void resetCounter() {
        synchronized (gate) {
            selectCount = 0;
        }
    }


    public static int getSelectCount() {
        synchronized (gate) {
            return selectCount;
        }
    }
}
