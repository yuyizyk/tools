package cn.yuyizyk.tools.files.offices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * 按指定模板生成word文档 需下载jacob-1.18-M2-x64.dll、jacob-1.18-M2-x84.dll 放置jdk bin目录下
 * 
 * @author hong
 */
public class ToOfficeUtil {

	/**
	 * 初始化配置生成模板配置对象
	 * 
	 * @param request
	 * @param temp
	 * @return
	 * @throws IOException
	 */
	private static Template configTemplate(String tempName, String tempPath) throws IOException {
		Configuration config = new Configuration();
		// ServletContext sc = request.getSession().getServletContext();
		// config.setDirectoryForTemplateLoading(new File(sc.getRealPath(tempPath)));
		config.setDirectoryForTemplateLoading(new File(tempPath));
		config.setObjectWrapper(new DefaultObjectWrapper());
		Template template = config.getTemplate(tempName, "UTF-8");
		return template;
	}

	/**
	 * 生成文档
	 * 
	 * @param request
	 * @param tempName
	 *            模板文件名（写好模板后改后缀'.ftl'）
	 * @param tempPath
	 *            模板路径
	 * @param root
	 *            生成word 文档的数据集
	 * @param pathFileName
	 *            生成文档指定文件名路径（文件名不能带后缀,默认doc后缀）
	 * @author hong WORD_TEMPLATE01 = "template_plan.ftl"; //模板名 WORD_TEMPLATE02 =
	 *         "template_progarm.ftl"; TEMPLATE_PATH = "/docTemplate/"; //模板文件的路径
	 *         TEMPLATE_PATH_NEW = "/uploads/"; //指定生成文件的路径
	 */
	public static String toPreview(String tempName, String tempPath, Map<?, ?> root, String pathFileName) {
		String previewPath_xml = null;
		String previewPath_doc = null;
		try {
			previewPath_xml = pathFileName + ".xml";
			previewPath_doc = pathFileName + ".doc";
			Template template = configTemplate(tempName, tempPath);
			FileOutputStream fos = new FileOutputStream(previewPath_xml);
			Writer out = new OutputStreamWriter(fos, "UTF-8");
			template.process(root, out);
			out.flush();
			out.close();
			converWrod(previewPath_xml, previewPath_doc);
			File file_xml = new File(previewPath_xml);
			file_xml.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return previewPath_doc;
	}

	/**
	 * word另存为
	 * 
	 * @param inFile
	 *            需要转换的文件名路径
	 * @param tpFile
	 *            转换过后的文件名路径
	 */
	public static void converWrod(String inFile, String tpFile) {
		ActiveXComponent app = null;
		try {
			if (inFile.indexOf(".xls") > -1) {
				app = new ActiveXComponent("Word.Application"); // 要转换的word文件
			} else if (inFile.indexOf(".doc") > -1) {
				app = new ActiveXComponent("Excel.Application"); // 要转换的word文件
			} else if (inFile.indexOf(".ppt") > -1) {
				app = new ActiveXComponent("Powerpoint.Application"); // 要转换的word文件
			} else {
				throw new Exception("Office 不能转换此类文件！" + inFile);
			}
			boolean visible = false;
			// 设置word不可见
			app.setProperty("Visible", new Variant(visible));
			// logger.inf("设置word不可见成功!");
			Dispatch docs = app.getProperty("Documents").toDispatch();
			Dispatch doc = Dispatch.invoke(docs, "Open", Dispatch.Method,
					new Object[] { inFile, new Variant(false), new Variant(true) }, new int[1]).toDispatch(); // 打开word文件
																												// //在word2003的vba文档中application有UserName属性。
																												// String
																												// userName=app.getPropertyAsString("UserName");
																												// logger.inf("用户名："+userName);
																												// Dispatch
																												// selection=app.getProperty("Selection").toDispatch();
																												// //得到一个组件
																												// logger.inf("Selection");
																												// Dispatch
																												// find
																												// =
																												// app.call(selection,
																												// "Find").toDispatch();

			// 保存文件
			Dispatch.invoke(doc, "SaveAs", Dispatch.Method, new Object[] { tpFile, new Variant(0) }, new int[1]);
			// 作为word格式保存到目标文件
			Variant f = new Variant(false);
			Dispatch.call(doc, "Close", f);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			app.invoke("Quit", new Variant[] {});
		}
	}

}
