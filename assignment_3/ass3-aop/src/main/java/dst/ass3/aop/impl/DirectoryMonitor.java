package dst.ass3.aop.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DirectoryMonitor {

    public interface INewJarListener { void onNewJar(File file); }

    private final Map<String, File> jars = Collections.synchronizedMap(new HashMap<String, File>());
    private final INewJarListener listener;

    public DirectoryMonitor(INewJarListener listener) {
        this.listener = listener;
    }

    public synchronized void scanDirectories(Set<File> directories) {
        for (File dir : directories) {
            File files[] = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });

            for (File file : files) {
                try {
                    final String filename = file.getCanonicalPath();
                    final File old = jars.get(filename);

                    if (old != null && old.lastModified() >= file.lastModified()) {
                        continue;
                    }

                    jars.put(filename, file);
                    listener.onNewJar(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
