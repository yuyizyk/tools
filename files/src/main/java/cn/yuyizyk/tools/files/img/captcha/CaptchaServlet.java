package cn.yuyizyk.tools.files.img.captcha;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 验证码Servlet
 * 
 */
public class CaptchaServlet {
	public static final String DEFAULT_CAPTCHA_SERVICE_ID = "captchaService";
	public static final String CAPTCHA_SERVICE_ID = "captchaServiceId";
	private MyImageCaptchaService captchaService;

	public void init(MyImageCaptchaService captchaService) throws ServletException {
		this.captchaService = captchaService;
	}

	private void challenge(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Set to expire far in the past.
		resp.setDateHeader("Expires", 0);
		// Set standard HTTP/1.1 no-cache headers.
		resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		resp.addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Set standard HTTP/1.0 no-cache header.
		resp.setHeader("Pragma", "no-cache");
		// return a jpeg
		resp.setContentType("image/jpeg");

		ServletOutputStream out = resp.getOutputStream();

		try {
			String captchaId = req.getSession(true).getId();
			// create the image with the text
			BufferedImage challenge = captchaService.getImageChallengeForID(captchaId, Locale.ENGLISH);
			// write the data out
			ImageIO.write(challenge, "jpg", out);
			out.flush();
		} finally {
			out.close();
		}
	}

	private void tryResponse(String captcha, HttpServletRequest req, HttpServletResponse resp) {
		boolean removeOnError = "true".equals(req.getParameter("removeOnError"));
		if (Captchas.isValidTry(captchaService, req, captcha, removeOnError)) {
			Servlets.writeHtml(resp, "true");
		} else {
			Servlets.writeHtml(resp, "false");
		}
	}
}
