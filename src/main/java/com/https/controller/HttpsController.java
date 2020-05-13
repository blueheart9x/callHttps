/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.https.controller;

import com.https.service.CommonService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
public class HttpsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpsController.class);

    private final CommonService commonService;

    @Autowired
    public HttpsController(CommonService commonService) {
        this.commonService = commonService;
    }

    @RequestMapping(value = {"/api"}, method = {RequestMethod.GET})
    public String findAll(@RequestParam("url") String url) {
        String result = this.commonService.sendRequest(url);
        LOGGER.info("https url [{}] ==> {}", url, result);
        return result;
    }
}
