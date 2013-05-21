package dst.ass3.aop;

import dst.ass3.aop.impl.PluginExecutor;

public class PluginExecutorFactory {

    public static IPluginExecutor createPluginExecutor() {
        return new PluginExecutor();
    }

}
