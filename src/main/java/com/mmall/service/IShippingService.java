package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Shipping;

public interface IShippingService {
    public ServiceResponse add(Integer userId, Shipping shipping);
    public ServiceResponse del(Integer shippingId , Integer userId);
    public ServiceResponse update(Integer userId,Shipping shipping);
    public ServiceResponse<Shipping> select(Integer userId,Integer shippingId);
    public ServiceResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);
}
