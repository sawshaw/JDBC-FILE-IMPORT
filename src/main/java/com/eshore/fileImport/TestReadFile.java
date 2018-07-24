package com.eshore.fileImport;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TestReadFile {
	public static void main(String[] args) {
		ReadSettingFileUtils u=new ReadSettingFileUtils();
		Map<String, String> map = new HashMap<String, String>();
		map=u.getFileContent();
		 for (Entry<String, String> entry : map.entrySet()) {
			System.out.println("key:"+entry.getKey()+",value:"+entry.getValue());
		}
		 
	}

}
