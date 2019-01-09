package com.pinyougou.manager.controller;

import cn.itcast.core.pojo.entity.Result;
import com.itheima.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        //获取到文件的完整路径名
        String filename = file.getOriginalFilename();
        try {
            //创建一个fastDFS的客户端D:\pinyougou\pinyougou-parent\pinyougou-show-web\src\main\resources\fastDFS\fdfs_client.conf
            FastDFSClient client = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            String uploadFile = client.uploadFile(file.getBytes(), filename, file.getSize());
            String path = FILE_SERVER_URL+uploadFile;
            return new Result(true,path);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }

}
