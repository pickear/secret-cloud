package com.weasel.secret.cloud.interfaces.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by dell on 2017/11/21.
 */
@Controller
@Api(description = "secret-cloud api文档",protocols = "http/https",hidden = true)
public class ConfigurationController {


    @ApiOperation(value = "api",notes = "会重定向到swagger-ui.html",hidden = true,httpMethod = "GET",protocols = "http/https")
    @RequestMapping("/api")
    public String api(){

        return "redirect:swagger-ui.html";
    }
}
