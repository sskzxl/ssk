package com.mmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {
    @RequestMapping(value = "/req.do")
    public ModelAndView test(){
        ModelAndView mv =new ModelAndView();
        mv.addObject("shu");
        mv.addObject("yi");
        mv.setViewName("/WEB-INF/views/result.jsp");
        return mv;
    }
}
