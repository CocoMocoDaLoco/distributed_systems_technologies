package dst.ass1.jpa.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.ICluster;

@Entity
public class Admin extends Person implements IAdmin {

    @OneToMany(targetEntity = Cluster.class)
    private List<ICluster> clusters = new ArrayList<ICluster>();

    @Override
    public List<ICluster> getClusters() {
        return clusters;
    }

    @Override
    public void setClusters(List<ICluster> clusters) {
        this.clusters = clusters;
    }

    @Override
    public void addCluster(ICluster cluster) {
        clusters.add(cluster);
    }

}
