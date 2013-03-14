package dst.ass1.jpa.listener;

import java.util.Date;


public class DefaultListener {

    private static int activePersistCount;
    private static Date persistStart;
    private static long totalPersistTime;

    private static final Object gate = new Object();

    private static int totalPersistCount;
    private static int totalLoadCount;
    private static int totalUpdateCount;
    private static int totalRemoveCount;

    public void onPrePersist(Object object) {
        synchronized (gate) {
            activePersistCount++;
            if (activePersistCount == 1) {
                persistStart = new Date();
            }
        }
    }

    public void onPostPersist(Object object) {
        synchronized (gate) {
            totalPersistCount++;

            activePersistCount--;
            if (activePersistCount == 0) {
                long seconds = new Date().getTime() - persistStart.getTime();
                totalPersistTime += seconds;
            }
        }
    }

    public void onPostUpdate(Object object) {
        synchronized (gate) {
            totalUpdateCount++;
        }
    }

    public void onPostLoad(Object object) {
        synchronized (gate) {
            totalLoadCount++;
        }
    }

    public void onPostRemove(Object object) {
        synchronized (gate) {
            totalRemoveCount++;
        }
    }

    public static int getLoadOperations() {
        return totalLoadCount;
    }

    public static int getUpdateOperations() {
        return totalUpdateCount;
    }

    public static int getRemoveOperations() {
        return totalRemoveCount;
    }

    public static int getPersistOperations() {
        return totalPersistCount;
    }

    public static long getOverallTimeToPersist() {
        return totalPersistTime;
    }

    public static double getAverageTimeToPersist() {
        synchronized (gate) {
            return (double)totalPersistTime / totalPersistCount;
        }
    }

	public  static void clear() {
        synchronized (gate) {
            activePersistCount = 0;
            totalPersistTime = 0;
            persistStart = null;
            totalPersistCount = 0;
            totalLoadCount = 0;
            totalUpdateCount = 0;
            totalRemoveCount = 0;
        }
	}
}
