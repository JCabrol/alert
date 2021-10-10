package com.safetynet.alert.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {
    @ResponseBody
    @RequestMapping(path = "/")
    public String home(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String host = request.getServerName();

        String endpointBasePath = "/actuator";

        String url = "http://" + host + ":8090" + contextPath + endpointBasePath;
           return "Sprig Boot Actuator:" +


                    // http://localhost:8090/actuator
                    "<a href='" + url + "'>" + url + "</a>"
                    ;
        }
    }


