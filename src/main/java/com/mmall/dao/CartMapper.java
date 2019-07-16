package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByProductIdUserId(@Param("productId") Integer productId, @Param("useId")Integer userId);

    List<Cart> selectCartByUserId(Integer UserId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    int deleteByUserIdProductIds(@Param("userId")Integer userId,@Param("productIdList") List<String> productIdList);

    int selectCartProductChecked(@Param("userId")Integer userId,@Param("productId") Integer productId,@Param("checked")Integer checked);

    int selectCartProductCount(Integer userId);

    List<Cart> selectCheckedCartByUserId(Integer userId);
}