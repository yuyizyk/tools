package cn.yuyizyk.tools.files.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

public interface FtpClientExtractor {
	public void doInFtp(FTPClient client) throws IOException;
}
