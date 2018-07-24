package com.eshore.fileImport;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapRegister{
	private Logger log=LoggerFactory.getLogger(this.getClass());
	public static Map<String, String> map = new HashMap<String, String>();
	public void init(){
		map= new ReadSettingFileUtils().getFileContent();
		 for (Entry<String, String> entry : map.entrySet()) {
				log.info("key:"+entry.getKey()+",value:"+entry.getValue());
			}
	}
	public static Map<String, String> getMap() {
		return map;
	}
	public static void setMap(Map<String, String> map) {
		MapRegister.map = map;
	}
}
