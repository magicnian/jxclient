package cn.magicnian.client;


import cn.magicnian.util.DownloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by liunn on 2017/11/1.
 */
@Slf4j
public class JXClient {

    static {
        File home = new File(System.getProperty("user.home"));
        String dataDir = System.getProperty("data.dir");
        if (StringUtils.isBlank(dataDir)) {
            dataDir = home + File.separator + "data" + File.separator + "jxcaptcha";
        }
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        System.setProperty("data.dir", dataDir);
        log.info("data.dir :{}", dataDir);
    }

    private static final String getCaptchaUrl = "https://wode.homecredit.cn/CustomerService/getCheckCode?it=0.6000329938903031";
    private static final String verifyUrl = "http://127.0.0.1:8333/captchaverify";

    public static void main() {
        CloseableHttpClient client = HttpClients.createDefault();
        String dir = System.getProperty("data.dir");
        try {
            HttpGet httpGet = new HttpGet(getCaptchaUrl);
            String picName = UUID.randomUUID()+".png";
            //下载验证码
            DownloadUtil.downloadPic(client,getCaptchaUrl,dir,picName);

            String filepath = dir+File.separator+picName;

            //构建验证服务url
            HttpPost httpPost = new HttpPost(verifyUrl);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //通过httpclient传送下载好的图片到验证服务（验证服务在jxcaptcha工程中，使用flask框架搭建的简易http服务）
            builder.addBinaryBody("pic",new File(filepath), ContentType.MULTIPART_FORM_DATA,"captcha.png");
            builder.addTextBody("name","magicnian",ContentType.MULTIPART_FORM_DATA);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);

            CloseableHttpResponse response = client.execute(httpPost);

            String result = "";
            HttpEntity responseEntity = response.getEntity();
            if(null!=responseEntity){
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
                log.info(result);
            }

        }catch (Exception e){
            log.info("execute error!",e);
        }finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
