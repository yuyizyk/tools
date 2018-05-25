package cn.yuyizyk.tools.files.offices;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

public class SwfFileUploadUtil {
	private static final Logger logger = Logger.getLogger(SwfFileUploadUtil.class);

	/**
	 * 上传文件
	 * 
	 * @param savePath
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean uploadFile(String savePath, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		File f1 = new File(savePath);
		if (!f1.exists()) {
			f1.mkdirs();
		}
		DiskFileItemFactory fac = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fac);
		upload.setHeaderEncoding("utf-8");
		List fileList = null;
		try {
			fileList = upload.parseRequest(request);
		} catch (FileUploadException ex) {
			ex.printStackTrace();
		}
		Iterator<FileItem> it = fileList.iterator();
		String name = "";
		while (it.hasNext()) {
			FileItem item = it.next();
			if (!item.isFormField()) {
				name = item.getName();
				// 更新的时候去删除原来的文件，添加的时候判断文件是否存在；
				if (null != request.getAttribute("type")) {
					if (0 == Integer.valueOf(request.getAttribute("type").toString())) {
						Files.deleteIfExists(Paths.get(savePath + "/" + name));
					}
				} else {
					if (Files.exists(Paths.get(savePath, name))) {
						response.getWriter().print("ERROR:" + name + "文件名已存在！");
						return false;
					}
				}
				if (name.indexOf(" ") > -1 || name.split("\\.").length > 2) {
					response.getWriter().print("ERROR:" + name + " 文件名中不能有空格或非法符号.*！");
					return false;
				}
				long size = item.getSize();
				String type = item.getContentType();
				logger.info(size + "字节" + " " + type);
				if (name == null || name.trim().equals("")) {
					continue;
				}
				File file = null;
				do {
					file = new File(savePath + name);
				} while (file.exists());
				File saveFile = new File(savePath + name);
				try {
					item.write(saveFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		response.getWriter().print(savePath + name);
		response.getWriter().close();
		response.getWriter().flush();
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static InputStream getFileInputStream(HttpServletRequest request) {
		try {
			DiskFileItemFactory fac = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(fac);
			upload.setHeaderEncoding("utf-8");
			List fileList = null;
			try {
				fileList = upload.parseRequest(request);
			} catch (FileUploadException ex) {
				ex.printStackTrace();
			}
			Iterator<FileItem> it = fileList.iterator();
			while (it.hasNext()) {
				FileItem item = it.next();
				if (item != null) {
					return item.getInputStream();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
