package cn.yuyizyk.tools.files.offices;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

import org.apache.log4j.Logger;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

import cn.yuyizyk.tools.common.Strings;

/**
 * 
 * 在线预览转PDF,SWF工具类
 * @备注需安装opnOffice(并开服务)
 * @或MSOffice(需下载jacob-1.18-M2-x64.dll、jacob-1.18-M2-x84.dll 放置jdk bin目录下)
 * @备注 转swf需下载pdf2swf.exe
 * @author hong
 */
public class OnlinePDFPreview {
	
	private static final Logger logger = Logger.getLogger(OnlinePDFPreview.class);

	private File file;
	private File pdfFile;
	private	File swfFile;
    
    /**
     * 将word转换为PDF或SWF
     * @注意tomcat8 中文文件名不用转码
     * @param pathName 文件路径
     * @param fileName 文件名
     * @param swfToolsPath swf的转换工具路径：d:\pdf2swf.exe
     * @param ifSWF  true:转为SWF和PDF false:只转换PDF
     * @return 文件名是返回的文件名是16进制字符串
     */
	public String converter(String pathName,String fileName,String swfToolsPath,boolean ifSWF) throws Exception{

		String pathAndName = null;
		String pathAndName_pdf = null;
		String pathAndName_swf = null;
		
//		try {
//			String new_fileName = new String(fileName.getBytes("ISO-8859-1"),"UTF-8");
			String new_fileName = Strings.convertStringTo16(fileName.split("\\.")[0]);
			pathAndName = pathName + fileName;
			pathAndName_pdf = pathName + new_fileName + ".pdf"; //PDF目标文件
			if(ifSWF) pathAndName_swf = pathName + new_fileName + ".swf"; //SWF目标文件
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		file = new File(pathAndName); //源文件
		pdfFile = new File(pathAndName_pdf); //PDF目标文件
		if(ifSWF) swfFile = new File(pathAndName_swf); //SWF目标文件

		//PDF转换-----------------------------------------------------------------------------
		if(file.exists()){
			if(!pdfFile.exists()){
				if(fileName.indexOf(".xls") > -1) ExcelUtil.setExcelUtilScaleFactor(pathAndName,60); //格式化Excel的打印范围
				OpenOfficeConnection connection = new SocketOpenOfficeConnection("localhost",8100);
				try {
					connection.connect();
					DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
//					DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
					converter.convert(file, pdfFile);
					pdfFile.createNewFile();
					connection.disconnect();

				} catch (ConnectException e) {
					// TODO Auto-generated catch block
					logger.info("openOffice服务未启动 转换失败,准备msOffice转换......");
					ToOfficeUtil.converWrod(pathAndName, pathAndName_pdf);
//					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				//logger.inf("pdf文件已存在不需转换！");
			}
		}else{
			//logger.inf("需要转换的文件不存在！");
			throw new Exception("需要转换WORD的文件不存在！" + pathAndName);
		}

		//SWF转换------------------------------------------------------------------------------
		if(ifSWF){
			Runtime r = Runtime.getRuntime();
			if(!swfFile.exists()){
				if(pdfFile.exists()){
					try {
						Process pc = r.exec(swfToolsPath + " " + pdfFile.getPath() + " -o " + swfFile.getPath() + " -T9");
						pc.waitFor();
						swfFile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					//logger.inf("pdf文件不存在");
					throw new Exception("需要转换PDF的文件不存在！" + pathAndName);
				}
			}else{
				//logger.inf("swf文件已存在！");
			}
		}
		if(ifSWF) return "{fileName:'"+ swfFile.getName() +"'}";
		return "{fileName:'"+ pdfFile.getName() +"'}";
	}
}
