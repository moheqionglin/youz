package com.example.auth.demo.dao;

import com.example.auth.demo.BaseTest;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.sm.service.ServiceUtil;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-02-09 19:20
 */
public class JdbcTemplateTest extends BaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    String baseDir = "/Users/wanli.zhou/Desktop/youzai/wanli-yz/";
    @Autowired
    private Auth auth;
    @Autowired
    private ServiceUtil serviceUtil;

    @Ignore
    @Test
    public void uploadTest(){
        String upToken = serviceUtil.getNewImgToken();
        try {
            byte[] bytes1 = Files.readAllBytes(Paths.get("/Users/wanli.zhou/Desktop/youzai/706000247.jpg"));
            String upload = upload(bytes1, upToken);
            System.out.println("uploadupload " + upload);
        } catch (QiniuException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void download(){
        String upToken = serviceUtil.getNewImgToken();

        List<Map<String, Object>> productImgs = jdbcTemplate.queryForList("select id, profile_img,lunbo_imgs,detail_imgs from products_bk");
        productImgs.stream().forEach(item -> {
            try {
                String profile_img = item.get("profile_img").toString();
                Integer id = (Integer) item.get("id");
                profile_img = StringUtils.trimToEmpty(profile_img);
                if(StringUtils.isNoneBlank(profile_img) && StringUtils.contains(profile_img, "121.40.186.118")){
                    String qn = downloadAndUpload(profile_img,upToken );
                    System.out.println("【"+id+"】下载成功 profile_img " + profile_img + " qn = "+ qn);
                    String newUrl =  "http://img.suimeikeji.com/"+ qn;
                    jdbcTemplate.update("update products_bk set profile_img = '" + newUrl+ "' where id = "+id);
                }

            }catch (Exception e){
                e.printStackTrace();
                System.err.println(item.get("id") + " get profile_img error");
            }
            try {
                String lunbo_imgs = item.get("lunbo_imgs").toString();
                Integer id = (Integer) item.get("id");
                lunbo_imgs = StringUtils.trimToEmpty(lunbo_imgs);
                if(StringUtils.isNoneBlank(lunbo_imgs)){
                    List<String> newUrlr = new ArrayList<>();
                    for(String im : lunbo_imgs.split("\\|")){
                        if(StringUtils.contains(im, "121.40.186.118")){
                            String qn = downloadAndUpload( im,upToken);
                            System.out.println("【"+id+"】下载成功 lunbo_imgs " + im + " qn = " + qn);
                            newUrlr.add("http://img.suimeikeji.com/"+ qn);
                        }
                    }
                    if(!newUrlr.isEmpty()){
                        jdbcTemplate.update("update products_bk set lunbo_imgs = '" + newUrlr.stream().collect(Collectors.joining("|"))+ "' where id = "+id);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                System.err.println(item.get("id").toString() + " get lunbo_imgs error");
            }

            try {
                String detail_imgs = item.get("detail_imgs").toString();
                Integer id = (Integer) item.get("id");
                List<String> newUrlr = new ArrayList<>();
                if(StringUtils.isNoneBlank(detail_imgs)){

                    for(String im : detail_imgs.split("\\|")){
                        if(StringUtils.contains(im, "121.40.186.118")){
                            String qn = downloadAndUpload( im,upToken);
                            System.out.println("【"+id+"】下载成功 detail_imgs " + im + " qn = " + qn);
                            newUrlr.add("http://img.suimeikeji.com/"+ qn);
                        }
                    }
                }
                if(!newUrlr.isEmpty()){
                    jdbcTemplate.update("update products_bk set detail_imgs = '" + newUrlr.stream().collect(Collectors.joining("|"))+ "' where id = "+id);
                }
            }catch (Exception e){
                e.printStackTrace();
                System.err.println(item.get("id").toString() + " get detail_imgs error");
            }

        });


//        List<Map<String, Object>> lunboImgs = jdbcTemplate.queryForList("select id, image from lunbo");
//        lunboImgs.stream().forEach(item -> {
//            try{
//
//                String image = item.get("image").toString();
//                Integer id = (Integer) item.get("id");
//                image = StringUtils.trimToEmpty(image);
//                if(StringUtils.isNoneBlank(image)){
//                    String newUrl =  "http://img.suimeikeji.com/lunbo/"+ getImgName(image);
//                    download(baseDir + "/lunbo/"+ getImgName(image), image);
//                    System.out.println("【"+id+"】下载成功 lunbo " + getImgName(image));
//                    jdbcTemplate.update("update lunbo set image = '" + newUrl+ "' where id = "+id);
//                }
//            }catch (Exception e){
//                System.err.println(item.get("id").toString() + " get lunboImgs error");
//            }
//        });
//        List<Map<String, Object>> lunboImgs = jdbcTemplate.queryForList("select product_profile_img from orders_item");
//        List<Map<String, Object>> productCategoryImgs = jdbcTemplate.queryForList("select id, image from product_category");
//        productCategoryImgs.stream().forEach(item -> {
//            try{
//
//                String image = item.get("image").toString();
//                image = StringUtils.trimToEmpty(image);
//                Integer id = (Integer) item.get("id");
//                if(StringUtils.isNoneBlank(image) && StringUtils.contains(image, "121.40.186.118")){
//
//                    String qn = downloadAndUpload( image,upToken);
//                    System.out.println("【"+id+"】下载成功 productCategory " + image +", qn = " +qn);
//                    String newUrl =  "http://img.suimeikeji.com/"+ qn;
//                    jdbcTemplate.update("update product_category set image = '" + newUrl+ "' where id = "+id);
//                }
//            }catch (Exception e){
//                System.err.println(item.get("id").toString() + " get productCategory error");
//            }
//        });
//        List<Map<String, Object>> productZhuanquImgs = jdbcTemplate.queryForList("select id, image from product_zhuanqu_category");
//
//        productZhuanquImgs.stream().forEach(item -> {
//            try{
//                String image = item.get("image").toString();
//                Integer id = (Integer) item.get("id");
//                image = StringUtils.trimToEmpty(image);
//                if(StringUtils.isNoneBlank(image) && StringUtils.contains(image, "121.40.186.118")){
//
//                    String qn = downloadAndUpload( image,upToken);
//                    String newUrl =  "http://img.suimeikeji.com/"+ qn;
//                    System.out.println("【"+id+"】下载成功 productZhuanqu " + image + " qn = " + qn);
//                    jdbcTemplate.update("update product_zhuanqu_category set image = '" + newUrl+ "' where id = "+id);
//                }
//            }catch (Exception e){
//                System.err.println(item.get("id").toString() + " get productZhuanquImgs error");
//            }
//        });
        System.out.println("FINISH");
    }

    private String getImgName(String image) {
        return image.substring(image.lastIndexOf("/") + 1);
    }

    @Test
    public void d() throws IOException {
        System.out.println(getImgName("http://121.40.186.118:10090/upload/201904/17/201904171236420501.jpg"));
//        download("/Users/wanli.zhou/Desktop/youzai/wanli-yz/aaa", "http://121.40.186.118:10090/upload/201904/17/201904171236420501.jpg");
    }
    private String downloadAndUpload(String url, String upToken) throws IOException {
        URL uri = new URL(url);
        InputStream in = uri.openStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int length = 0;
        while ((length = in.read(bytes)) > 0){
            out.write(bytes, 0, length);
        }
        out.flush();
        out.close();
        in.close();

        String upload = upload(out.toByteArray(), upToken);
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return upload;
    }

    private String upload(byte[] bytes, String token) throws QiniuException {
        Configuration cfg = new Configuration(Region.region0());
//...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
        String bucket = "your bucket name";
//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;

        Response response = uploadManager.put(bytes, null, token);
        //解析上传成功的结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        System.out.println("Upload succcess " + putRet.key + putRet.hash);
        return putRet.hash;
    }
}