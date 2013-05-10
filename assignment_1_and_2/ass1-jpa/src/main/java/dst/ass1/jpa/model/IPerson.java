package dst.ass1.jpa.model;

public interface IPerson {

	public Long getId();

	public void setId(Long id);

	public String getLastName();

	public void setLastName(String lastName);

	public String getFirstName();

	public void setFirstName(String firstName);

	public IAddress getAddress();

	public void setAddress(IAddress address);
}
