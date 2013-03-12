package dst.ass1.jpa.model.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.IMembership;

@Entity
public class Grid implements IGrid {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;
    private String location;
    private String name;
    private BigDecimal costsPerCPUMinute;

    @ElementCollection(targetClass = Membership.class)
    private List<IMembership> memberships;

    @ElementCollection(targetClass = Cluster.class)
    private List<ICluster> clusters;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public BigDecimal getCostsPerCPUMinute() {
        return costsPerCPUMinute;
    }

    @Override
    public void setCostsPerCPUMinute(BigDecimal costsPerCPUMinute) {
        this.costsPerCPUMinute = costsPerCPUMinute;
    }

    @Override
    public void addMembership(IMembership membership) {
        memberships.add(membership);
    }

    @Override
    public List<IMembership> getMemberships() {
        return memberships;
    }

    @Override
    public void setMemberships(List<IMembership> memberships) {
        this.memberships = memberships;
    }

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
