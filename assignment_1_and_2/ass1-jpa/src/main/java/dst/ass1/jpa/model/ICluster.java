package dst.ass1.jpa.model;

import java.util.Date;
import java.util.List;

public interface ICluster {

	public Long getId();

	public void setId(Long id);

	public String getName();

	public void setName(String name);

	public Date getLastService();

	public void setLastService(Date lastService);

	public Date getNextService();

	public void setNextService(Date nextService);

	public List<ICluster> getComposedOf();

	public void setComposedOf(List<ICluster> composedOf);

	public void addComposedOf(ICluster cluster);

	public List<ICluster> getPartOf();

	public void setPartOf(List<ICluster> partOf);

	public void addPartOf(ICluster cluster);

	public List<IComputer> getComputers();

	public void setComputers(List<IComputer> computers);

	public void addComputer(IComputer computer);

	public IAdmin getAdmin();

	public void setAdmin(IAdmin admin);

	public IGrid getGrid();

	public void setGrid(IGrid grid);

}
