package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.service.impl.UserServiceImpl;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService iUserService;
    /*
       用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String username, String password, HttpSession session){
        //service--> mybatis--> dao
        ServiceResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){

            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;


    }

    /**登出操作
     * @param session
     * @return
     */
    @RequestMapping(value = "/logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccess();
    }

    /**
     * 注册操作
     * @param user
     * @return
     */
    @RequestMapping(value = "/register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 检查
     */
    @RequestMapping(value = "/check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str,type);
    }

    /*
     *获取登陆用户信息
     */
    @RequestMapping(value = "/get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServiceResponse.createBySuccess(user);
        }
        return ServiceResponse.createByErrorMessage("无法获取用户信息，用户未登陆");
    }

    /**
     * 忘记密码获取问题
     */

    @RequestMapping(value = "/forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }


    /**
     * 忘记密码检查问题答案
     */

    @RequestMapping(value = "/forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetCheckAnswer(String username, String question, String answer){
        return iUserService.checkAnswer(username,question,answer);
    }
    //忘记密码->重置密码
    @RequestMapping(value = "/forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }
    //登陆状态->重置密码
    @RequestMapping(value = "/reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorMessage("用户未登陆");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    //更新个人信息
    @RequestMapping(value = "/update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> update_information(HttpSession session,User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServiceResponse.createByErrorMessage("用户未登陆");
        }
        //不更新Id和Username
        user.setUsername(currentUser.getUsername());
        user.setId(currentUser.getId());
        ServiceResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    //获取个人信息
    @RequestMapping(value = "/get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> get_information(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"需要登陆");
        }
        return iUserService.getInformation(user.getId());
    }

}
