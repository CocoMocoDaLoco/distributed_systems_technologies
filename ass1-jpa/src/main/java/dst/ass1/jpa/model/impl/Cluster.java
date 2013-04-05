package dst.ass1.jpa.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IGrid;

@Entity
public class Cluster implements ICluster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String name;
    private Date lastService;
    private Date nextService;

    @ManyToMany(cascade = CascadeType.PERSIST, targetEntity = Cluster.class)
    @JoinTable(name = "composed_of",
               joinColumns = @JoinColumn(name = "partOf_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "composedOf_id", referencedColumnName = "id"))
    private List<ICluster> composedOf = new ArrayList<ICluster>();

    @ManyToMany(targetEntity = Cluster.class, mappedBy = "composedOf")
    private List<ICluster> partOf = new ArrayList<ICluster>();

    @OneToMany(cascade = CascadeType.PERSIST, targetEntity = Computer.class)
    private List<IComputer> computers = new ArrayList<IComputer>();

    @ManyToOne(targetEntity = Admin.class, optional = false, cascade = CascadeType.PERSIST)
    private IAdmin admin;

    @ManyToOne(targetEntity = Grid.class, optional = false, cascade = CascadeType.PERSIST)
    private IGrid grid;

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
    public Date getLastService() {
        return lastService;
    }

    @Override
    public void setLastService(Date lastService) {
        this.lastService = lastService;
    }

    @Override
    public Date getNextService() {
        return nextService;
    }

    @Override
    public void setNextService(Date nextService) {
        this.nextService = nextService;
    }

    @Override
    public List<ICluster> getComposedOf() {
        return composedOf;
    }

    @Override
    public void setComposedOf(List<ICluster> composedOf) {
        this.composedOf = composedOf;
    }

    @Override
    public void addComposedOf(ICluster cluster) {
        composedOf.add(cluster);
    }

    @Override
    public List<ICluster> getPartOf() {
        return partOf;
    }

    @Override
    public void setPartOf(List<ICluster> partOf) {
        this.partOf = partOf;
    }

    @Override
    public void addPartOf(ICluster cluster) {
        partOf.add(cluster);
    }

    @Override
    public List<IComputer> getComputers() {
        return computers;
    }

    @Override
    public void setComputers(List<IComputer> computers) {
        this.computers = computers;
    }

    @Override
    public void addComputer(IComputer computer) {
        computers.add(computer);
    }

    @Override
    public IAdmin getAdmin() {
        return admin;
    }

    @Override
    public void setAdmin(IAdmin admin) {
        this.admin = admin;
    }

    @Override
    public IGrid getGrid() {
        return grid;
    }

    @Override
    public void setGrid(IGrid grid) {
        this.grid = grid;
    }

}
