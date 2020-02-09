package com.example.auth.demo.dao;

import com.example.auth.demo.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-02-09 19:20
 */
public class JdbcTemplateTest extends BaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    String baseDir = "/Users/wanli.zhou/Desktop/youzai/wanli-yz/";
    @Test
    public void download(){


//        List<Map<String, Object>> productImgs = jdbcTemplate.queryForList("select id, profile_img,lunbo_imgs,detail_imgs from products");
//        productImgs.stream().forEach(item -> {
//            try {
//                String profile_img = item.get("profile_img").toString();
//                Integer id = (Integer) item.get("id");
//                profile_img = StringUtils.trimToEmpty(profile_img);
//                if(StringUtils.isNoneBlank(profile_img)){
//                    String newUrl =  "http://img.suimeikeji.com/products/"+ getImgName(profile_img);
//                    download(baseDir + "/products/"+ getImgName(profile_img), profile_img);
//                    System.out.println("【"+id+"】下载成功 profile_img " + getImgName(profile_img));
//                    jdbcTemplate.update("update products set profile_img = '" + newUrl+ "' where id = "+id);
//                }
//
//            }catch (Exception e){
//                System.err.println(item.get("id") + " get profile_img error");
//            }
//            try {
//                String lunbo_imgs = item.get("lunbo_imgs").toString();
//                Integer id = (Integer) item.get("id");
//                lunbo_imgs = StringUtils.trimToEmpty(lunbo_imgs);
//                if(StringUtils.isNoneBlank(lunbo_imgs)){
//                    List<String> newUrlr = new ArrayList<>();
//                    for(String im : lunbo_imgs.split("\\|")){
//                        download(baseDir + "/products/"+ getImgName(im), im);
//                        System.out.println("【"+id+"】下载成功 lunbo_imgs " + getImgName(im));
//                        newUrlr.add("http://img.suimeikeji.com/products/"+ getImgName(im));
//                    }
//                    if(!newUrlr.isEmpty()){
//                        jdbcTemplate.update("update products set lunbo_imgs = '" + newUrlr.stream().collect(Collectors.joining("|"))+ "' where id = "+id);
//                    }
//                }
//            }catch (Exception e){
//                System.err.println(item.get("id").toString() + " get lunbo_imgs error");
//            }
//            try {
//                String detail_imgs = item.get("detail_imgs").toString();
//                Integer id = (Integer) item.get("id");
//                List<String> newUrlr = new ArrayList<>();
//                if(StringUtils.isNoneBlank(detail_imgs)){
//                    for(String im : detail_imgs.split("\\|")){
//                        download(baseDir + "/products/"+ getImgName(im), im);
//                        System.out.println("【"+id+"】下载成功 detail_imgs " + getImgName(im));
//                        newUrlr.add("http://img.suimeikeji.com/products/"+ getImgName(im));
//                    }
//                }
//                if(!newUrlr.isEmpty()){
//                    jdbcTemplate.update("update products set detail_imgs = '" + newUrlr.stream().collect(Collectors.joining("|"))+ "' where id = "+id);
//                }
//            }catch (Exception e){
//                System.err.println(item.get("id").toString() + " get detail_imgs error");
//            }
//
//        });
//
//
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
////        List<Map<String, Object>> lunboImgs = jdbcTemplate.queryForList("select product_profile_img from orders_item");
//        List<Map<String, Object>> productCategoryImgs = jdbcTemplate.queryForList("select id, image from product_category");
//        productCategoryImgs.stream().forEach(item -> {
//            try{
//
//                String image = item.get("image").toString();
//                image = StringUtils.trimToEmpty(image);
//                Integer id = (Integer) item.get("id");
//                if(StringUtils.isNoneBlank(image)){
//                    String newUrl =  "http://img.suimeikeji.com/productCategory/"+ getImgName(image);
//                    download(baseDir + "/productCategory/"+ getImgName(image), image);
//                    System.out.println("【"+id+"】下载成功 productCategory " + getImgName(image));
//                    jdbcTemplate.update("update product_category set image = '" + newUrl+ "' where id = "+id);
//                }
//            }catch (Exception e){
//                System.err.println(item.get("id").toString() + " get productCategory error");
//            }
//        });
        List<Map<String, Object>> productZhuanquImgs = jdbcTemplate.queryForList("select id, image from product_zhuanqu_category");

        productZhuanquImgs.stream().forEach(item -> {
            try{
                String image = item.get("image").toString();
                Integer id = (Integer) item.get("id");
                image = StringUtils.trimToEmpty(image);
                if(StringUtils.isNoneBlank(image)){
                    String newUrl =  "http://img.suimeikeji.com/productZhuanqu/"+ getImgName(image);
                    download(baseDir + "/productZhuanqu/"+ getImgName(image), image);
                    System.out.println("【"+id+"】下载成功 productZhuanqu " + getImgName(image));
                    jdbcTemplate.update("update product_zhuanqu_category set image = '" + newUrl+ "' where id = "+id);
                }
            }catch (Exception e){
                System.err.println(item.get("id").toString() + " get productZhuanquImgs error");
            }
        });
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
    private void download(String path, String url) throws IOException {
        URL uri = new URL(url);
        InputStream in = uri.openStream();
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out = new FileOutputStream(path);
        byte[] bytes = new byte[1024];
        int length = 0;
        while ((length = in.read(bytes)) > 0){
            out.write(bytes, 0, length);
        }
        out.flush();
        out.close();
        in.close();
    }
}