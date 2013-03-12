package dst.ass1.jpa.dao.impl;

import java.util.List;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IClusterDAO;
import dst.ass1.jpa.model.ICluster;

public class ClusterDAO implements IClusterDAO {

    public ClusterDAO(Session session) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public ICluster findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ICluster> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

}
