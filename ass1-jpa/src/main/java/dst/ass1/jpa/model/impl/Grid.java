package dst.ass1.jpa.model.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.IMembership;

@Entity
public class Grid implements IGrid {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String location;

    @Column(unique = true)
    private String name;
    private BigDecimal costsPerCPUMinute;

    @ManyToMany(targetEntity = Membership.class)
    private List<IMembership> memberships = new ArrayList<IMembership>();

    @OneToMany(targetEntity = Cluster.class)
    private List<ICluster> clusters = new ArrayList<ICluster>();

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
