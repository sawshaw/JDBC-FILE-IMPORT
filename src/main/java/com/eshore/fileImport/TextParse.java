package com.eshore.fileImport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author mercy
 *文件解析器
 *数据库入库
 */
public class TextParse extends TimerTask{
	public static Logger logger = LoggerFactory.getLogger(TextParse.class);
	
	 //每次读取的最大文件数
	 //private int maxfiles = 500;
	 private int maxfiles =0;
	 //锁所在目录
	 //private String fileLockPath="D:/fileTest1/lock/";
	 private String fileLockPath="";
	 //锁定文件锁
	// private String fileLock="FILE.LOCK";
	 private String fileLock="";
	// private int flag=1;
	 //private  String localDir="D:/fileTest1/old/";
	 private  String localDir="";
	 //每隔1秒处理一个文件
	 //private int intevalTime = 1000;
	 private int intevalTime=0;
	 //文件备份目录
	// private String localBakDir="D:/fileTest1/new/";
	 private String localBakDir="";
	 //表名称
	 private String tableName="";
	 private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 public void run(){
		 maxfiles=Integer.parseInt(MapRegister.getMap().get("maxfiles"));
		 fileLockPath=MapRegister.getMap().get("fileLockPath");
		 fileLock=MapRegister.getMap().get("fileLock");
		 localDir=MapRegister.getMap().get("localDir");
		 intevalTime=Integer.parseInt(MapRegister.getMap().get("intevalTime"));
		 localBakDir=MapRegister.getMap().get("localBakDir");
		 tableName=MapRegister.getMap().get("tableName");
		 logger.info("run...."+sdf.format(new Date()));
		// while(true){
		 //检测是否存在锁
		 if(isFileLocked()){
			 //不存在文件锁则创建文件锁
			 if(!getFileLock()){
				 return;
			 }
			 String fileList=getFileList(localDir,maxfiles);
			 for(String myfile: fileList.split(",")){
				 if(myfile!=null&&myfile.length()>0){
					 //做导入文件到表操作
					 //System.out.println("导入文件....");
					 logger.info("process file:{}",myfile);
					 doInsertTables(myfile,tableName);
					 //移除文件操作
					// System.out.println("移动文件....");
					 if (!removeFiles(myfile,localDir,localBakDir, ""))
			            {
						  //移动文件失败就配一个新文件名再移动
			              String newFileName = "RP_" + myfile + "_" + getCurTimeString();
			              removeFiles(myfile, localDir, localBakDir, newFileName);
			            }
					 try {
			              logger.info("sleep for a moment...");
			              Thread.sleep(intevalTime);
			            } catch (InterruptedException e) {
			              logger.error(e.toString());
			            }
				 }
			 }
		 }
		 //释放文件锁
		releaseFileLock();
		 //}
		 
	 }
	 
	 //判断是否有文件锁
	 public boolean isFileLocked(){
		 File lockFile = new File(fileLockPath + fileLock);
		 if (lockFile.exists()) {
			 logger.info("the file is locked");
		      return false;
		    }
		 return true;
		 
	 }
	 //获取文件锁
	 public boolean getFileLock(){
		 File file = new File(fileLockPath); 
		 //创建文件锁所在的目录
		 if(!file.exists()){ 
			 file.mkdirs(); 
			}
		 File f = new File(fileLockPath + fileLock);
		 try {
			 logger.info("create file lock");
			f.createNewFile();
		} catch (IOException e) {
			logger.info("get file lock error...."+e.getMessage());
			return false;
		}
		return true;
		 
	 }
	 //释放文件锁
	 public boolean releaseFileLock(){
		 logger.info("release lock");
		 File f = new File(fileLockPath +fileLock);
		 try{
			 f.delete();
		 }catch(Exception e){
			 logger.info("release file lock error...."); 
			 return false;
		 }
		 return true;
		 
	 }
	
	 //获取文件名列表
	 public String getFileList(String filePath,int maxfiles){
		 logger.info("get fileName list");
	    String fileList = "";
	    File myfile = new File(filePath);
	    String[] fs = myfile.list();
	    int i = 0;
	    for (String f : fs) {
	    	if ((f.endsWith(".txt")) || (f.endsWith(".TXT"))) {
	    		fileList = fileList + f + ",";
	    		i++; 
	    		if (i >= maxfiles)
	    			break;
	    	}
	    }
	    if (fileList.endsWith(",")) {
	    	fileList = fileList.substring(0, fileList.length() - 1);
	    }
	    return fileList;
	  }
	 //移动源文件到目标目录
	 public boolean removeFiles(String f, String filePath, String dPath, String newfilename)
	  {
	    if (f.length() < 0) 
	    	return false;
	    File file=new File(dPath);
	    //创建目标文件目录
	    if(!file.exists()){
	    	file.mkdirs(); 
	    }
	    File fl = new File(filePath + f);
	    if ((newfilename != null) && (newfilename.trim().length() > 0)){
	    	f = newfilename;
	    }
	    File nf = new File(dPath + f);
	    if (nf.exists()) {
	    	logger.info("目标数据已经存在，");
	      return false;
	    }
	    fl.renameTo(nf);
	    return true;
	  }
	 //解析文件
	 //格式 
	 //82750854       ,1688588530     ,20170401,000244,22        ,1       ,0.00      ,      ,020       ,1682
	 //82750854       ,1688588510     ,20170401,000404,23        ,1       ,0.00      ,      ,020       ,1682
	 //82750854       ,1688588505     ,20170401,000535,39        ,1       ,5.00      ,      ,020       ,1682
	 //36454810       ,1688311160     ,20170401,000755,17        ,1       ,30.00     ,      ,020       ,1682
	 public void doInsertTables(String fileName,String tableName){
		 StringBuffer sbuf = new StringBuffer();
	        try{
	        	String fileNamebak=fileName;
	        	fileName=localDir+fileName;
	        //InputStream is = TextParse.class.getResourceAsStream(fileName);
	        InputStream is = new FileInputStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			String line = reader.readLine();
			//System.out.println("line:"+line);
			while (null != line) {
				sbuf.append(line).append("\n"); 
				//转换
				int i=0;
				String[] lines=line.split(",",-1);
				/*for(String lin:lines){
					System.out.println(i++);
					System.out.println("==="+lin);
				}*/
				//写数据库
				insertTable(lines,fileNamebak,tableName);
				//System.out.println("换行");
				//for循环转换成对象
				line = reader.readLine();
			}
			reader.close();//关闭reader就行了，is.close()不必 
	        }catch(IOException e){
	        	logger.info("读取异常...");
	        }
			//System.out.println("输出读取的txt\n"+sbuf.toString()+"\n");
	}
	 private static void insertTable(String[] lines,String fileName,String tableName) {
		//Model model=setModel(lines);
		Dbutils util=new Dbutils();
		String sql="insert into "+tableName+ " values(?,?,?,?,?,?,?,?,?,?,?,?)";
		String now=sdf.format(new Date());
		util.update(sql, lines[0],lines[1],lines[2],lines[3],lines[4],lines[5],lines[6],lines[7],lines[8],lines[9],now,fileName);
		
	}
	 private static String getCurTimeString() {
	    return sdf.format(new Timestamp(System.currentTimeMillis()));
	 }

	public int getMaxfiles() {
		return maxfiles;
	}

	public void setMaxfiles(int maxfiles) {
		this.maxfiles = maxfiles;
	}

	public String getFileLockPath() {
		return fileLockPath;
	}

	public void setFileLockPath(String fileLockPath) {
		this.fileLockPath = fileLockPath;
	}

	public String getLocalDir() {
		return localDir;
	}

	public void setLocalDir(String localDir) {
		this.localDir = localDir;
	}

	public int getIntevalTime() {
		return intevalTime;
	}

	public void setIntevalTime(int intevalTime) {
		this.intevalTime = intevalTime;
	}

	public String getLocalBakDir() {
		return localBakDir;
	}

	public void setLocalBakDir(String localBakDir) {
		this.localBakDir = localBakDir;
	}

	public void setFileLock(String fileLock) {
		this.fileLock = fileLock;
	}
	class student{
		private int id;
		private String name;
		private float core;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public float getCore() {
			return core;
		}
		public void setCore(float core) {
			this.core = core;
		}
	}
	public static void main(String[] args){
		//String fileName="/test.txt";
		//doInsertTables(fileName);
		new TextParse().run();
	}
}