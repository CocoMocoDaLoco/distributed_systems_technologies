package dst.ass1.jpa.listener;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import dst.ass1.jpa.model.impl.Computer;

public class ComputerListener {

    @PrePersist
    public void onPrePersist(Computer computer) {
        final Date now = new Date();
        computer.setCreation(now);
        computer.setLastUpdate(now);
        System.out.printf("%s: onPrePersist%n", ComputerListener.class.getName());
    }

    @PreUpdate
    public void onPreUpdate(Computer computer) {
        final Date now = new Date();
        computer.setLastUpdate(now);
        System.out.printf("%s: onPreUpdatet%n", ComputerListener.class.getName());
    }

}
