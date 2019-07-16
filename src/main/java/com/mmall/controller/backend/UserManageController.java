package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@RequestMapping("/manage/user")
@Controller
public class UserManageController {
    @Autowired
    IUserService iUserService;
    @RequestMapping(value = "/login.do" ,method = RequestMethod.POST)
    public ServiceResponse<User> login(HttpSession session, String username, String password){
        ServiceResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            User user = response.getData();
            if(user.getRole() == Const.Role.ROLE_ADMIN){
                //说明登陆的是管理员
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            }else {
                return ServiceResponse.createByErrorMessage("不是管理员，无法登陆");
            }
        }
        return response;

    }
}
