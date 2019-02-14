package cn.yuyizyk.tools.files.ftl;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.setup.BaseAction;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 静态资源生成器
 * 
 * @author yuyi
 *
 */
public class FreemakerTools {
	private static final Logger log = LoggerFactory.getLogger(BaseAction.class);

	/**
	 * 根据模板生成文件
	 * 
	 * @param templateDirectoryPath
	 *            ftl文件目录绝对路径
	 * @param ftlPath
	 *            跟ftl文件目录的抽象文件路径
	 * @param filePath
	 *            生成文件绝对路径
	 * @param map
	 *            ftl map data
	 * @return 返回该资源的相对路径
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static Path create(Path templateDirectoryPath, Path ftlPath, Path filePath, Map<String, ?> map)
			throws IOException, TemplateException {
		assert Files.exists(ftlPath);

		if (!Files.exists(filePath)) {
			if (!Files.exists(filePath.getParent())) {
				Files.createDirectories(filePath.getParent());
			}
			Files.createFile(filePath);
		}
		Configuration cfg = new Configuration(Configuration.getVersion());
		// cfg.setClassForTemplateLoading(Thread.currentThread().getContextClassLoader().getClass(),
		// "../templates/");// 类路径

		// 指定Configuration寻找模版文件的目录。

		cfg.setDirectoryForTemplateLoading(templateDirectoryPath.toFile());
		// cfg.setDirectoryForTemplateLoading(ResourceUtils.getFile("classpath:templates/index.ftl"));
		// cfg.setClassForTemplateLoading(Thread.currentThread().getContextClassLoader().getClass(),
		// "/");
		cfg.setDefaultEncoding("UTF-8");
		// 2.在模板文件目录中找到名称为name的模版文件
		Template temp = cfg.getTemplate(ftlPath.toString());
		// 3.定义静态页面输出文件
		FileWriter out = new FileWriter(filePath.toFile());
		// 4.输出静态页面。第一个参数是Map<String,Object>格式的数据，第二个参数是文件输出流。输出后别忘了out.close();
		temp.process(map, out);
		return filePath;
	}
}
