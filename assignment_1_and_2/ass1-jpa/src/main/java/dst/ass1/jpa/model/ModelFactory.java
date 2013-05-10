package dst.ass1.jpa.model;

import java.io.Serializable;

import dst.ass1.jpa.model.impl.Address;
import dst.ass1.jpa.model.impl.Admin;
import dst.ass1.jpa.model.impl.Cluster;
import dst.ass1.jpa.model.impl.Computer;
import dst.ass1.jpa.model.impl.Environment;
import dst.ass1.jpa.model.impl.Execution;
import dst.ass1.jpa.model.impl.Grid;
import dst.ass1.jpa.model.impl.Job;
import dst.ass1.jpa.model.impl.Membership;
import dst.ass1.jpa.model.impl.MembershipKey;
import dst.ass1.jpa.model.impl.User;

public class ModelFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    public IAddress createAddress() {
        return new Address();
    }

    public IAdmin createAdmin() {
        return new Admin();
    }

    public ICluster createCluster() {
        return new Cluster();
    }

    public IComputer createComputer() {
        return new Computer();
    }

    public IEnvironment createEnvironment() {
        return new Environment();
    }

    public IExecution createExecution() {
        return new Execution();
    }

    public IGrid createGrid() {
        return new Grid();
    }

    public IJob createJob() {
        return new Job();
    }

    public IMembership createMembership() {
        return new Membership();
    }

    public IMembershipKey createMembershipKey() {
        return new MembershipKey();
    }

    public IUser createUser() {
        return new User();
    }

}
