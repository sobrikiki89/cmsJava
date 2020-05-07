package web.module.setup.model;

import com.fasterxml.jackson.annotation.JsonView;

public class CompanyAjaxDTO {
	public static class PublicView {
	}

	private Long companyId;

	private String name;

	private String contactPerson;

	private String telNo;

	private String faxNo;

	private String address1;

	private String address2;

	private String address3;

	private String city;

	private String postcode;

	private String state;

	@JsonView(PublicView.class)
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@JsonView(PublicView.class)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonView(PublicView.class)
	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	@JsonView(PublicView.class)
	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	@JsonView(PublicView.class)
	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	@JsonView(PublicView.class)
	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	@JsonView(PublicView.class)
	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	@JsonView(PublicView.class)
	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	@JsonView(PublicView.class)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@JsonView(PublicView.class)
	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	@JsonView(PublicView.class)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
