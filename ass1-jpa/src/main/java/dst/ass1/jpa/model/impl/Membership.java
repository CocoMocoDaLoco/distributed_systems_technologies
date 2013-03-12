package dst.ass1.jpa.model.impl;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import dst.ass1.jpa.model.IMembership;
import dst.ass1.jpa.model.IMembershipKey;

@Entity
//@IdClass(value = MembershipKey.class)
public class Membership implements IMembership {

    @Id /* TODO: Temp */
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public long tempId;

    @Transient
    private IMembershipKey id;

    //    /* Do we need to duplicate key fields here? */
    //    private IUser user;
    //    private IGrid grid;

    private Date registration;
    private Double discount;

    @Override
    public IMembershipKey getId() {
        return id;
    }

    @Override
    public void setId(IMembershipKey id) {
        this.id = id;
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
