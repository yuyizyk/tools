package cn.yuyizyk.tools.files.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

import jcifs.smb.SmbFile;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTPTools
{
    
    /**
     * Description: 从FTP服务器下载文件
     * 
     * @param url
     *            FTP服务器hostname
     * @param port
     *            FTP服务器端口
     * @param username
     *            FTP登录账号
     * @param password
     *            FTP登录密码
     * @param remotePath
     *            FTP服务器上的相对路径
     * @param fileName
     *            要下载的文件名
     * @param localPath
     *            下载后保存到本地的路径
     * @return
     */
    public static boolean downFile(String url, int port, String username, String password,
            String remotePath, String fileName, String localPath)
    {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try
        {
            int reply;
            ftp.connect(url, port);
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                ftp.disconnect();
                return success;
            }
            ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs)
            {
                if (ff.getName().equals(fileName))
                {
                    File localFile = new File(localPath + "/" + ff.getName());
                    OutputStream is = new FileOutputStream(localFile);
                    ftp.retrieveFile(ff.getName(), is);
                    is.close();
                }
            }
            ftp.logout();
            success = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (ftp.isConnected())
            {
                try
                {
                    ftp.disconnect();
                }
                catch (IOException ioe)
                {
                }
            }
        }
        return success;
    }
    
    /**
     * @Title: checkFtp
     * @Description: 检查ftp是否可以连接
     * @param url
     * @param username
     * @param password
     * @param remotePath
     * @return boolean
     * @author lqy
     * @throws
     */
    public static boolean checkFtp(String url, String username, String password, String remotePath)
    {
        
        boolean success = false;
        FTPClient ftp = getFtpClient(url, username, password);
        try
        {
            if (ftp != null && ftp.isConnected())
            {
                ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
                return true;
            }
        }
        catch (IOException e)
        {
            return success;
        }
        finally
        {
            if (ftp != null && ftp.isConnected())
            {
                try
                {
                    ftp.disconnect();
                }
                catch (IOException ioe)
                {
                }
            }
        }
        return success;
    }
    
    /**
     * @Title: getFtpClient
     * @Description: 获取ftp连接
     * @param url
     * @param username
     * @param password
     * @return FTPClient
     * @author lqy
     * @throws
     */
    public static FTPClient getFtpClient(String url, String username, String password)
    {
        FTPClient ftp = new FTPClient();
        try
        {
            int reply;
            ftp.setConnectTimeout(5000);
            ftp.connect(url, 21);
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                ftp.disconnect();
                return null;
            }
            return ftp;
        }
        catch (IOException e)
        {
        }
        return null;
        
    }
    
    /**
     * @Title: checkSmbFile
     * @Description: 检查共享盘
     * @param username
     * @param pwd
     * @param ip
     * @param path
     * @return boolean
     * @author lqy
     * @throws
     */
    public static boolean checkSmbFile(String username, String pwd, String ip, String path)
    {
        
        SmbFile remoteFile = null;
        try
        {
            String remotePhotoUrl = "smb://" + username + ":" + pwd + "@" + ip;
            if (path.startsWith("/"))
            {
                remotePhotoUrl += path;
            }
            else
            {
                remotePhotoUrl += "/" + path;
            }
            remoteFile = new SmbFile(remotePhotoUrl);
            remoteFile.connect(); // 尝试连接
            if (!remoteFile.exists())
            {
                remoteFile.mkdirs();
            }
            return true;
        }
        catch (MalformedURLException e)
        {
            return false;
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
        return false;
    }
    
    public static void main(String[] args)
    {
        // FTPClient ftp = getFtpClient("192.168.20.222", "test", "test");
        // OutputStream out = null;
        // try
        // {
        // InputStream input = new FileInputStream(new
        // File("F://ftp//aaa.txt"));
        // ftp.setFileType(FTP.BINARY_FILE_TYPE);//设置文件类型
        // ftp.enterLocalPassiveMode();
        // ftp.setControlEncoding("UTF-8");
        // boolean m = ftp.changeWorkingDirectory("/");
        // System.out.println(m);
        //
        // boolean o = ftp.makeDirectory("mmm");
        // boolean b = ftp.changeWorkingDirectory("/mmm");
        // System.out.println(o);
        // boolean c = ftp.storeFile("bbb.txt", input);
        // System.out.println(c);
        // input.close();
        // ftp.logout();
        // }
        // catch (IOException e)
        // {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        
        /*
         * try { InputStream input = new FileInputStream(new
         * File("F://ftp//aaa.txt")); checkSmbFile("liquanyu", "liquanyu",
         * "192.168.20.222", "q/sss"); } catch (FileNotFoundException e) { //
         * TODO Auto-generated catch block e.printStackTrace(); }
         */
    }
}