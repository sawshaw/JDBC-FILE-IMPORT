package com.eshore.fileImport;

import java.util.Map;
import java.util.Timer;

/**
 * @author mercy
 *创建定时任务,定时执行
 */
public class TaskTimer {
	private static Map<String,String> map=MapRegister.getMap();	
	private Timer timer = new Timer("fileImport");
	public void startTask(){
		if(timer==null){
			timer=new Timer("fileImport");
		}
		TextParse task=new TextParse();
		long delay=2000;
		long period=Long.parseLong(map.get("period"));
		timer.schedule(task, delay, period* 1000L);
	}
	public static void main(String[] args) {
		new TaskTimer().startTask();
	}

}
