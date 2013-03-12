package dst.ass1.jpa.dao.impl;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IClusterDAO;
import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.impl.Cluster;

public class ClusterDAO extends GenericJpaDAO<ICluster> implements IClusterDAO {

    public ClusterDAO(Session session) {
        super(session);
    }

    @Override
    protected String getTableName() {
        return Cluster.class.getSimpleName();
    }

}
