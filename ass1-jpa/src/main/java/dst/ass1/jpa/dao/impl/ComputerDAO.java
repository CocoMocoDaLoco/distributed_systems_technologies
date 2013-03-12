package dst.ass1.jpa.dao.impl;

import java.util.HashMap;

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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getTableName() {
        return Computer.class.getSimpleName();
    }

}
