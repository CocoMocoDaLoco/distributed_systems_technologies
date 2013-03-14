package dst.ass1.jpa.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import dst.ass1.jpa.dao.IComputerDAO;
import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.impl.Computer;

public class ComputerDAO extends GenericJpaDAO<IComputer> implements IComputerDAO {

    public ComputerDAO(Session session) {
        super(session);
    }

    @Override
    public HashMap<IComputer, Integer> findComputersInViennaUsage() {
        Query query = getSession().getNamedQuery("findComputersInVienna");
        List results = query.list();

        HashMap<IComputer, Integer> h = new HashMap<IComputer, Integer>();

        for (Object o : results) {
            Object oo[] = (Object[])o;
            Computer computer = (Computer)oo[0];
            Date start = (Date)oo[1];
            Date end = (Date)oo[2];

            int elapsedTime = 0;

            if (start != null && end != null) {
                elapsedTime = (int)(end.getTime() - start.getTime());
            }

            if (h.containsKey(computer)) {
                elapsedTime += h.get(computer);
            }

            h.put(computer, elapsedTime);
        }

        return h;
    }

    @Override
    protected String getTableName() {
        return Computer.class.getSimpleName();
    }

}
