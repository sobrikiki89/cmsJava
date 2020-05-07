package module.setup.dto;

import app.core.dto.DTOBase;

public class ContactDTO extends DTOBase {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private String contactPerson;

	private String telNo;

	private String faxNo;

	private String mobileNo;

	private String email;

	private String address1;

	private String address2;

	private String address3;

	private String city;

	private String postcode;

	private String stateCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address1 == null) ? 0 : address1.hashCode());
		result = prime * result + ((address2 == null) ? 0 : address2.hashCode());
		result = prime * result + ((address3 == null) ? 0 : address3.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((contactPerson == null) ? 0 : contactPerson.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((faxNo == null) ? 0 : faxNo.hashCode());
		result = prime * result + ((mobileNo == null) ? 0 : mobileNo.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((postcode == null) ? 0 : postcode.hashCode());
		result = prime * result + ((stateCode == null) ? 0 : stateCode.hashCode());
		result = prime * result + ((telNo == null) ? 0 : telNo.hashCode());
		return result;
	}

	public boolean dtoIsEmpty() {
		if (address1 != null && !address1.isEmpty()) {
			return false;
		}
		if (address2 != null && !address2.isEmpty()) {
			return false;
		}
		if (address3 != null && !address3.isEmpty()) {
			return false;
		}
		if (city != null && !city.isEmpty()) {
			return false;
		}
		if (contactPerson != null && !contactPerson.isEmpty()) {
			return false;
		}
		if (email != null && !email.isEmpty()) {
			return false;
		}
		if (faxNo != null && !faxNo.isEmpty()) {
			return false;
		}
		if (mobileNo != null && !mobileNo.isEmpty()) {
			return false;
		}
		if (name != null && !name.isEmpty()) {
			return false;
		}
		if (postcode != null && !postcode.isEmpty()) {
			return false;
		}
		if (stateCode != null && !stateCode.isEmpty()) {
			return false;
		}
		if (telNo != null && !telNo.isEmpty()) {
			return false;
		}
		return true;
	}

}
