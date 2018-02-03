package com.forms.task.core.base;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.forms.platform.core.exception.Throw;
import com.forms.task.core.ITask;


/**
 * Project : Sybase数据库迁移 <br>
 * Description : 迁移任务组，充当迁移任务的容器<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public class TaskGroup extends AbstractTask{

	/**
	 * 任务列表
	 */
	private List<ITask> taskList;
	
	/**
	 * 并发执行线程数
	 */
	private int threadCount;
	
	/**
	 * 注册任务
	 */
	@Override
	public void register() {
		super.register();//注册任务本身
		List<ITask> taskList = getTaskList();
		if(null != taskList && !taskList.isEmpty()){
			//注册子任务
			for(ITask task : taskList){
				task.register();
			}
		}
	}

	public Integer call(){
		List<ITask> taskList = getTaskList();
		if(null == taskList || taskList.isEmpty()){
			return 0;
		}
		try {
			int count = Math.min(getThreadCount(), taskList.size());
			ExecutorService executor = count > 1 ?Executors.newFixedThreadPool(count):Executors.newSingleThreadExecutor();
			List<Future<Integer>> a = executor.invokeAll(taskList);
			for(Future<Integer> f: a){
				f.get();
			}
			executor.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			e.getCause().printStackTrace();
			Throw.throwException(e.getCause());
		}
		return 0;
	}

	public List<ITask> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<ITask> taskList) {
		this.taskList = taskList;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
}
