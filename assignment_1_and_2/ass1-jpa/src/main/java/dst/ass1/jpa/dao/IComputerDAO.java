package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.IComputer;

import java.util.HashMap;

public interface IComputerDAO extends GenericDAO<IComputer> {

	public HashMap<IComputer, Integer> findComputersInViennaUsage();
	
}
