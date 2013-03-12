package dst.ass1.jpa.model.impl;

import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

import dst.ass1.jpa.model.IAddress;
import dst.ass1.jpa.model.IPerson;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person implements IPerson {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;
    private String lastName;
    private String firstName;

    @Embedded
    private IAddress address;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public IAddress getAddress() {
        return address;
    }

    @Override
    public void setAddress(IAddress address) {
        this.address = address;
    }

}
