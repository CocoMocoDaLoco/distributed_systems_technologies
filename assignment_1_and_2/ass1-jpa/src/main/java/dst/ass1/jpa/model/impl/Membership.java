package dst.ass1.jpa.model.impl;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.IMembership;
import dst.ass1.jpa.model.IMembershipKey;
import dst.ass1.jpa.model.IUser;

@Entity
@IdClass(value = MembershipKey.class)
public class Membership implements IMembership {

    @Id
    @ManyToOne(targetEntity = User.class, optional = false)
    private IUser user;

    @Id
    @ManyToOne(targetEntity = Grid.class, optional = false)
    private IGrid grid;

    private Date registration;
    private Double discount;

    @Override
    public IMembershipKey getId() {
        MembershipKey key = new MembershipKey();
        key.setGrid(grid);
        key.setUser(user);
        return key;
    }

    @Override
    public void setId(IMembershipKey id) {
        user = id.getUser();
        grid = id.getGrid();
    }

    @Override
    public Date getRegistration() {
        return registration;
    }

    @Override
    public void setRegistration(Date registration) {
        this.registration = registration;
    }

    @Override
    public Double getDiscount() {
        return discount;
    }

    @Override
    public void setDiscount(Double discount) {
        this.discount = discount;
    }

}
