package dst.ass1.jpa.dao.impl;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IComputerDAO;
import dst.ass1.jpa.model.IComputer;

public class ComputerDAO implements IComputerDAO {

    public ComputerDAO(Session session) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public IComputer findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IComputer> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HashMap<IComputer, Integer> findComputersInViennaUsage() {
        // TODO Auto-generated method stub
        return null;
    }

}
