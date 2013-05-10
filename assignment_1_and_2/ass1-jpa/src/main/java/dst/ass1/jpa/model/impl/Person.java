package dst.ass1.jpa.model.impl;

import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import dst.ass1.jpa.model.IAddress;
import dst.ass1.jpa.model.IPerson;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person implements IPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String lastName;
    private String firstName;

    @Embedded
    private Address address;

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
        this.address = new Address();
        this.address.setCity(address.getCity());
        this.address.setStreet(address.getStreet());
        this.address.setZipCode(address.getZipCode());
    }

}
