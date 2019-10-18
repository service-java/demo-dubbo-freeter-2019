
package com.freeter.common.aspect;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.alibaba.dubbo.rpc.RpcContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freeter.common.annotation.DataFilter;
import com.freeter.common.container.ThreadContainer;
import com.freeter.common.utils.Constant;
import com.freeter.modules.sys.entity.SysUserEntity;
import com.freeter.modules.sys.service.SysDeptService;
import com.freeter.modules.sys.service.SysRoleDeptService;
import com.freeter.modules.sys.service.SysUserRoleService;
import com.freeter.modules.user.entity.UserEntity;

/**
 * 数据过滤，切面处理类
 *
 * @author Mark 171998110@qq.com
 * @since 2.0.3 2018-10-24
 */
@Aspect
@Component
public class DataFilterAspect {
	@Autowired
	private SysDeptService sysDeptService;
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysRoleDeptService sysRoleDeptService;

	@Pointcut("@annotation(com.freeter.common.annotation.DataFilter)")
	public void dataFilterCut() {

	}

	@Before("dataFilterCut()")
	public void dataFilter(JoinPoint point) throws Throwable {
		Object params = point.getArgs()[0];

		if (params != null && params instanceof Map) {
			Map map = (Map) params;
			SysUserEntity user = (SysUserEntity) map.get("user_entity");
			// 如果不是超级管理员，则进行数据过滤
			if (user != null && user.getUserId() != Constant.SUPER_ADMIN) {
				map.put(Constant.SQL_FILTER, getSQLFilter(user, point));
			}

			return;
		}
		SysUserEntity user = (SysUserEntity) ThreadContainer.get("user_entity");

		if (user.getUserId() != Constant.SUPER_ADMIN) {
			ThreadContainer.set(Constant.SQL_FILTER, getSQLFilter(user, point));
		}
		return;

	}

	/**
	 * 获取数据过滤的SQL
	 */
	private String getSQLFilter(SysUserEntity user, JoinPoint point) {
		MethodSignature signature = (MethodSignature) point.getSignature();
		DataFilter dataFilter = signature.getMethod().getAnnotation(DataFilter.class);
		// 获取表的别名
		String tableAlias = dataFilter.tableAlias();
		if (StringUtils.isNotBlank(tableAlias)) {
			tableAlias += ".";
		}

		// 部门ID列表
		Set<Long> deptIdList = new HashSet<>();

		// 用户角色对应的部门ID列表
		List<Long> roleIdList = sysUserRoleService.queryRoleIdList(user.getUserId());
		if (roleIdList.size() > 0) {
			List<Long> userDeptIdList = sysRoleDeptService
					.queryDeptIdList(roleIdList.toArray(new Long[roleIdList.size()]));
			deptIdList.addAll(userDeptIdList);
		}

		// 用户子部门ID列表
		if (dataFilter.subDept()) {
			List<Long> subDeptIdList = sysDeptService.getSubDeptIdList(user.getDeptId());
			deptIdList.addAll(subDeptIdList);
		}

		StringBuilder sqlFilter = new StringBuilder();
		sqlFilter.append(" (");

		if (deptIdList.size() > 0) {
			sqlFilter.append(tableAlias).append(dataFilter.deptId()).append(" in(")
					.append(StringUtils.join(deptIdList, ",")).append(")");
		}

		// 没有本部门数据权限，也能查询本人数据
		if (dataFilter.user()) {
			if (deptIdList.size() > 0) {
				sqlFilter.append(" or ");
			}
			sqlFilter.append(tableAlias).append(dataFilter.userId()).append("=").append(user.getUserId());
		}

		sqlFilter.append(")");

		return sqlFilter.toString();
	}
}
