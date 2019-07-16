package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;

public interface IUserService {
   public ServiceResponse<User> login(String username, String password);
   public ServiceResponse<String> register(User user);
    public ServiceResponse<String> checkValid(String str, String type);

    public ServiceResponse selectQuestion(String username);

    public ServiceResponse<String> checkAnswer(String username, String question, String answer);

    public ServiceResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    public ServiceResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    public ServiceResponse<User> updateInformation(User user);

    public ServiceResponse <User> getInformation(Integer UserId);

    public ServiceResponse checkAdminRole(User user);
}
