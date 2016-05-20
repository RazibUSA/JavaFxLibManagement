package business;

import javafx.beans.property.SimpleStringProperty;

public class MemberDisplay {
	private SimpleStringProperty memberId;
	private SimpleStringProperty firstName;
	private SimpleStringProperty lastName;
	private SimpleStringProperty street;
	private SimpleStringProperty city;
	private SimpleStringProperty state;
	private SimpleStringProperty zip;
	private SimpleStringProperty telephone;
	
	public MemberDisplay(String memberId, String firstName, String lastName,
			String street, String city, String state, String zip,
			String telephone) {
		this.memberId = new SimpleStringProperty(memberId);
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
		this.street = new SimpleStringProperty(street);
		this.city = new SimpleStringProperty(city);
		this.state = new SimpleStringProperty(state);
		this.zip = new SimpleStringProperty(zip);
		this.telephone = new SimpleStringProperty(telephone);
	}

	public String getMemberId() {
		return memberId.get();
	}

	public String getFirstName() {
		return firstName.get();
	}

	public String getLastName() {
		return lastName.get();
	}

	public String getStreet() {
		return street.get();
	}

	public String getCity() {
		return city.get();
	}

	public String getState() {
		return state.get();
	}

	public String getZip() {
		return zip.get();
	}

	public String getTelephone() {
		return telephone.get();
	}
	
	
	
}
