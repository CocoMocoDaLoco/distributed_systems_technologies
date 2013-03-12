package dst.ass1.jpa.dao.impl;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IGridDAO;
import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.impl.Grid;

public class GridDAO extends GenericJpaDAO<IGrid> implements IGridDAO {

    public GridDAO(Session session) {
        super(session);
    }

    @Override
    protected String getTableName() {
        return Grid.class.getSimpleName();
    }

}
