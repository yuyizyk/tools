package cn.yuyizyk.tools.files.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class SmbTools {

	/**
	 * 提取共享文件里的内容
	 * 
	 */
	public static String getFileInfo(String url){
        SmbFile file;
		try {
			file = new SmbFile(url);
			BufferedReader in; //远程目录
			StringBuffer sb= new StringBuffer("");
	        if(file.exists()){ //判断是否是文件夹
	                //logError((f.getName()) + " " + f.lastModified());
//                    if(f.lastModified() >= nowDate){
                    //if(isPattern(f.getName(),ps)){
                    //logError((f.getName()) + "---------------------");
//	                	length = f.getContentLength();//得到文件的大小
//	                	buffer = new byte[length];
	                	in = new BufferedReader(new InputStreamReader(new SmbFileInputStream(file),"GB2312"));
	                	String line = null;
	                	while((line = in.readLine())  != null){
	                		sb.append(line+"\r\n");
	                	}
	                	in.close();
	                	return sb.toString();
	                	//f.delete(); //删除文件
	        }else{
	        	System.out.println("文件不存在！");
            }
	        return sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SmbException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 得到文件名
	 * @param url
	 * @return
	 */
	public static List<Map<String,String>> getFileNames(String url){
        SmbFile file;
		try {
			file = new SmbFile(url);
			List<Map<String,String>> list_map = new ArrayList<Map<String,String>>();
	        if(file.exists()){ //判断是否是文件夹
	            SmbFile[] files = file.listFiles();
	            Map<String,String> map = null;
	            for(int i =0;i < files.length;i++){
	            	map = new HashMap<String, String>();
                    SmbFile f = files[i];
                    if(f.exists()){ //判断是否是文件夹
                    	map.put("fileName", f.getName());
                    	map.put("filePath", f.getPath());
                    	list_map.add(map);
                    }else{
                    	System.out.println("文件不存在！");
                    }
	            }
	            return list_map;
	        }else{
	        	System.out.println("文件不存在！");
            }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SmbException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String agar []){
//		String MSTSC_PROPERTIES_URL = "smb://administrator:hong@192.168.2.99/ftplog/"; //远程配置文件地址
		System.out.println(getFileInfo("smb://administrator:hong@192.168.2.99/ftplog/log_db_20150722.txt"));
//		getFileNames(MSTSC_PROPERTIES_URL);
	}
}
