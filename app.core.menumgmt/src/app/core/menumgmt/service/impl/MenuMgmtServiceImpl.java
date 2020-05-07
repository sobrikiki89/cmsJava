package app.core.menumgmt.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.dto.Entity2DTOMapper;
import app.core.exception.BaseApplicationException;
import app.core.menumgmt.dto.MenuItemDTO;
import app.core.menumgmt.model.MenuItem;
import app.core.menumgmt.service.MenuMgmtService;
import app.core.service.impl.AbstractServiceImpl;
import app.core.usermgmt.model.Function;

@Service
public class MenuMgmtServiceImpl extends AbstractServiceImpl implements MenuMgmtService {

	@Autowired
	private SessionFactory sessionFactory;

	/*
	 * menu_item
	 */
	// 1. Listing
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public DataSet<MenuItemDTO> getMenuItem(DatatablesCriterias criteria) throws BaseApplicationException {
		DataSet<MenuItemDTO> menus = getListByCriteria(criteria, MenuItem.class,
				new Entity2DTOMapper<MenuItem, MenuItemDTO>() {
					@Override
					public MenuItemDTO map(MenuItem entity) {
						MenuItemDTO obj = new MenuItemDTO();
						obj.setId(entity.getId());
						obj.setName(entity.getName());
						if (entity.getParentMenuItem() != null) {
							obj.setParentMenuItem(entity.getParentMenuItem().getName());
						} else {
							obj.setParentMenuItem(" ");
						}
						obj.setDescription(entity.getDescription());
						obj.setParentFlag(entity.getParentFlag());
						obj.setSortOrder(entity.getSortOrder());
						return obj;
					}
				});

		Iterator<MenuItemDTO> iterMenus = menus.getRows().iterator();
		MenuItemDTO dto;
		while (iterMenus.hasNext()) {
			dto = iterMenus.next();
			if (dto.getId() == 1L) {
				iterMenus.remove();
			}
		}
		return menus;
	}

	// 2. New
	@Transactional
	public Long createMenu(MenuItem menuItem) {
		Session session = sessionFactory.getCurrentSession();
		if (menuItem.getFunction() != null && StringUtils.isEmpty(menuItem.getFunction().getCode())) {
			menuItem.setFunction(null);
		}
		return (Long) session.save(menuItem);
	}

	// 3. Edit
	@Override
	@Transactional
	public void updateMenu(MenuItem menuItem) {
		if (menuItem.getFunction() != null && StringUtils.isEmpty(menuItem.getFunction().getCode())) {
			menuItem.setFunction(null);
		}
		Session session = sessionFactory.getCurrentSession();
		session.merge(menuItem);
	}

	// 4. Delete
	@Override
	@Transactional
	public void deleteMenu(MenuItem menuItem) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(menuItem);
	}

	@Override
	@Transactional
	public MenuItem getMenuEdit(Long menuId) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT m FROM " + MenuItem.class.getName() + " m WHERE m.id = :menuId")
				.setParameter("menuId", menuId);
		return (MenuItem) query.uniqueResult();
	}

	// Drop Down Box
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<MenuItem> getMenuItem() {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("SELECT m FROM " + MenuItem.class.getName() + " m ORDER BY m.name");
		return (List<MenuItem>) query.list();
	}

	//Check duplicate
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public MenuItem getMenuItemByFunction(Function function) {
		Session session = sessionFactory.getCurrentSession();
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		Query query = session.createQuery("SELECT m FROM " + MenuItem.class.getName() + " m WHERE m.function.code = :functionCode")
				.setParameter("functionCode", function.getCode());
		menuItemList = (List<MenuItem>) query.list();
		if (menuItemList.size() > 0) {
			return menuItemList.get(0);
		}
		return null ;
	}
	
}
