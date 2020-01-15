package com.example.auth.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author wanli.zhou
 * @description
 * @time 2019-12-23 20:59
 */
public class Dow {
    public static void main(String[] args) throws IOException {

        Files.lines(Paths.get("/Users/wanli.zhou/Desktop/youzai/tupian"))
            .forEach(img -> {
                try {
                    final String httpPrefix = "http://121.40.186.118:10090";
                        if(!img.startsWith(httpPrefix)){
                            img = httpPrefix + img;
                        }
                        URL url = new URL(img);
                        InputStream inputStream = url.openConnection().getInputStream();
                        byte[] bs = new byte[1024];
                        int leng = 0;

                        String filePath = img.replace(httpPrefix, "");
                        String substring = filePath.substring(0, filePath.lastIndexOf("/") + 1);
                        File dir = new File("/Users/wanli.zhou/Desktop/youzai" + substring);
                        if(!dir.exists()){
                            dir.mkdirs();
                        }
                        File file = new File("/Users/wanli.zhou/Desktop/youzai"  + filePath);
                        if(file.exists()){
                            file.delete();
                        }
                        FileOutputStream out = new FileOutputStream(file);
                        while ((leng = inputStream.read(bs)) > 0){
                            out.write(bs, 0, leng);
                        }
                        out.close();
                        inputStream.close();
                        System.out.println("下载 " + img + "成功") ;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }
}