package com.yu.controller;

import com.yu.model.people.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("${people-service.api-base-url}/dict")
@RestController
public class DictionaryController {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryController.class);

    @GetMapping("/Gender")
    public List<String> listAllGender(){
        // enum listing normally wont require paging.
        return Arrays.stream(Gender.values()).map(it->it.name()).collect(Collectors.toList());
    }

}
