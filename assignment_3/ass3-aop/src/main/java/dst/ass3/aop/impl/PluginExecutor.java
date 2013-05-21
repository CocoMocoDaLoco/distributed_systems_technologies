package dst.ass3.aop.impl;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dst.ass3.aop.IPluginExecutor;
import dst.ass3.aop.impl.DirectoryMonitor.INewJarListener;

public class PluginExecutor implements IPluginExecutor, INewJarListener {

    private final Set<File> directories = Collections.synchronizedSet(new HashSet<File>());
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Timer timer = new Timer();
    private final DirectoryMonitor directoryMonitor;

    public PluginExecutor() {
        directoryMonitor = new DirectoryMonitor(this);
    }

    @Override
    public void monitor(File dir) {
        directories.add(dir);
    }

    @Override
    public void stopMonitoring(File dir) {
        directories.remove(dir);
    }

    @Override
    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                directoryMonitor.scanDirectories(directories);
            }
        }, 0, 1000);
    }

    @Override
    public void stop() {
        timer.cancel();
    }

    @Override
    public void onNewJar(File file) {
        System.out.printf("Found new JAR: %s%n", file.getName());
    }

}
