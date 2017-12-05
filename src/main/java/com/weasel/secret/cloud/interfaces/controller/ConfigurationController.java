package com.weasel.secret.cloud.interfaces.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

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
    public void download(HttpServletResponse response){
        try {
            String fileName = "secret-common-1.0-20171205.131951-11.jar";
            File file = FileUtils.getFile(path + File.separator+fileName);
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            OutputStream outputStream = response.getOutputStream();
            FileUtils.copyFile(file,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
