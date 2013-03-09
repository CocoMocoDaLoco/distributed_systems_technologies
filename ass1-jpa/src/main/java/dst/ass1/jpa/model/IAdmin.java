package dst.ass1.jpa.model;

import java.util.List;

public interface IAdmin {
	
	public List<ICluster> getClusters();

	public void setClusters(List<ICluster> clusters);

	public void addCluster(ICluster cluster);

}
