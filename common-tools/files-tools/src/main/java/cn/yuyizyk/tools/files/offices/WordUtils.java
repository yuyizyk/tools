package cn.yuyizyk.tools.files.offices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;

public class WordUtils {

	/**
	 * 生成word
	 * @param imgs 图片对象
	 * @param imgHW 图片高宽
	 * @param wordPath word模板位置
	 * @param newWordPath 生成word
	 * @param data word中的文字数据
	 * @param table_data 
	 */
	
	public void toWord(Map<String,File> imgs,Map<String,int []> imgHW,String wordPath,String newWordPath,Map<String,Object> data, List<Map<String, Object>> tabledata){
		try{  
			OPCPackage pack = POIXMLDocument.openPackage(wordPath);
			//XWPFDocument doc = new XWPFDocument(pack);  
			CustomXWPFDocument doc = new CustomXWPFDocument(pack);

			//替换work模板图片
			replacePictures(imgs,imgHW,doc);
			
			//创建表格
			createTabToData(tabledata,doc);

			//替换段落文字
			//replaceInPara(doc);
			//替换表格中的文字
			replaceInTable(doc,data);


			FileOutputStream fos = new FileOutputStream(newWordPath);
			doc.write(fos);  
			fos.flush();
			fos.close();
		}catch(Exception e){  
			e.printStackTrace();  
		}
	}

	/**
	 * 替换模板中的所有图片
	 * @param imgs
	 * @param doc
	 */
	private void replacePictures(Map<String,File> imgs,Map<String,int []> imgHW,CustomXWPFDocument doc){
		try{
			Iterator<String> it_img = imgs.keySet().iterator();
			while (it_img.hasNext()) {
				String img_name = it_img.next();

				Iterator<XWPFTable> it = doc.getTablesIterator();
				while(it.hasNext()){
					XWPFTable table = it.next();
					List<XWPFTableRow> rows = table.getRows();
					for(XWPFTableRow row:rows){  
						List<XWPFTableCell> cells = row.getTableCells();
						for(XWPFTableCell cell:cells){
							if(cell.getText().endsWith(img_name)){
								//图片流对象
								if(!imgs.get(img_name).exists()) continue;
								FileInputStream is = new FileInputStream(imgs.get(img_name));
								cell.removeParagraph(0);  
								XWPFParagraph pargraph = cell.addParagraph();  
								//100为宽，150为高  
								doc.addPictureData(is, Document.PICTURE_TYPE_PNG);  
								int [] hw = imgHW.get(img_name);
								doc.createPicture(doc.getAllPictures().size()-1, hw[0], hw[1],pargraph);
								is.close();
							}
							List<XWPFParagraph> pars = cell.getParagraphs();  
							for(XWPFParagraph par:pars){
								List<XWPFRun> runs = par.getRuns();
								for(XWPFRun run:runs){
									run.removeBreak();
								}
							}
						}
					}
				}
			}
		}catch(Exception e){  
			e.printStackTrace();  
		}

	}

	/**
	 * 添加表格
	 * @param data 表格中的数据
	 * @param doc
	 */
	private void createTabToData(List<Map<String,Object>> data,CustomXWPFDocument doc){

		Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
		XWPFParagraph para;
		while (iterator.hasNext()) {
			para = iterator.next();
			String table_name = para.getText();
			if(table_name.contains("table_")){
				//移除 变量
				List<XWPFRun> list_runs = para.getRuns();
				while(list_runs.size() != 0){
					para.removeRun(0);
				}
				int row_len = data.size() + 1;
				if(row_len == 0) return;
				int col_len = data.get(0).size();
				//创建表格
				XWPFTable table = doc.createTable(row_len, col_len);
				// 设置上下左右四个方向的距离，可以将表格撑大
				//表格属性
				CTTblPr tablePr = table.getCTTbl().addNewTblPr();
				//表格宽度
				CTTblWidth width = tablePr.addNewTblW();
				width.setW(BigInteger.valueOf(8000));
				
				
				//表头设置--------------------------------
				List<XWPFTableCell> tableCells1 = table.getRow(0).getTableCells();
				Map<String,Object> table_hand = data.get(0);
				Iterator<String> it_hand = table_hand.keySet().iterator();
				int i = 0;
				while (it_hand.hasNext()) {
					String hand_name = it_hand.next();
					//						tableCells1.get(i).setText(hand_name);
					//样式设置
					XWPFParagraph p1 = tableCells1.get(i).getParagraphs().get(0);
					XWPFRun r1 = p1.createRun();
					r1.setText(hand_name);
					r1.setBold(true);
//					r1.setItalic(true);
					r1.setFontFamily("宋体");
					p1.setAlignment(ParagraphAlignment.CENTER);
					i++;
				}
				//往表格中填数据---------------------------
				for(int j = 1;j < row_len; j++){
					tableCells1 = table.getRow(j).getTableCells();
					Map<String,Object> table_data = data.get(j-1);
					Iterator<String> it_data = table_data.keySet().iterator();
					int g = 0;
					while (it_data.hasNext()) {
						String hand_name = it_data.next();
						tableCells1.get(g).setText(table_data.get(hand_name).toString());
						g++;
					}
				}

			}
		}
	}


//	public static void main(String agrs[]){
//		Map<String,File> file_img = new HashMap<String, File>();
//		File file1 = new File("E:\\apache-tomcat-8.0.9-windows-x64\\apache-tomcat-8.0.9\\webapps\\STA\\resources\\images\\host_device.png");
//		File file2 = new File("E:\\apache-tomcat-8.0.9-windows-x64\\apache-tomcat-8.0.9\\webapps\\STA\\templateWord\\png\\雷达图.png");
//		File file3 = new File("E:\\apache-tomcat-8.0.9-windows-x64\\apache-tomcat-8.0.9\\webapps\\STA\\templateWord\\png\\状态一天环比.png");
//		File file4 = new File("E:\\apache-tomcat-8.0.9-windows-x64\\apache-tomcat-8.0.9\\webapps\\STA\\templateWord\\png\\综合评估.png");
//		file_img.put("${img_devicename}", file1);
//		file_img.put("${img_line}", file2);
//		file_img.put("${img_leida}", file3);
//		file_img.put("${img_zh}", file4);
//		Map<String,int []> img_hw = new HashMap<String, int[]>();
//		img_hw.put("${img_devicename}",new int[]{250,220});
//		img_hw.put("${img_line}",new int[]{400,270});
//		img_hw.put("${img_leida}",new int[]{400,270});
//		img_hw.put("${img_zh}",new int[]{400,270});
//		String wordPath = "C:\\Users\\hong\\Desktop\\word.docx";
//		String newWordPath = "C:\\Users\\hong\\Desktop\\word1.docx";
//		Map<String,Object> data = new HashMap<String, Object>();
//		data.put("text_a", "非常厉害");
//		List<Map<String,Object>> tabledata = new ArrayList<Map<String,Object>>();
//		Map<String,Object> data2 = new HashMap<String, Object>();
//		data2.put("指标分类", "主机设备");
//		data2.put("指标名称", "cpu使用率");
//		tabledata.add(data2);
//		WordUtils main = new WordUtils();
//		main.toWord(file_img, img_hw, wordPath, newWordPath,data, tabledata);
//	}

	/**
	 * 替换段落里面的变量
	 * @param doc 要替换的文档
	 * @param params 参数
	 */
	//	private  void replaceInPara(XWPFDocument doc, Map<String, Object> params) {
	//		Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
	//		XWPFParagraph para;
	//		while (iterator.hasNext()) {
	//			para = iterator.next();
	//			this.replaceInPara(para, params);
	//		}
	//	}

	/**
	 * 替换段落里面的变量
	 * @param para 要替换的段落
	 * @param params 参数
	 */
	private void replaceInPara(XWPFParagraph para, Map<String, Object> params) {
		List<XWPFRun> runs;
		//	      Matcher matcher;
		if (this.matcher(para.getParagraphText()).find()) {
			runs = para.getRuns();
			for (int i=0; i<runs.size(); i++) {
				XWPFRun run = runs.get(i);
				String runText = run.toString();
				//	            matcher = this.matcher(runText);
				Object val = params.get(runText);
				if (val != null) {
					//	                while ((matcher = this.matcher(runText)).find()) {
					//	                   runText = matcher.replaceFirst(String.valueOf(params.get(matcher.group(1))));
					//	                }
					//直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
					//所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。
					para.removeRun(i);
					para.insertNewRun(i).setText(val.toString());
					//	                i++;
				}else{
					if(runs.size()==1 && !"}".equals(runText))continue;
					para.removeRun(i);
					i=-1;
				}
			}
		}
	}

	/**
	 * 替换表格里面的变量
	 * @param doc 要替换的文档
	 * @param params 参数
	 */
	private void replaceInTable(CustomXWPFDocument doc, Map<String, Object> params) {
		Iterator<XWPFTable> iterator = doc.getTablesIterator();
		XWPFTable table;
		List<XWPFTableRow> rows;
		List<XWPFTableCell> cells;
		List<XWPFParagraph> paras;
		while (iterator.hasNext()) {
			table = iterator.next();
			rows = table.getRows();
			for (XWPFTableRow row : rows) {
				cells = row.getTableCells();
				for (XWPFTableCell cell : cells) {
					paras = cell.getParagraphs();
					for (XWPFParagraph para : paras) {
						replaceInPara(para, params);
					}
				}
			}
		}
	}

	/**
	 * 正则匹配字符串
	 * @param str
	 * @return
	 */
	private Matcher matcher(String str) {
		Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		return matcher;






	}
}
