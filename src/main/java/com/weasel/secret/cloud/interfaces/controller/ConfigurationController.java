package com.weasel.secret.cloud.interfaces.controller;

import com.weasel.secret.common.domain.UpdateInfo;
import com.weasel.secret.common.helper.Md5Helper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Comparator;

/**
 * Created by dell on 2017/11/21.
 */
@Controller
@Api(description = "secret-cloud api文档",protocols = "http/https",hidden = true)
public class ConfigurationController {

    private final static Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

    @Value("${file.path}")
    private String path;
    @Value("${file.url}")
    private String url;

    @ApiOperation(
            value = "api",
            notes = "会重定向到swagger-ui.html",
            hidden = true,httpMethod = "GET",
            protocols = "http/https"
    )
    @RequestMapping("/api")
    public String api(){

        return "redirect:swagger-ui.html";
    }

    @ApiOperation(
            value = "下载指定版本的安卓客户端",
            notes = "<h5>1.</h5>版本号必要是存在的，否则将返回404.<br>" +
                    "<h5>2.</h5>可能先通过/version请求获取最新的版本号.<br>"+
                    "<h5>3.</h5>版本号如果不传，默认下载最新版.<br>",
            response = Void.class,
            httpMethod = "GET",
            protocols = "http/https"
    )
    @ApiImplicitParam(name = "version",value = "0.1",defaultValue = "0.0",example = "1.0",required = false,dataTypeClass = Float.class)
    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public void download(@RequestParam(value = "version",required = false) Float version, HttpServletResponse response){
        try {
            OutputStream outputStream = response.getOutputStream();
            Collection<File> files = FileUtils.listFiles(FileUtils.getFile(path),new String[]{"apk"},false);
            String fileName;
            if(null != version){
                fileName = files.stream()
                                .filter(file -> version == getVersion(file.getName()))
                                .map(file -> file.getName())
                                .findFirst()
                                .orElse("");
                if (null == fileName || "" == fileName){
                    response.setStatus(404);
                    response.setContentType("text/html;charset=utf-8");
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


    @ApiOperation(
            value = "version",
            notes = "获取最新版的安卓客户端版本号",
            httpMethod = "GET",
            protocols = "http/https"
    )
    @RequestMapping(value = "/version",method = RequestMethod.GET)
    public @ResponseBody float version(){

        Collection<File> files = FileUtils.listFiles(FileUtils.getFile(path),new String[]{"apk"},false);
        float version = files.stream()
                             .map(file -> getVersion(file.getName()))
                             .max(Comparator.comparing(v -> v))
                             .orElse(0.0f);
        return version;
    }

    @ApiOperation(
            value = "获取版本更新信息",
            notes = "<h5>1.</h5>版本号应该是存在的版本.<br>" +
                    "<h5>2.</h5>约定软件定义的版本号规则为 大版本号.小版本号,当需要强制更新时，说明改动较大，那么需要更新大版本号。<br>" +
                    "当更新的功能较少，兼容之前的版本，那只修改小版本号。如:用户安装的版本为1.2,最新版本为1.5，那么是不需要强制更新的。" +
                    "如果最新版本为2.0，那么说明不兼容1.x版本，需要强制用户更新。<br>",
            response = Void.class,
            httpMethod = "GET",
            protocols = "http/https"
    )
    @ApiImplicitParam(name = "version",value = "0.1",defaultValue = "0.0",example = "1.0",required = true,dataTypeClass = Float.class)
    @RequestMapping(value = "/update-info",method = RequestMethod.GET)
    public @ResponseBody UpdateInfo updateInfo(@RequestParam(value = "version") float version) throws IOException {

        Collection<File> files = FileUtils.listFiles(FileUtils.getFile(path),new String[]{"apk"},false);

        //获取版本号大于等于传过来的版本号的最大版本号文件
        File maxVersionFile = files.stream()
                                   .filter(file -> getVersion(file.getName()) >= version)
                                   .max(Comparator.comparing(file -> getVersion(file.getName())))
                                   .orElse(null);

        //如果文件找不到，说明传过来的版本号文件不存在
        if(null == maxVersionFile){
            return new UpdateInfo(false,false,0.0f,"","",0);
        }
        float maxVersion = getVersion(maxVersionFile.getName());
        long size = FileUtils.sizeOf(maxVersionFile);
        String md5 = Md5Helper.md5Hex(maxVersionFile);
        /**
         * 通常maxVersion > version,说明有新版本更新
         * 约定地，maxVersion - version >= 1说明版本差别较大，需要强制更新
         */
        return new UpdateInfo(maxVersion > version,maxVersion - version >= 1,maxVersion,url,md5,size);
    }

    /**
     *通过文件名获取版本号。
     * @param fileName  文件名，格式为xxxx-版本号.apk，如：secret-android-0.1.apk
     * @return
     */
    private float getVersion(String fileName){
        Assert.hasLength(fileName,"无法获取文件名，检查目录["+path+"]下是否有要发布的包!");
        String[] names = fileName.split("-");
        String version = names[names.length-1];
        logger.debug("version is [{}]",version);
        float v = Float.parseFloat(version.split(".apk")[0]);
        return v;
    }
}
