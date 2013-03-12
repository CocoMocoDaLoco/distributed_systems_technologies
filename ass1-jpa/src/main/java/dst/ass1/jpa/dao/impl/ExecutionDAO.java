package dst.ass1.jpa.dao.impl;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IExecutionDAO;
import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.impl.Execution;

public class ExecutionDAO extends GenericJpaDAO<IExecution> implements IExecutionDAO {

    public ExecutionDAO(Session session) {
        super(session);
    }

    @Override
    protected String getTableName() {
        return Execution.class.getSimpleName();
    }

}
