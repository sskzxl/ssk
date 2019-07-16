package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;

    public ServiceResponse add(Integer userId, Shipping shipping){
        //前端不会传userId
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServiceResponse.createBySuccess("新增地址成功",result);
        }
        return ServiceResponse.createByErrorMessage("新增地址失败");
    }

    public ServiceResponse del(Integer shippingId , Integer userId){
        //把useId做为更新条件为了防止横向越权，保证只能修改自己的地址
        int rowCount = shippingMapper.deleteByUserIdShippingId(shippingId,userId);
        if(rowCount >0 ){
            return ServiceResponse.createBySuccessMsg("删除地址成功");
        }
        return ServiceResponse.createByErrorMessage("删除地址失败");
    }

    public ServiceResponse update(Integer userId,Shipping shipping){
        //也要防止横向越权
        //把useId做为更新条件，不能更新userId
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount >0 ){
            return ServiceResponse.createBySuccessMsg("更新地址成功");
        }
        return ServiceResponse.createByErrorMessage("更新地址失败");
    }

    public ServiceResponse<Shipping> select(Integer userId,Integer shippingId){
        //也要防止横向越权
        //把useId做为更新条件，不能更新userId
        Shipping shipping = shippingMapper.selectByUserIdShipping(userId,shippingId);
        if(shipping != null ){
            return ServiceResponse.createBySuccess(shipping);
        }
        return ServiceResponse.createByErrorMessage("查询地址失败");
    }

    public ServiceResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.shippingList(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServiceResponse.createBySuccess(pageInfo);
    }
}
