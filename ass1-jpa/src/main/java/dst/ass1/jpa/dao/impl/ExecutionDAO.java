package dst.ass1.jpa.dao.impl;

import java.util.List;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IExecutionDAO;
import dst.ass1.jpa.model.IExecution;

public class ExecutionDAO implements IExecutionDAO {

    public ExecutionDAO(Session session) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public IExecution findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IExecution> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

}
