package app.core.dto;

public abstract class DTOCode extends DTOBase {

	private static final long serialVersionUID = 1539486644519508489L;

	private String code;

	private String name;

	private Long sortOrder;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}

}
