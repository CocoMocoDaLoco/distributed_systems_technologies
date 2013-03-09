package dst.ass1.jpa.model;

import java.math.BigDecimal;
import java.util.List;

public interface IGrid {

	public Long getId();

	public void setId(Long id);

	public String getName();

	public void setName(String name);

	public String getLocation();

	public void setLocation(String location);

	public BigDecimal getCostsPerCPUMinute();

	public void setCostsPerCPUMinute(BigDecimal costsPerCPUMinute);

	public void addMembership(IMembership membership);

	public List<IMembership> getMemberships();

	public void setMemberships(List<IMembership> memberships);

	public List<ICluster> getClusters();

	public void setClusters(List<ICluster> clusters);

	public void addCluster(ICluster cluster);
}
