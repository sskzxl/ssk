package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;

public interface IUserService {
   public ServiceResponse<User> login(String username, String password);
   public ServiceResponse<String> register(User user);
    public ServiceResponse<String> checkValid(String str, String type);

    public ServiceResponse selectQuestion(String username);

    public ServiceResponse<String> checkAnswer(String username, String question, String answer);
}
