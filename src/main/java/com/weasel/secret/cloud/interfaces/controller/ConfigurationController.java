package com.weasel.secret.cloud.interfaces.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dell on 2017/11/21.
 */
@Controller
@Api(description = "secret-cloud api文档",protocols = "http/https",hidden = true)
public class ConfigurationController {

    @Value("${file.path}")
    private String path;

    @ApiOperation(value = "api",notes = "会重定向到swagger-ui.html",hidden = true,httpMethod = "GET",protocols = "http/https")
    @RequestMapping("/api")
    public String api(){

        return "redirect:swagger-ui.html";
    }

    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public void download(@RequestParam(value = "version",required = false) float version, HttpServletResponse response){
        try {
            OutputStream outputStream = response.getOutputStream();
            Collection<File> files = FileUtils.listFiles(FileUtils.getFile(path),new String[]{"apk"},false);
            String fileName = "secret-common-1.0-20171205.131951-11.jar";
            if(version > 0.0){
                fileName = files.stream()
                                .filter(file -> version == getVersion(file.getName()))
                                .map(file -> file.getName())
                                .findFirst()
                                .orElse("");
                if ("" == fileName){
                    outputStream.write("找不到您要下载的文件".getBytes(Charset.forName("utf-8")));
                    outputStream.flush();
                    return;
                }
            }else{

                fileName = files.stream()
                                .map(file -> file.getName())
                                .max(Comparator.comparing(fn -> getVersion(fn)))
                                .orElse("");
            }

            File file = FileUtils.getFile(path + File.separator+fileName);
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            FileUtils.copyFile(file,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float getVersion(String fileName){
        String[] names = StringUtils.split(fileName,'-');
        float v = Float.parseFloat(StringUtils.split(names[names.length-1],'.')[0]);
        return v;
    }
}
