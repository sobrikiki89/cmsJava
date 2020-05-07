package app.core.service.impl;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import app.core.registry.Module;
import app.core.security.UserPrincipal;
import app.core.service.MenuService;

@Service
public class MenuServiceImpl implements MenuService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MenuServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MessageSource messageSource;

	@Transactional(readOnly = true)
	public void getMenu(UserPrincipal userPrincipal, Locale locale) {
		if (userPrincipal != null) {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT TBL.id, TBL.parent_id, TBL.name, TBL.path ");
			sql.append(
					"FROM (SELECT m.id, m.parent_id, m.name, m.parent_flag, m.sort_order, null AS path, null AS role_id");
			sql.append(" FROM MENU_ITEM m WHERE m.FUNCTION_CODE IS NULL");
			sql.append(" UNION");
			sql.append(" SELECT m.id, m.parent_id, m.name, m.parent_flag, m.sort_order, f.path, r.role_id");
			sql.append(" FROM menu_item m LEFT JOIN app_function f ON m.function_code = f.code");
			sql.append(" INNER JOIN ROLE_FUNCTION r ON r.function_code = f.code");
			sql.append(" WHERE r.role_id = ?) AS TBL ");
			sql.append("WHERE TBL.parent_id IS NOT NULL ");
			sql.append("ORDER BY TBL.parent_id, TBL.sort_order");

			Session session = sessionFactory.getCurrentSession();
			Query query = session.createSQLQuery(sql.toString()).setParameter(0,
					new Long(String.valueOf(userPrincipal.getCurrentRoleId())));
			@SuppressWarnings("unchecked")
			List<Object[]> result = query.list();

			String menu = "";
			// No point to construct the map if no result found
			if (result != null && result.size() > 0) {
				menu = constructMenuMap(result, userPrincipal, locale);
			}

			userPrincipal.setMenuList(menu);
		}
	}

	/**
	 * This method is going to perform a recursive lookup to form the menu map
	 * 
	 * @param resultList
	 * @param map
	 */
	protected String constructMenuMap(List<Object[]> resultList, UserPrincipal userPrincipal, Locale locale) {
		/**
		 * <pre>
		 * ID,      PARENT_ID,      NAME,       PATH
		 * 1        null            HOME        null
		 * 2        1               MENU 1      null
		 * 3        1               MENU 2      null
		 * 4        2               MENU 1.1    path1
		 * 5        2               MENU 1.2    path2
		 * </pre>
		 */

		StringBuffer html = new StringBuffer();
		Map<Long, Menu> menuMap = new LinkedHashMap<Long, Menu>();

		// First we need to fill up the index map first
		for (Object[] objs : resultList) {
			Long parentId = BigInteger.class.cast(objs[1]).longValue();
			Menu parentMenu = findParentNode(menuMap, parentId, "");
			// No parent menu found
			if (parentMenu == null) {
				Menu index = new Menu();
				index.setName(String.class.cast(objs[2]));
				index.setPath(String.class.cast(objs[3]));
				index.setIndexStr((menuMap.size() + 1) + ".");
				menuMap.put(BigInteger.class.cast(objs[0]).longValue(), index);
			} else {
				if (parentMenu.getSubMenu() == null) {
					// First child
					Map<Long, Menu> childMap = new LinkedHashMap<Long, Menu>();
					Menu index = new Menu();
					index.setName(String.class.cast(objs[2]));
					index.setPath(String.class.cast(objs[3]));
					index.setIndexStr(parentMenu.getIndexStr() + "1.");
					childMap.put(BigInteger.class.cast(objs[0]).longValue(), index);
					parentMenu.setSubMenu(childMap);
				} else {
					Map<Long, Menu> childMap = parentMenu.getSubMenu();
					Menu index = new Menu();
					index.setName(String.class.cast(objs[2]));
					index.setPath(String.class.cast(objs[3]));
					index.setIndexStr(parentMenu.getIndexStr() + (childMap.size() + 1) + ".");
					childMap.put(BigInteger.class.cast(objs[0]).longValue(), index);
				}
			}
		}

		html.append("<li class=\"dropdown\">");
		html.append("<a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\">")
				.append(messageSource.getMessage("label.menu", null, locale)).append(" <b class=\"caret\">")
				.append("</b></a>");
		html.append("<ul class=\"dropdown-menu\">");
		constructMenu(html, menuMap, userPrincipal, locale);
		html.append("</ul>");
		html.append("</li>");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("User menu HTML [" + html.toString() + "]");
		}

		return html.toString();
	}

	protected Menu findParentNode(Map<Long, Menu> menuMap, Long parentId, String space) {
		for (Map.Entry<Long, Menu> entry : menuMap.entrySet()) {
			if (entry.getKey().equals(parentId)) {
				return entry.getValue();
			} else if (entry.getValue().getSubMenu() != null && entry.getValue().getSubMenu().size() > 0) {
				Menu parent = findParentNode(entry.getValue().getSubMenu(), parentId, space + " ");
				if (parent != null) {
					return parent;
				}
			}
		}
		return null;
	}

	/**
	 * <pre>
	 * <li class=" dropdown">
	 *	<a href="#" class="dropdown-toggle" data-toggle=
	"dropdown">Drop Down <b class="caret"></b></a>
	 *	<ul class="dropdown-menu">
	 *		<li class=" dropdown dropdown-submenu">
	 *			<a href="#" class="dropdown-toggle" data-toggle=
	"dropdown">Level 1</a>
	 *			<ul class="dropdown-menu">
	 *				<li>
	 *					<a href="#">Link 1</a>
	 *				</li>
	 *				<li class="dropdown dropdown-submenu">
	 *					<a href="#" class="dropdown-toggle" data-toggle=
	"dropdown">Level 2</a>
	 *					<ul class="dropdown-menu">
	 *						<li>
	 *							<a href="#">Link 3</a>
	 *						</li>
	 *					</ul>
	 *				</li>
	 *			</ul>
	 *		</li>
	 *	</ul>
	 * </li>
	 * </pre>
	 */
	protected void constructMenu(StringBuffer html, Map<Long, Menu> menuMap, UserPrincipal userPrincipal,
			Locale locale) {
		for (Menu menu : menuMap.values()) {
			// If it has child menu, that means it is a parent
			if (menu.getSubMenu() != null && menu.getSubMenu().size() > 0) {
				html.append("<li class=\"dropdown dropdown-submenu").append("\">");
				html.append("<a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\">").append(menu.getName())
						.append("</b></a>");
				html.append("<ul class=\"dropdown-menu\">");
				constructMenu(html, menu.getSubMenu(), userPrincipal, locale);
				html.append("</ul>");
				html.append("</li>");
			}
			// If it is a node
			else {
				if (menu.getName() != null && !"".equals(menu.getName().trim())) {
					if (menu.getPath() != null) {
						html.append("<li><a href=\"").append(userPrincipal.getContextRoot()).append(menu.getPath())
								.append("\">").append(menu.getName()).append("</a></li>");
					} else {
						// Not to display the menu item if it has no hyperlink
						//html.append("<li><a href=\"#\">").append(menu.getName()).append("</a></li>");
					}
				}
			}
		}
	}

	private class Menu implements Serializable {
		private static final long serialVersionUID = 1L;

		private String indexStr;
		private String name;
		private String path;
		private Map<Long, Menu> subMenu;

		public String getIndexStr() {
			return indexStr;
		}

		public void setIndexStr(String indexStr) {
			this.indexStr = indexStr;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public Map<Long, Menu> getSubMenu() {
			return subMenu;
		}

		public void setSubMenu(Map<Long, Menu> subMenu) {
			this.subMenu = subMenu;
		}
	}

	@Transactional
	public void updateMenus(Module module) {
		app.core.annotation.Menu menu = AnnotationUtils.findAnnotation(module.getClass(),
				app.core.annotation.Menu.class);
		if (menu != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("@Menu found at " + module.getClass().getName());
			}
			app.core.annotation.MenuItem[] items = menu.value();
			if (items != null && items.length > 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Total @MenuItem found [" + items.length + "]");
				}
				try {
					Session session = sessionFactory.getCurrentSession();
					Query query;
					int rowUpdated;
					for (app.core.annotation.MenuItem item : items) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("@MenuItem found with ID [" + item.id() + "], NAME [" + item.name()
									+ "], DESCRIPTION [" + item.description() + "], FUNCTION CODE [" + item.function()
									+ "], PARENT FLAG [" + item.isParent() + "], PARENT ID [" + item.parentId()
									+ "], SORT ORDER [" + item.sortOrder() + "]");
						}

						query = session.createSQLQuery("SELECT id FROM menu_item WHERE id = :id").setParameter("id",
								item.id());
						if (query.uniqueResult() != null) {
							query = session.createSQLQuery(
									"UPDATE menu_item SET name = :name, description = :description, function_code = :function_code, "
											+ "parent_flag = :parent_flag, parent_id = :parent_id, sort_order = :sort_order WHERE id = :id");
							query.setParameter("name", item.name(), StandardBasicTypes.STRING)
									.setParameter("description", item.description(), StandardBasicTypes.STRING)
									.setParameter("function_code",
											StringUtils.isEmpty(item.function()) ? null : item.function(),
											StandardBasicTypes.STRING)
									.setParameter("parent_flag", item.isParent(), StandardBasicTypes.BOOLEAN)
									.setParameter("parent_id", item.parentId() == -1L ? null : item.parentId(),
											StandardBasicTypes.LONG)
									.setParameter("sort_order", item.sortOrder(), StandardBasicTypes.INTEGER)
									.setParameter("id", item.id(), StandardBasicTypes.LONG);
							rowUpdated = query.executeUpdate();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Update menu_item [" + item.id() + "], row updated [" + rowUpdated + "]");
							}
						} else {
							query = session.createSQLQuery(
									"INSERT INTO menu_item (id, name, description, function_code, parent_flag, "
											+ "parent_id, sort_order) VALUES (:id, :name, :description, :function_code, :parent_flag, "
											+ ":parent_id, :sort_order)");
							query.setParameter("id", item.id(), StandardBasicTypes.LONG)
									.setParameter("name", item.name(), StandardBasicTypes.STRING)
									.setParameter("description", item.description(), StandardBasicTypes.STRING)
									.setParameter("function_code",
											StringUtils.isEmpty(item.function()) ? null : item.function(),
											StandardBasicTypes.STRING)
									.setParameter("parent_flag", item.isParent(), StandardBasicTypes.BOOLEAN)
									.setParameter("parent_id", item.parentId() == -1L ? null : item.parentId(),
											StandardBasicTypes.LONG)
									.setParameter("sort_order", item.sortOrder(), StandardBasicTypes.INTEGER);
							rowUpdated = query.executeUpdate();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Insert menu_item [" + item.id() + "], row inserted [" + rowUpdated + "]");
							}
						}
					}
				} catch (Exception e) {
					LOGGER.error("Error on synchronizing @Menu annotation", e);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Skip due to no @MenuItem found");
				}
			}
		}
	}
}
