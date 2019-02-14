package cn.yuyizyk.tools.common;

import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlUtils {
	private final static Logger log = LoggerFactory.getLogger(Strings.class);

	public static String getTextFromHtml(String html, int length) {
		if (StringUtils.isBlank(html)) {
			return html;
		}
		if (length <= 0) {
			length = Integer.MAX_VALUE;
		}
		StringBuilder buff = new StringBuilder((int) (html.length() * 0.7));
		Lexer lexer = new Lexer(html);
		Node node;
		try {
			while ((node = lexer.nextNode()) != null && buff.length() < length) {
				if (node instanceof TextNode) {
					buff.append(org.springframework.web.util.HtmlUtils.htmlUnescape(node.getText()));
				}
			}
		} catch (ParserException e) {
			log.error("parse html exception", e);
		}
		if (buff.length() > length) {
			buff.setLength(length);
		}
		return buff.toString();
	}

	public static String getTextFromHtml(String html) {
		return getTextFromHtml(html, Integer.MAX_VALUE);
	}
}
