package cn.magicnian.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liunn on 2017/11/1.
 */
@Slf4j
public class DownloadUtil {

    public static void downloadPic(CloseableHttpClient client, String url, String path, String fileName) throws Exception {
        if (null == client) {
            client = HttpClients.createDefault();
        }

        CloseableHttpResponse response = null;
        FileOutputStream fos = null;
        HttpGet request = null;
        try {
            request = new HttpGet(url);
            response = client.execute(request, HttpClientContext.create());
            int status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                log.info("download status is :{}", status);
                throw new RuntimeException("download error,responseCode is :" + status);
            }


            File dir = new File(path);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            if (file == null || !file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            response.getEntity().writeTo(fos);


        } catch (Exception e) {
            throw e;
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != request) {
                request.releaseConnection();
            }
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


    }
}
