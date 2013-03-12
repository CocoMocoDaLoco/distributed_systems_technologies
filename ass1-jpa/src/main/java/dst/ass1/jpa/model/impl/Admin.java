package dst.ass1.jpa.model.impl;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.ICluster;

@Entity
public class Admin implements IAdmin {

    @Id /* TODO: Temp! */
    public long id;

    @ElementCollection(targetClass = Cluster.class)
    private List<ICluster> clusters;

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
