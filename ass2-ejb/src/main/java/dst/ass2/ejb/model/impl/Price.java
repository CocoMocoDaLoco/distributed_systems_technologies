package dst.ass2.ejb.model.impl;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import dst.ass2.ejb.model.IPrice;

@Entity
public class Price implements IPrice, Comparable<Price> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int nrOfHistoricalJobs;
    private BigDecimal price;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Integer getNrOfHistoricalJobs() {
        return nrOfHistoricalJobs;
    }

    @Override
    public void setNrOfHistoricalJobs(Integer nrOfHistoricalJobs) {
        this.nrOfHistoricalJobs = nrOfHistoricalJobs;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public int compareTo(Price that) {
        return nrOfHistoricalJobs - that.nrOfHistoricalJobs;
    }
}
