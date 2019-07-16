package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.vo.CartVo;

public interface ICartService {
    public ServiceResponse<CartVo> add(Integer productId, Integer UserId, Integer count);
    public ServiceResponse<CartVo> update(Integer productId, Integer UserId,Integer count);
    public ServiceResponse<CartVo> delete(String productIds, Integer userId);
    public ServiceResponse<CartVo> list(Integer userId);
    public ServiceResponse<CartVo> selectOrUnSelectAll(Integer userId, Integer productId, Integer checked);
    public ServiceResponse<Integer> getCartProductCount(Integer userId);
}
