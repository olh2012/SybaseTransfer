package com.forms.task;

import com.forms.task.core.base.AbstractTask;


public class TestTask extends AbstractTask {
	
	private String name;
	
	public Integer call() throws Exception {
		System.out.println("thread:"+Thread.currentThread().getName()+";name:"+name+";execute start");
		Thread.sleep(30);
		System.out.println("thread:"+Thread.currentThread().getName()+";name:"+name+";execute complete");
		return 0;
	}

	public void setName(String name){
		this.name =name;
	}
}
