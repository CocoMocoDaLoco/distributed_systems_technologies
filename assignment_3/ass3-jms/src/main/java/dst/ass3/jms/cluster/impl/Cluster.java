package dst.ass3.jms.cluster.impl;

import dst.ass3.jms.cluster.ICluster;

/* PERSISTENT, p842. */

public class Cluster implements ICluster {

    private final String name;
    private IClusterListener listener;

    public Cluster(String name) {
        this.name = name;
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setClusterListener(IClusterListener listener) {
        this.listener = listener;
    }

}
