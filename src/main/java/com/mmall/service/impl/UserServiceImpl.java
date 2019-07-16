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
import sun.plugin.com.Utils;

import java.nio.channels.SeekableByteChannel;
import java.util.Timer;
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
            //2019.05.18 bug： checkUsername(str) 误写为 checkUsername(type)，导致任何调用都是返回没有发现已存在。
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount>0){
                    return ServiceResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
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
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServiceResponse.createBySuccess(forgetToken);
        }
        return ServiceResponse.createByErrorMessage("问题答案错误");
    }
    //校验token，修改密码
    public ServiceResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {

        if (StringUtils.isBlank(forgetToken)) {
            return ServiceResponse.createByErrorMessage("参数错误,token需要传递");
        }
        ServiceResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX +username);
        if (StringUtils.isBlank(token)) {
            return ServiceResponse.createByErrorMessage("token错误或过期");
        }
        if (StringUtils.equals(token, forgetToken)) {
            //token正确，修改密码
            String MD5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username, MD5Password);
            if(resultCount>0){
                return ServiceResponse.createBySuccessMsg("修改密码成功");
            }
        }else {
            return ServiceResponse.createByErrorMessage("token错误,请重新获取");
        }
        return ServiceResponse.createByErrorMessage("修改密码失败");
    }

    public ServiceResponse<String> resetPassword(String passwordOld, String passwordNew, User user){
        //为了防止横向确权，需要校验这个旧密码是这个用户的。(如果只有旧密码可能在数据库查出多条记录，密码不唯一)
        int resultCount =  userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServiceResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount >0 ){
            return ServiceResponse.createBySuccessMsg("修改密码成功");
        }
        return ServiceResponse.createByErrorMessage("修改密码失败");
    }

    public ServiceResponse<User> updateInformation(User user){
        //username是不可更新的
        //需要校验email是否不通于数据库中其他用户的email
        int resultCount = userMapper.checkEmailByUserId( user.getEmail(), user.getId());
        if(resultCount>0){
            return ServiceResponse.createByErrorMessage("邮箱已经存在");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServiceResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServiceResponse.createByErrorMessage("更新个人信息失败");
    }
    //获取个人信息
    public ServiceResponse <User> getInformation(Integer UserId){
        User user = userMapper.selectByPrimaryKey(UserId);
        if(user == null){
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess(user);
    }
    //判断是否管理员
    public ServiceResponse checkAdminRole(User user){

        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServiceResponse.createBySuccess();
        }
        return ServiceResponse.createByError();
    }
}
