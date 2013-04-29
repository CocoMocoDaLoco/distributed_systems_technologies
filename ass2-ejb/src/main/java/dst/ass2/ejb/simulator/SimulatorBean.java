package dst.ass2.ejb.simulator;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@Startup
public class SimulatorBean {

    @Resource
    private TimerService timerService;

    @PersistenceContext
    private EntityManager entityManager;

    @Schedule(hour="*", minute = "*", second = "*/15")
    public void simulate() {
        System.out.println("OMG");
    }

}
