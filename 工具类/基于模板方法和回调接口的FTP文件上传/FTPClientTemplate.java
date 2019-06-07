package com.xuexi.ftp;

import org.apache.commons.lang.Validate;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-06-07 20:46
 */
public class FTPClientTemplate {

    public static void main(String[] args) throws IOException {
        FTPClientTemplate ftpClientTemplate = new FTPClientTemplate("47.102.202.215", "ftpuser", "123456");
        System.out.println(Arrays.toString(new boolean[]{ftpClientTemplate.deleteFile("img/", "after.xml")}));
    }

    private static final Logger logger = LoggerFactory.getLogger(FTPClientTemplate.class);

    private FTPClientConfig ftpClientConfig;

    private String server;

    private String username;

    private String password;

    private int port = 21;

    public FTPClientTemplate(String host, String username, String password) {
        this.server = host;
        this.username = username;
        this.password = password;
    }

    /**
     * 顶层模板方法
     * @param callback
     * @throws IOException
     */
    public void execute(FTPClientCallback callback) throws IOException {
        FTPClient ftp = new FTPClient();
        try {
            if (this.getFtpClientConfig() != null) {
                ftp.configure(this.getFtpClientConfig());
            }

            ftp.connect(server, getPort());
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new IOException("failed to connect to the FTP Server:" + server);
            }

            boolean isLoginSuc = ftp.login(this.getUsername(), this.getPassword());
            if (!isLoginSuc) {
                throw new IOException("wrong username or password, please try to login again.");
            }
            callback.processFTPRequest(ftp);
        } finally {
            if (ftp.isConnected()) {
                ftp.disconnect();
            }
        }
    }


    /**
     * 删除文件
     * @param remoteDir
     * @param ftpFileName
     * @return
     * @throws IOException
     */
    public boolean deleteFile(final String remoteDir, final String ftpFileName) throws IOException {
        final boolean[] isSuc = {false};
        execute((ftp) -> {
            ftp.enterLocalPassiveMode();
            changeWorkingDir(ftp, remoteDir);
            if (logger.isDebugEnabled()) {
                logger.debug("working dir:" + ftp.printWorkingDirectory());
            }
            isSuc[0] = ftp.deleteFile(ftpFileName);
        });
        return isSuc[0];
    }

    /**
     * 修改文件名称
     * @param remoteDir
     * @param from
     * @param to
     * @throws IOException
     */
    public boolean rename(final String remoteDir, String from, String to) throws IOException {
        final boolean[] isSuc = {false};
        execute((ftp) -> {
            ftp.enterLocalPassiveMode();
            changeWorkingDir(ftp, remoteDir);
            if (logger.isDebugEnabled()) {
                logger.debug("working dir:" + ftp.printWorkingDirectory());
            }
            isSuc[0] = ftp.rename(from, to);
        });
        return isSuc[0];
    }

    /**
     * 列出所有文件的名字
     * @param remoteDir
     * @param fileNamePattern
     * @return
     * @throws IOException
     */
    public String[] listFileNames(final String remoteDir, final String fileNamePattern) throws IOException{
        final List<String[]> container = new ArrayList<>();

        execute((ftp) -> {
            ftp.enterLocalPassiveMode();
            changeWorkingDir(ftp, remoteDir);
            if (logger.isDebugEnabled()) {
                logger.debug("working dir:" + ftp.printWorkingDirectory());
            }
            container.add(ftp.listNames());
        });
        if (container.size() > 0) {
            return container.get(0);
        }
        return null;
    }

    protected void changeWorkingDir(FTPClient ftp, String remoteDir) throws IOException {
        Validate.notEmpty(remoteDir);
        ftp.changeWorkingDirectory(remoteDir);
    }

    public FTPClientConfig getFtpClientConfig() {
        return ftpClientConfig;
    }

    public void setFtpClientConfig(FTPClientConfig ftpClientConfig) {
        this.ftpClientConfig = ftpClientConfig;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
