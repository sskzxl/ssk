package com.mmall.service.impl;
import ch.qos.logback.core.net.SyslogOutputStream;
import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.UUID;
@Service("iFileService")
public class FileServiceImpl  implements  IFileService{

    Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path){
        //得到上传时的文件名
        String fileName = file.getOriginalFilename();
        file.getName();
        //lastIndexOf(s)从后往前第一个出现s字符的位置
        //substring(n)截取n个字符后的所有字符
        //如image.xxx.jpg    lastIndexOf(.)会xxx后的.的index 从0开始  返回9
        //不要. 所有+1
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件的文件名:{},上传的路径是:{},新文件名:{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        //创建File类的对象后，磁盘不会立马创建对应的文件，需要调用mkdirs方法
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            /*
                mkdirs 和 mkdir的区别
                mkdirs： 可新创建多级目录
                mkdir：  只可以新创建一级目录，如果是不存在的多级目录则不会创建。
             */
            fileDir.mkdirs();

        }
        File targetFile = new File(path,uploadFileName);
        targetFile.mkdirs();
        try {
            /*
                把上传的文件写入磁盘 这里是 E:\~
             */
            file.transferTo(targetFile);
            //上传文件已经成功
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //已经上传到ftp服务器上
            targetFile.delete();
            //这里删除了所有磁盘看不到文件

        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();


    }


}
