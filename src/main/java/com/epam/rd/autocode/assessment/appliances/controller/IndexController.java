package com.epam.rd.autocode.assessment.appliances.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(Locale locale) {
        System.out.println(">> Current locale: " + locale);
        return "index";
    }
}