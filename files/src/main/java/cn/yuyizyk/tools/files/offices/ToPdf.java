package cn.yuyizyk.tools.files.offices;

import java.io.File;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;

/**
 * PDF工具 <详细功能描述>
 * 
 * @author jing
 * @date 2014-10-30
 */
public class ToPdf {

	static final int wdDoNotSaveChanges = 0;// 不保存待定的更改。
	static final int wdFormatPDF = 17;// PDF 格式

	public void pdf(String pathW, String pathP) {

		// System.out.println("启动Word...");
		// long start = System.currentTimeMillis();
		ActiveXComponent app = null;
		try {
			app = new ActiveXComponent("Word.Application");
			app.setProperty("Visible", false);

			Dispatch docs = app.getProperty("Documents").toDispatch();
			// System.out.println("打开文档..." + pathW);
			Dispatch doc = Dispatch.call(docs,//
					"Open", //
					pathW,// FileName
					false,// ConfirmConversions
					true // ReadOnly
					).toDispatch();
			File tofile = new File(pathP);
			if (tofile.exists()) {
				tofile.delete();
			}
			// System.out.println("转换文档到PDF..." + pathP);
			Dispatch.call(doc,//
					"SaveAs", //
					pathP, // FileName
					wdFormatPDF);

			Dispatch.call(doc, "Close", false);
			// long end = System.currentTimeMillis();
			// System.out.println("转换完成..用时：" + (end - start) + "ms.");
		} catch (Exception e) {
			System.out.println("========Error:文档转换失败：" + e.getMessage());
		} finally {
			if (app != null)
				app.invoke("Quit", wdDoNotSaveChanges);
		}
	}
}
