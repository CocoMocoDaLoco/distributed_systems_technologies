package dst.ass1.jpa.model.impl;

import javax.persistence.Embeddable;

import dst.ass1.jpa.model.IAddress;

@Embeddable
public class Address implements IAddress {

    private String street;
    private String city;
    private String zipCode;

    @Override
    public String getStreet() {
        return street;
    }

    @Override
    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String getZipCode() {
        return zipCode;
    }

    @Override
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

}
