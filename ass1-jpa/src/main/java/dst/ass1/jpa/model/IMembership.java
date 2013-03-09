package dst.ass1.jpa.model;

import java.util.Date;

public interface IMembership {

	public IMembershipKey getId();

	public void setId(IMembershipKey id);

	public Date getRegistration();

	public void setRegistration(Date registration);

	public Double getDiscount();

	public void setDiscount(Double discount);
}
