package com.forms.task;

import org.junit.Test;

import com.forms.platform.SpringCoreTest;
import com.forms.task.core.TaskExecute;

public class TaskExecuteTest  extends SpringCoreTest{

	@Test
	public void test() throws Exception {
		// 按配置文件中的任务名称提交执行
		TaskExecute.main(new String[]{"-help"});
		// 按配置文件中的任务名称提交执行
		TaskExecute.main(new String[]{"-transfer"});
		// 按配置文件中的任务名称提交执行
		TaskExecute.main(new String[]{"test"});
		// 通过命令行参数定义任务，然后执行
		TaskExecute.main(new String[]{"-transfer","-o","-n","p_gykh_daybat1"});
		// 同时按以上两种方式提交任务
		TaskExecute.main(new String[]{"-transfer","-o","-n","p_gykh_daybat1","test","-tn","2"});
	}
}
