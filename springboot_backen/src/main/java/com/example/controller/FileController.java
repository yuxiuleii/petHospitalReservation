package com.example.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.example.common.Result;
import com.example.entity.FileEntity;
import com.example.service.FileService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;


@RestController
@RequestMapping("/file")
public class FileController {
    @Value("${files.upload.path}")
    private String fileUpLoadPath;

    @Value("${server.ip}")
    private String serverIp;

    @Autowired
    private FileService fileService;

    /*
    文件上传接口
    file-----前端传过来的文件
    * */
    @PostMapping("/upload")
    public String upLoad(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        //  定义一个文件唯一的标识码
        String uuId = IdUtil.fastSimpleUUID();

        String fileUUID = uuId + StrUtil.DOT + type;
        File uploadFile = new File(fileUpLoadPath + fileUUID);
        //  如果文件目录不存在则新建一个
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }

        String url;
        //  上传文件到磁盘
        file.transferTo(uploadFile);
        //  获取文件的md5
        String md5 = SecureUtil.md5(uploadFile);
        // 从数据库查询是否存在相同记录
        FileEntity fileByMD5 = getFileByMD5(md5);
        if (fileByMD5 != null) {
            url = fileByMD5.getUrl();
            // 由于文件已存在，所以删除刚刚上传的文件
            uploadFile.delete();
        } else {
            //  不存在重复文件，创建新的url
            url = "http://"+ serverIp +":9090/file/" + fileUUID;
        }

        //  存储到数据库
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(originalFilename);
        fileEntity.setType(type);
        fileEntity.setSize(size / 1024);
        fileEntity.setUrl(url);
        fileEntity.setMd5(md5);
        fileService.save(fileEntity);
        return url;
    }

    /*
     * 文件下载接口： http://localhost:9090/file/{fileUUID}
     *
     * */
    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        //  根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUpLoadPath + fileUUID);

        //  设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");

        //  读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    private FileEntity getFileByMD5(String md5) {
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<FileEntity> list = fileService.list(queryWrapper);
        return list.size() == 0 ? null : list.get(0);
    }


    @PostMapping("/update")
    public Result update(@RequestBody FileEntity file) {
        return Result.success(fileService.saveOrUpdate(file));
    }


    @DeleteMapping("/{name}")
    public Result delete(@PathVariable String name){
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        FileEntity file = fileService.getOne(queryWrapper);
        fileService.saveOrUpdate(file);
        return Result.success();
    }
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<String> names){
        int num=names.size();
        for (String name : names){
            QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name",name);
            FileEntity file = fileService.getOne(queryWrapper);
            fileService.saveOrUpdate(file);
            num--;
        }
        if (num==0){
            return Result.success(true);
        }
        return Result.success(false);
    }

    /*
     * 分页查询接口
     * */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(required = false) String name) {
        IPage<FileEntity> page = new Page<>(pageNum, pageSize);
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", false);
        queryWrapper.like(Strings.isNotEmpty(name), "name", name);

        return Result.success(fileService.page(page, queryWrapper));
    }
}