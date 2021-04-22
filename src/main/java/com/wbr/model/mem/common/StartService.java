package com.wbr.model.mem.common;


import com.wbr.model.mem.constant.SystemCon;
import com.wbr.model.mem.exception.BaseException;
import com.wbr.model.mem.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartService implements ApplicationRunner {

	@Autowired
	private RedisUtil redisUtil;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		//清理映射表缓存
		try{
			System.out.println("=======清理映射表缓存==========");
			redisUtil.set(SystemCon.TAGPOINTALLDATA,null);
			System.out.println("=======清理在线用户==========");
			redisUtil.set(SystemCon.SESSIONUSERDATA,null);
			System.out.println("=======清理token==========");
			redisUtil.deleteByPrex(SystemCon.USERTOKENKEY);

		}catch (Exception e){
			throw new BaseException(e.getMessage());
		}
	}
	
}