package com.eshore.fileImport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadSettingFileUtils {
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	//private static final String fileName="D:/fileTest1/properties/fileSettings.properties";
	//private static final String fileName="/home/billing/test/fileSettings.properties";
	private static final String fileName="/home/data/168/program/fileSettings.properties";
	private static final String projectFileName="/fileSettings.properties";
	private static  Properties properties = new Properties();
	public Map<String, String> getFileContent(){
		this.load();
		Map<String, String> map = new HashMap<String, String>((Map) properties);
		return map;
	}
	 /**
	 * 从文件里面读取不到配置的话就读取项目路径下的配置
	 * @author mercy
	 */
	public void load() {
	        if (null != properties) {
	            InputStream in = null;
	            try {
                    /* 从文件路径获取配置文件 */
                    logger.debug("classpath not found filename!!");
                    in = new FileInputStream(fileName);
                    properties.load(in);
	                logger.info("load config file success!");
	            } catch (FileNotFoundException e) {
	                logger.warn(" server config file not found!");
					in = this.getClass().getResourceAsStream(projectFileName);
					 try {
						properties.load(in);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
	            } catch (IOException e) {
	                logger.error("load config file error!", e);
	            } finally {
	                if (null != in) {
	                    try {
	                        in.close();
	                    } catch (IOException e) {
	                        logger.error("read config file error:", e);
	                    }
	                }
	            }
	        }
	    }
}
