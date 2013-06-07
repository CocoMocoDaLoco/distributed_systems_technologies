package dst.ass3.aop.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dst.ass3.aop.IPluginExecutable;
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
        JarFile jarFile = null;
        URLClassLoader cl = null;
        try {
            System.out.printf("Found new JAR: %s%n", file.getName());

            jarFile = new JarFile(file);
            final Enumeration<JarEntry> e = jarFile.entries();

            final URL[] urls = { new URL(String.format("jar:file:%s!/", file.getCanonicalPath())) };
            cl = URLClassLoader.newInstance(urls);

            while (e.hasMoreElements()) {
                final JarEntry je = e.nextElement();
                if(je.isDirectory() || !je.getName().endsWith(".class")){
                    continue;
                }

                final String className = je.getName()
                        .substring(0, je.getName().length() - ".class".length())
                        .replace('/', '.');
                final Class<?> c = cl.loadClass(className);

                if (!IPluginExecutable.class.isAssignableFrom(c))  {
                    continue;
                }

                final IPluginExecutable instance = (IPluginExecutable)c.newInstance();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        instance.execute();
                    }
                });
            }

            cl.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } finally {
            try { if (jarFile != null) { jarFile.close(); } } catch (IOException e) { e.printStackTrace(); }
            try { if (cl != null) { cl.close(); } } catch (IOException e) { e.printStackTrace(); }
        }
    }

}
