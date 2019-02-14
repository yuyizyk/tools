package cn.yuyizyk.tools.files.img;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.yuyizyk.tools.files.img.Scalr.Mode;

/**
 * 来源于 TngouFS 图片系统 https://git.oschina.net/397713572/tngouFS
 * 
 * @author tngou
 *
 */
public class ImgTools {

	private final static Logger log = LoggerFactory.getLogger(ImgTools.class);

	/**
	 * 
	 * @Title: drawWhiteBackgroud @Description: TODO 画一张以白色背景的图片 @param @param width
	 * 图片宽度 @param @param height 图片高度 @param @return 设定文件 @return BufferedImage
	 * 返回类型 @throws
	 */
	public BufferedImage drawWhiteBackgroud(int width, int height) {

		int imageType = BufferedImage.TYPE_INT_RGB;
		BufferedImage bufferedImage = new BufferedImage(width, height, imageType);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.setBackground(Color.WHITE);// 设置背景色
		g2.clearRect(0, 0, width, height);// 通过使用当前绘图表面的背景色进行填充来清除指定的矩形。
		g2.dispose();
		return bufferedImage;
	}

	/**
	 * 
	 * @Title: drawEmptyBackgroud @Description: TODO 画一张以透明背景的图片 ，注意，透明背景存储格式 我饿PNG
	 * 如：ImageIO.write(image, "PNG", new File("")); @param @param width
	 * 图片宽度 @param @param height 图片高度 @param @return 设定文件 @return BufferedImage
	 * 返回类型 @throws
	 */
	public BufferedImage drawEmptyBackgroud(int width, int height) {

		int imageType = BufferedImage.TYPE_INT_ARGB;
		BufferedImage bufferedImage = new BufferedImage(width, height, imageType);
		return bufferedImage;
	}

	/**
	 * 
	 * @Title: Image @Description: TODO 图片转为 BufferedImage @param @param
	 * imageFile @param @return 设定文件 @return BufferedImage 返回类型 @throws
	 */
	public BufferedImage image(File imageFile) {
		try {
			BufferedImage bufferedImage = ImageIO.read(imageFile);
			return bufferedImage;
		} catch (IOException e) {
			log.error("异常:{}", e);
		}
		return null;
	}

	public BufferedImage image(String imageFilePath) {
		return image(new File(imageFilePath));
	}

	/**
	 * 
	 * @Title: resize @Description: TODO 调整图像的原始比例。 @param @param
	 * bufferedImage @param @param percentOfOriginal @param @return 设定文件 @return
	 * BufferedImage 返回类型 @throws
	 */
	public BufferedImage resize(BufferedImage bufferedImage, int percentOfOriginal) {
		int newWidth = bufferedImage.getWidth() * percentOfOriginal / 100;
		int newHeight = bufferedImage.getHeight() * percentOfOriginal / 100;
		return resize(bufferedImage, newWidth, newHeight);
	}

	public BufferedImage resize(BufferedImage bufferedImage, int newWidth, int newHeight) {

		int oldWidth = bufferedImage.getWidth();
		int oldHeight = bufferedImage.getHeight();
		if (newWidth <= 0 && newHeight <= 0)
			return bufferedImage; // 如果两者都小于等于 0 就返回原图

		if (newWidth * newHeight <= 0) // 如果一边数据小于等于零，就按照另外的一个比例
		{
			int percentOfOriginal = 100;
			if (newWidth <= 0) {
				percentOfOriginal = (int) (new Double(newHeight) / new Double(oldHeight) * 100);
			} else {
				percentOfOriginal = (int) (new Double(newWidth) / new Double(oldWidth) * 100);
			}

			return resize(bufferedImage, percentOfOriginal);
		}

		BufferedImage result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_BGR);

		double widthSkip = new Double(oldWidth - newWidth) / new Double(newWidth);
		double heightSkip = new Double(oldHeight - newHeight) / new Double(newHeight);

		double widthCounter = 0;
		double heightCounter = 0;

		int newY = 0;

		boolean isNewImageWidthSmaller = widthSkip > 0;
		boolean isNewImageHeightSmaller = heightSkip > 0;

		for (int y = 0; y < oldHeight && newY < newHeight; y++) {

			if (isNewImageHeightSmaller && heightCounter > 1) { // new image suppose to be smaller - skip row
				heightCounter -= 1;
			} else if (heightCounter < -1) { // new image suppose to be bigger - duplicate row
				heightCounter += 1;

				if (y > 1)
					y = y - 2;
				else
					y = y - 1;
			} else {

				heightCounter += heightSkip;
				int newX = 0;
				for (int x = 0; x < oldWidth && newX < newWidth; x++) {
					if (isNewImageWidthSmaller && widthCounter > 1) { // new image suppose to be smaller - skip column
						widthCounter -= 1;
					} else if (widthCounter < -1) { // new image suppose to be bigger - duplicate pixel
						widthCounter += 1;
						if (x > 1)
							x = x - 2;
						else
							x = x - 1;
					} else {

						int rgb = bufferedImage.getRGB(x, y);
						result.setRGB(newX, newY, rgb);
						newX++;
						widthCounter += widthSkip;
					}
				}

				newY++;
			}
		}

		return result;
	}

	/**
	 * 
	 * @Title: rotateLeft @Description: TODO 旋转图像的左90度。 @param @param
	 * bufferedImage @param @return 设定文件 @return BufferedImage 返回类型 @throws
	 */
	public BufferedImage rotateLeft(BufferedImage bufferedImage) {

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(height, width, BufferedImage.TYPE_INT_BGR);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int rgb = bufferedImage.getRGB(x, y);
				result.setRGB(y, x, rgb);
			}
		}

		return result;

	}

	/**
	 * 
	 * @Title: rotateRight @Description: TODO旋转图像的右90度。 @param @param
	 * bufferedImage @param @return 设定文件 @return BufferedImage 返回类型 @throws
	 */
	public BufferedImage rotateRight(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(height, width, BufferedImage.TYPE_INT_BGR);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int rgb = bufferedImage.getRGB(x, y);
				result.setRGB(height - y - 1, x, rgb);
			}
		}

		return result;

	}

	/**
	 * 
	 * @Title: rotate180 @Description: TODO 旋转180度 @param @param
	 * bufferedImage @param @return 设定文件 @return BufferedImage 返回类型 @throws
	 */
	public BufferedImage rotate180(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int rgb = bufferedImage.getRGB(x, y);
				result.setRGB(width - x - 1, height - y - 1, rgb);
			}
		}

		return result;

	}

	/**
	 * 
	 * @Title: crop @Description: TODO 截图，通过 ，xy-》xy @param @param
	 * bufferedImage @param @param startX @param @param startY @param @param
	 * endX @param @param endY @param @return 设定文件 @return BufferedImage
	 * 返回类型 @throws
	 */
	public BufferedImage crop(BufferedImage bufferedImage, int startX, int startY, int endX, int endY) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		if (startX == -1) {
			startX = 0;
		}

		if (startY == -1) {
			startY = 0;
		}

		if (endX == -1) {
			endX = width - 1;
		}

		if (endY == -1) {
			endY = height - 1;
		}

		if (endX > width) {
			endX = width;
		}
		if (endY > height) {
			endY = height;
		}
		BufferedImage result = drawWhiteBackgroud(endX - startX, endY - startY);
		// BufferedImage result = new BufferedImage(,
		// , BufferedImage.TYPE_INT_RGB);

		for (int y = startY; y < endY; y++) {
			for (int x = startX; x < endX; x++) {
				int rgb = bufferedImage.getRGB(x, y);
				result.setRGB(x - startX, y - startY, rgb);
			}
		}
		return result;
	}

	/**
	 * 
	 * @Title: crop @Description: 等比例截取，用填充的反思来完成 @param @param
	 * bufferedImage @param @param width 宽度 @param @param height 高度 @param @param
	 * transparent 是否透明 。true 透明 false白色 @param @return 设定文件 @return BufferedImage
	 * 返回类型 @throws
	 */
	public BufferedImage crop(BufferedImage bufferedImage, int width, int height, boolean transparent) {

		if (width <= 0 || height <= 0) // 如果截取 都小于 等于 0 就不剪切
		{
			return bufferedImage;
		}
		int w = bufferedImage.getWidth();
		int h = bufferedImage.getHeight();
		BufferedImage result = null;
		if (transparent) {
			result = drawEmptyBackgroud(width, height); // 透明背景
		} else {
			result = drawWhiteBackgroud(width, height); // 白色背景
		}

		int percentOfOriginal = 100;
		if (new Double(w) / new Double(width) > new Double(h) / new Double(height)) {
			percentOfOriginal = (int) (new Double(width) / new Double(w) * 100);
		} else {
			percentOfOriginal = (int) (new Double(height) / new Double(h) * 100);
		}

		bufferedImage = resize(bufferedImage, percentOfOriginal);
		w = bufferedImage.getWidth();
		h = bufferedImage.getHeight();
		int startX = (width - w) / 2;
		int startY = (height - h) / 2;

		result = pressImg(result, bufferedImage, startX, startY);

		return result;

	}

	/**
	 * 
	 * @Title: crop @Description: TODO 等比例剪切 ，不影响截取比例 不过会截取图片多余的内容 @param @param
	 * bufferedImage @param @param width 宽度 @param @param height 高度 @param @return
	 * 设定文件 @return BufferedImage 返回类型 @throws
	 */
	public BufferedImage crop(BufferedImage bufferedImage, int width, int height) {

		if (width <= 0 || height <= 0) // 如果截取 都小于 等于 0 就不剪切
		{
			return bufferedImage;
		}
		BufferedImage result = drawWhiteBackgroud(width, height); // 白色背景

		int w = bufferedImage.getWidth();
		int h = bufferedImage.getHeight();

		int percentOfOriginal = 100;
		if (new Double(w) / new Double(width) < new Double(h) / new Double(height)) {
			percentOfOriginal = (int) (new Double(width) / new Double(w) * 100);
		} else {
			percentOfOriginal = (int) (new Double(height) / new Double(h) * 100);
		}

		bufferedImage = resize(bufferedImage, percentOfOriginal);
		w = bufferedImage.getWidth();
		h = bufferedImage.getHeight();

		result = crop(bufferedImage, 0, 0, width, height);

		return result;

	}

	/**
	 * 
	 * @Title: emphasize @Description: TODO 遮罩，只显示指定区域 @param @param
	 * bufferedImage @param @param startX @param @param startY @param @param
	 * endX @param @param endY @param @return 设定文件 @return BufferedImage
	 * 返回类型 @throws
	 */

	public BufferedImage emphasize(BufferedImage bufferedImage, int startX, int startY, int endX, int endY) {
		return emphasize(bufferedImage, startX, startY, endX, endY, Color.BLACK, 3);
	}

	public BufferedImage emphasize(BufferedImage bufferedImage, int startX, int startY, int endX, int endY,
			Color backgroundColor) {
		return emphasize(bufferedImage, startX, startY, endX, endY, backgroundColor, 3);
	}

	public BufferedImage emphasize(BufferedImage bufferedImage, int startX, int startY, int endX, int endY, int jump) {
		return emphasize(bufferedImage, startX, startY, endX, endY, Color.BLACK, jump);
	}

	public BufferedImage emphasize(BufferedImage bufferedImage, int startX, int startY, int endX, int endY,
			Color backgroundColor, int jump) {

		// checkJump(jump);

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		if (startX == -1) {
			startX = 0;
		}

		if (startY == -1) {
			startY = 0;
		}

		if (endX == -1) {
			endX = width - 1;
		}

		if (endY == -1) {
			endY = height - 1;
		}

		for (int y = 0; y < height; y++) {
			for (int x = y % jump; x < width; x += jump) {

				if (y >= startY && y <= endY && x >= startX && x <= endX) {
					continue;
				}

				bufferedImage.setRGB(x, y, backgroundColor.getRGB());
			}
		}

		return bufferedImage;
	}

	/**
	 * 
	 * @Title: pressText @Description: TODO(这里用一句话描述这个方法的作用) @param @param
	 * bufferedImage @param @param pressText @param @param startX @param @param
	 * startY @param @param fontSize @param @return 设定文件 @return BufferedImage
	 * 返回类型 @throws
	 */
	public BufferedImage pressText(BufferedImage bufferedImage, String pressText, int startX, int startY,
			int fontSize) {
		String fontName = Font.SERIF;
		int fontStyle = Font.PLAIN;
		Color color = Color.RED;
		return pressText(bufferedImage, pressText, startX, startY, fontName, fontStyle, color, fontSize);
	}

	public BufferedImage pressText(BufferedImage bufferedImage, String pressText, int startX, int startY,
			String fontName, int fontStyle, Color color, int fontSize) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();

		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = result.createGraphics();
		g2d.drawImage(bufferedImage, 0, 0, width, height, null);
		// String s="www.qhd.com.cn";

		g2d.setFont(new Font(fontName, fontStyle, fontSize));
		g2d.setColor(color);

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
		g2d.drawString(pressText, startX, startY);
		g2d.dispose();

		return result;

	}

	public BufferedImage pressImg(BufferedImage bufferedImage, BufferedImage pressImg, int startX, int startY) {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		BufferedImage result = drawEmptyBackgroud(width, height);

		Graphics2D g2d = result.createGraphics();

		g2d.setBackground(Color.WHITE);// 设置背景色
		g2d.clearRect(startX, startY, pressImg.getWidth(), pressImg.getHeight());// 通过使用当前绘图表面的背景色进行填充来清除指定的矩形。

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
		g2d.drawImage(pressImg, startX, startY, null);
		g2d.dispose();

		return result;
	}

	/**
	 * 
	 * @Title: save @Description: TODO保存图片 @param @param bufferedImage @param @param
	 * file 设定文件 @return void 返回类型 @throws
	 */
	public File save(BufferedImage bufferedImage, File file) {
		return save(bufferedImage, "PNG", file);
	}

	public File save(BufferedImage bufferedImage, String pathname) {
		return save(bufferedImage, "PNG", new File(pathname));
	}

	public File save(BufferedImage bufferedImage, String formatName, String pathname) {
		return save(bufferedImage, formatName, new File(pathname));
	}

	public File save(BufferedImage bufferedImage, String formatName, File file) {

		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			ImageIO.write(bufferedImage, formatName, file);
		} catch (IOException e) {
			log.error("异常:{}", e);
		}
		return file;
	}

	/**
	 * 将静态图片生成GIF
	 * 
	 * @param path
	 */
	public static void jpgToGif(String path, List<String> list_urlfile, String gif_name, int setDelay) {
		// path = "E:\\Workspaces\\FRAPPS\\WebRoot\\imgFile\\radar\\";
		// List<String> list = new ArrayList<String>();
		// list.add("http://pi.weather.com.cn/i/product/pic/l/sevp_aoc_rdcp_sldas_ebref_ancn_l88_pi_20150929084000001.gif");
		// list.add("http://pi.weather.com.cn/i/product/pic/l/sevp_aoc_rdcp_sldas_ebref_ancn_l88_pi_20150929083000001.gif");
		// list.add("http://pi.weather.com.cn/i/product/pic/l/sevp_aoc_rdcp_sldas_ebref_ancn_l88_pi_20150929082000001.gif");
		try {
			AnimatedGifEncoder e = new AnimatedGifEncoder();
			e.setRepeat(0);
			e.start(path + gif_name);

			BufferedImage src;
			for (String string : list_urlfile) {
				src = ImageIO.read(new URL(string).openStream());// 读入文件
				e.setDelay(setDelay); // 1 frame per sec
				e.addFrame(src);
			}
			e.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param localpath
	 * @param rarpath
	 * @param wh
	 */
	public static void gifRAR(String localpath, String rarpath, float wh) {
		try {
			GifDecoder gd = new GifDecoder();
			int status = gd.read(new FileInputStream(new File(localpath)));
			if (status != GifDecoder.STATUS_OK) {
				return;
			}

			AnimatedGifEncoder ge = new AnimatedGifEncoder();
			ge.start(new FileOutputStream(new File(rarpath)));
			ge.setRepeat(0);

			for (int i = 0; i < gd.getFrameCount(); i++) {
				BufferedImage frame = gd.getFrame(i);
				int width = frame.getWidth();
				int height = frame.getHeight();
				// 80%
				width = (int) (width * wh);
				height = (int) (height * wh);

				BufferedImage rescaled = Scalr.resize(frame, Mode.FIT_EXACT, width, height);
				int delay = gd.getDelay(i);

				ge.setDelay(delay);
				ge.addFrame(rescaled);
			}

			ge.finish();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
