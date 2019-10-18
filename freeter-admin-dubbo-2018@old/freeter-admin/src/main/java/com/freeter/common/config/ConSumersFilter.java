package com.freeter.common.config;

import java.util.Map;
import java.util.UUID;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freeter.modules.sys.shiro.ShiroUtils;

public class ConSumersFilter  implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		// TODO Auto-generated method stub
		ShiroUtils.getUserEntity();
		ObjectMapper mapper = new ObjectMapper();
		String user;
		try {
			user = mapper.writeValueAsString(ShiroUtils.getUserEntity());
			System.out.println(user);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			user = "";
		}
		  Map<String, String> StringMap = invocation.getAttachments();
		  // 设置参数
		  StringMap.put("user_entity",user);
	        System.out.println("生成tradeId:"+StringMap.get("traceId"));
	        return invoker.invoke(invocation);
	}

}
