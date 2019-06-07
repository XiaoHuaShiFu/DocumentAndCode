package com.xuexi.ftp;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

/**
 * 描述:
 *
 * @author xhsf
 * @email 827032783@qq.com
 * @create 2019-06-07 20:57
 */
public interface FTPClientCallback {
    void processFTPRequest(FTPClient ftpClient) throws IOException;
}
