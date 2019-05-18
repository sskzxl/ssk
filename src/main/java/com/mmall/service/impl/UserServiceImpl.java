package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServiceResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

import java.util.UUID;

/**
 * create by kun
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    //登陆的业务方法，供controller调用
    public ServiceResponse<User> login(String username, String password){
        int resultCount = userMapper.checkUsername(username);
        if(resultCount==0){
            return ServiceResponse.createByErrorMessage("用户名不存在");
        }
        //用户名不存在的话上一个方法已经返回，所以user为空时，用户名是存在的。就是是密码错误
        String MD5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,MD5Password);
        if(user == null){
            return ServiceResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        //密码登陆MD5
        return ServiceResponse.createBySuccess("登陆成功",user);
    }

    public ServiceResponse<String> register(User user){
        //注册时检查账号
        ServiceResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //注册时检查邮箱
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }


        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount==0){
            return ServiceResponse.createByErrorMessage("注册失败");
        }
        return ServiceResponse.createBySuccess("注册成功");
    }

    public ServiceResponse<String> checkValid(String str, String type){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            //equals方法：对象为空时调用会抛出异常
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(type);
                if(resultCount>0){
                    return ServiceResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(type);
                if(resultCount>0){
                    return ServiceResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else {
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        return ServiceResponse.createBySuccessMsg("校验成功");
    }

    //忘记密码，找回问题
    public ServiceResponse selectQuestion(String username) {
        ServiceResponse validResponse = this.checkValid(username, Const.USERNAME);
        //如果isSuccess为true，说明用户不存在
        if (validResponse.isSuccess()) {
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServiceResponse.createBySuccess(question);
        }
        return ServiceResponse.createByErrorMessage("找回密码的问题是空的");
    }
    //忘记密码，校验问题答案
    public ServiceResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        //账号存在，且问题答案正确
        if(resultCount >0 ){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey("token_"+username,forgetToken);
            return ServiceResponse.createBySuccess(forgetToken);
        }
        return ServiceResponse.createByErrorMessage("问题答案错误");
    }
}
