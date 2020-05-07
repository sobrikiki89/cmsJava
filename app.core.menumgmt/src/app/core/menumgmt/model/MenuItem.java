package app.core.menumgmt.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import app.core.model.EntityBase;
import app.core.usermgmt.model.Function;

/**
 * Represents an entry in a menu of screens displayed to the user.
 * <p>
 * An entry which has a null Function is a "parent" menu that contains other
 * menu items (potentially nested to any depth).
 * <p>
 * An entry which has a non-null Function is a leaf menu item that causes the
 * screen associated with that function to be displayed when the user selects
 * that menu item.
 */
@Entity
@Table(name = "MENU_ITEM")
public class MenuItem extends EntityBase {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Function function;
	private MenuItem parentMenuItem;
	private String name;
	private String description;
	private Long sortOrder;
	private Boolean parentFlag;
	private List<MenuItem> childMenuItems = new ArrayList<MenuItem>();

	// Constructors

	/** default constructor */
	public MenuItem() {
	}

	/** constructor with id */
	public MenuItem(Long id) {
		this.id = id;
	}

	// Property accessors

	/** Synthetic Id */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "entityKeys")
	@Column(name = "ID")
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The Function whose associated screen is displayed when the user selects
	 * this menu item.
	 * <p>
	 * This is null for "parent" menu items that have other menu items nested
	 * within them.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FUNCTION_CODE", nullable = true)
	public Function getFunction() {
		return this.function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	/**
	 * Get the MenuItem which this menu-item is nested within. If null then this
	 * is a "root" menu-item.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PARENT_ID")
	public MenuItem getParentMenuItem() {
		return this.parentMenuItem;
	}

	public void setParentMenuItem(MenuItem parentMenuItem) {
		this.parentMenuItem = parentMenuItem;
	}

	/**
	 * The name to be displayed on the clickable user menu item
	 */
	@Column(name = "NAME")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * TODO: see getName
	 */
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * The order in which menu items should be displayed. This order is of
	 * course only meaningful relative to other menu items with the same parent.
	 */
	@Column(name = "SORT_ORDER")
	public Long getSortOrder() {
		return this.sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * 
	 */
	@Column(name = "PARENT_FLAG")
	public Boolean getParentFlag() {
		if (parentFlag == null) {
			parentFlag = false;
		}
		return this.parentFlag;
	}

	public void setParentFlag(Boolean parentFlag) {
		this.parentFlag = parentFlag;
	}

	/**
	 * Get the set of menu items that have this menu item as their parent.
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	public List<MenuItem> getChildMenuItems() {
		return this.childMenuItems;
	}

	public void setChildMenuItems(List<MenuItem> menuItems) {
		this.childMenuItems = menuItems;
	}

	@Override
	public String toString() {
		return id + "(" + name + ")";
	}

	@Override
	public boolean equals(Object obj) {
		try {
			MenuItem miObj = (MenuItem) obj;
			if (this.id == null || miObj.getId() == null)
				return false;
			else
				return this.id.equals(miObj.getId());
		} catch (Exception e) {
			return false;
		}
	}

}