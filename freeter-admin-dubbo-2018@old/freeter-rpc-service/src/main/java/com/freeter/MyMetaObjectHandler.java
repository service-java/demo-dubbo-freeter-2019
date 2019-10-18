package com.freeter;

import java.util.Date;

import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.mapper.MetaObjectHandler;
import com.freeter.common.container.ThreadContainer;
import com.freeter.modules.sys.entity.SysUserEntity;
import com.freeter.modules.sys.shiro.ShiroUtils;

//@Component
public class MyMetaObjectHandler extends MetaObjectHandler {

	protected final static Logger logger = LoggerFactory.getLogger(RPCServiceApplication.class);

	@Override
	public void insertFill(MetaObject metaObject) {
		logger.info("新增的时候干点不可描述的事情");
		setFieldValByName("createTime", new Date(), metaObject);
		try {
			if (ThreadContainer.get("user_entity") != null) {
				setFieldValByName("createBy", ((SysUserEntity)ThreadContainer.get("user_entity")).getUsername(), metaObject);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		logger.info("更新的时候干点不可描述的事情");

		setFieldValByName("updateTime", new Date(), metaObject);
		try {
			if (ThreadContainer.get("user_entity") != null) {
				setFieldValByName("updateBy", ((SysUserEntity)ThreadContainer.get("user_entity")).getUsername(), metaObject);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
