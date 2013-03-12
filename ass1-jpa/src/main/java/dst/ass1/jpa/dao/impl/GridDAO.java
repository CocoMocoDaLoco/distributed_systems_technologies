package dst.ass1.jpa.dao.impl;

import java.util.List;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IGridDAO;
import dst.ass1.jpa.model.IGrid;

public class GridDAO implements IGridDAO {

    public GridDAO(Session session) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public IGrid findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IGrid> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

}
