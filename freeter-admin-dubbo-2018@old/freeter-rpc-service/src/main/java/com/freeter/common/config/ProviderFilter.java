package com.freeter.common.config;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freeter.common.container.ThreadContainer;
import com.freeter.common.utils.Constant;
import com.freeter.modules.sys.entity.SysUserEntity;

@Activate
public class ProviderFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) {
        System.out.println("providerFilter");
        Map<String,String> map =invocation.getAttachments();
        map.keySet().forEach(key -> System.out.println("map.get(" + key + ") = " + map.get(key)));
        String userEntityJson=    invocation.getAttachment("user_entity");
        if(StringUtils.isNotEmpty(userEntityJson)) {
        	
        	SysUserEntity user =null;
			try {
				 ObjectMapper objectMapper = new ObjectMapper();
				user = objectMapper.readValue(userEntityJson,SysUserEntity.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(user !=null) {
        	 ThreadContainer.set("user_entity",user);
			}
        }
      
        return invoker.invoke(invocation);
    }
}