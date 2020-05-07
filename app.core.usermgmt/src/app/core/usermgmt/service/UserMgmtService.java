package app.core.usermgmt.service;

import java.io.Serializable;
import java.util.List;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.exception.BaseApplicationException;
import app.core.usermgmt.dto.RoleDTO;
import app.core.usermgmt.dto.RoleFunctionDTO;
import app.core.usermgmt.dto.UserDTO;
import app.core.usermgmt.model.Function;
import app.core.usermgmt.model.Role;
import app.core.usermgmt.model.RoleFunction;
import app.core.usermgmt.model.RoleFunctionPK;
import app.core.usermgmt.model.User;
import app.core.usermgmt.model.UserRole;
import app.core.usermgmt.model.UserRoleId;

public interface UserMgmtService {

	public void initContextVarType();

	public List<Role> getRoleByUserId(Long userId);

	public void updateLastRoleId(User user, Role role);

	public void resetConnectedFlag();

	public DataSet<UserDTO> getUser(DatatablesCriterias criteria) throws BaseApplicationException;

	public User getUser(Long userId);

	public User getUserByUsername(String username);

	public Long createUserIfNotFound(User user);

	public Long createUser(User user);

	public void updateUser(User user);

	public void updatePassword(User user);

	public UserRoleId createUserRoleIfNotFound(UserRole userRole);

	public List<Role> getRoles();

	public DataSet<RoleFunctionDTO> getRoleFunction(DatatablesCriterias criteria) throws BaseApplicationException;

	public Long createRoleIfNotFound(Role role);

	public Long createRole(Role role);

	public RoleFunctionPK createRoleFunctionIfNotFound(RoleFunction roleFunction);

	public Serializable createRoleFunction(RoleFunction roleFunction);

	public void updateRole(Role role);

	public List<RoleFunction> getRoleFunction();

	public List<Function> getFunction();

	public RoleFunction getRoleFuncEdit(RoleFunctionPK roleFunctionPk);

	public void updateRoleFunction(RoleFunction roleFunction);

	public void deleteRoleFunction(List<RoleFunction> roleFunctions);

	public DataSet<RoleDTO> getRole(DatatablesCriterias criteria) throws BaseApplicationException;

	public Role getRoleByName(String roleName);

	public Role getRole(Long roleId);

	public void updateRoleObject(Role role);

	public Role getUniqueSortOrder(Long sortOrder);

}
