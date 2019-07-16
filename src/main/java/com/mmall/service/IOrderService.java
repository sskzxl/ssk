package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

public interface IOrderService {
    public ServiceResponse pay(Integer userId, Long orderNo, String path);
    public ServiceResponse aliCallback(Map<String,String> params);
    public ServiceResponse queryOrderPayStatus(Integer userId, Long orderNo);
    public ServiceResponse createOrder(Integer userId, Integer ShippingId);
    public ServiceResponse<String> cancel(Integer userId, Long orderNo);
    public ServiceResponse getOrderCartProduct(Integer userId);
    public ServiceResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);
    public ServiceResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);
    public ServiceResponse<OrderVo> manageDetail(Long orderNo);
    public ServiceResponse<PageInfo> manageList(int pageNum,int pageSize);
    public ServiceResponse<String> manageSendGoods(Long orderNo);
    public ServiceResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);
}
