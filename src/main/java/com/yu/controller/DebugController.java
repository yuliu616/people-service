package com.yu.controller;

import com.yu.modelMapper.DebugDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * expose some internal function for ease of debugging,
 * should be blocked in production version.
 */
@RequestMapping("${people-service.api-base-url}/debug")
@RestController
public class DebugController {

    @Autowired
    protected DebugDataMapper debugDataMapper;

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    @Transactional
    @PostMapping("/enableAllPeople")
    public String enableAllProducts(){
        logger.error("debug method invoked");
        this.debugDataMapper.enableAllPeople();
        return "all peoples set to active.";
    }

    @Transactional
    @PostMapping("/resetVersionOfAllPeople")
    public String resetVersionOfAllProducts(){
        logger.error("debug method invoked");
        this.debugDataMapper.resetVersionOfAllPeople();
        return "version of all peoples is reset.";
    }

}
