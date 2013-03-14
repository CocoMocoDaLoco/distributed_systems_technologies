package dst.ass1.jpa.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.IMembership;
import dst.ass1.jpa.model.IUser;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "accountNo", "bankCode" }) })
@NamedQueries({
@NamedQuery(name = "findUsersWithActiveMembership",
            query = "select u " +
                    "from User u " +
                    "   join u.memberships m " +
                    "   join u.jobs j " +
                    "   join j.execution.computers c " +
                    "where m.grid.name = :name " +
                    "   and c.cluster.grid = m.grid " +
                    "group by u " +
                    "having count(j) >= :minNr"),
@NamedQuery(name = "findMostActiveUser",
            query = "select u " +
                    "from User u " +
                    "   join u.jobs j " +
                    "group by u " +
                    "having count(j) >= all ( " +
                    "   select count(j) " +
                    "   from User u " +
                    "      join u.jobs j " +
                    "   group by u)")})
public class User extends Person implements IUser {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(columnDefinition = "binary(16)")
    private byte[] password;
    private String accountNo;
    private String bankCode;

    @OneToMany(targetEntity = Job.class, mappedBy = "user")
    private List<IJob> jobs = new ArrayList<IJob>();

    @OneToMany(targetEntity = Membership.class, mappedBy = "user")
    private List<IMembership> memberships = new ArrayList<IMembership>();

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public byte[] getPassword() {
        return password;
    }

    @Override
    public void setPassword(byte[] password) {
        this.password = password;
    }

    @Override
    public String getAccountNo() {
        return accountNo;
    }

    @Override
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @Override
    public String getBankCode() {
        return bankCode;
    }

    @Override
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    @Override
    public List<IJob> getJobs() {
        return jobs;
    }

    @Override
    public void setJobs(List<IJob> jobs) {
        this.jobs = jobs;
    }

    @Override
    public void addJob(IJob job) {
        jobs.add(job);
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
    public void addMembership(IMembership membership) {
        this.memberships.add(membership);
    }

}
