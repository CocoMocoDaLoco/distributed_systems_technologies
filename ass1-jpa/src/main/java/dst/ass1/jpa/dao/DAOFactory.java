package dst.ass1.jpa.dao;

public class DAOFactory {

	private org.hibernate.Session session;

	public DAOFactory(org.hibernate.Session session) {
		this.session = session;
	}

	public IGridDAO getGridDAO() {
		// TODO
		return null;
	}

	public IAdminDAO getAdminDAO() {
		// TODO
		return null;
	}

	public IClusterDAO getClusterDAO() {
		// TODO
		return null;
	}

	public IComputerDAO getComputerDAO() {
		// TODO
		return null;
	}

	public IEnvironmentDAO getEnvironmentDAO() {
		// TODO
		return null;
	}

	public IExecutionDAO getExecutionDAO() {
		// TODO
		return null;
	}

	public IJobDAO getJobDAO() {
		// TODO
		return null;
	}

	public IMembershipDAO getMembershipDAO() {
		// TODO
		return null;
	}

	public IUserDAO getUserDAO() {
		// TODO
		return null;
	}

}
