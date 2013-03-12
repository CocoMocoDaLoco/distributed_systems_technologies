package dst.ass1.jpa.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.IMembership;
import dst.ass1.jpa.model.IUser;

@Entity
public class User extends Person implements IUser {

    private String username;
    private byte[] password;
    private String accountNo;
    private String bankCode;

    @OneToMany(targetEntity = Job.class)
    private List<IJob> jobs = new ArrayList<IJob>();

    @OneToMany(targetEntity = Membership.class)
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
