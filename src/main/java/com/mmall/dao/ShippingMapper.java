package com.mmall.dao;

import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUserIdShippingId(@Param("shippingId") Integer shippingId ,@Param("userId")Integer userId);

    int updateByShipping(Shipping record);

    Shipping selectByUserIdShipping(@Param("userId")Integer userId ,@Param("shippingId") Integer shippingId);

    List<Shipping> shippingList(Integer userId);
}