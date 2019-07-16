package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import com.mmall.vo.ProductListVo;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService{
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public ServiceResponse<Integer> getCartProductCount(Integer userId){
        if (userId == null){
            return ServiceResponse.createBySuccess(0);
        }
        return ServiceResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }


    //productId为null时则根据checked选择全选或者全反选，否则是单选或单反选
    public ServiceResponse<CartVo> selectOrUnSelectAll(Integer userId, Integer productId, Integer checked){
        cartMapper.selectCartProductChecked(userId,productId,checked);
        return this.list(userId);
    }
    //查询购物车所有
    public ServiceResponse<CartVo> list(Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServiceResponse.createBySuccess(cartVo);
    }
    public ServiceResponse<CartVo> add(Integer productId, Integer userId,Integer count){
        if(productId == null && count == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILEEGAL_AGUMENT.getCode(),ResponseCode.ILEEGAL_AGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByProductIdUserId(productId,userId);
        if(cart == null){
            //产品和购物车是多对多的关系
            //这个产品不在这个购物车，需要新增这个产品的记录
            Cart cartItem = new Cart();
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartMapper.insert(cartItem);
        }else{
            //如果这个产品已经存在，则数量相加
            System.out.println("cart.getQuantity"+" "+cart.getQuantity());
            System.out.println("count"+" "+count);
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    public ServiceResponse<CartVo> update(Integer productId, Integer userId,Integer count){
        if(productId == null && count == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILEEGAL_AGUMENT.getCode(),ResponseCode.ILEEGAL_AGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByProductIdUserId(productId,userId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKey(cart);
        return this.list(userId);
    }

    public ServiceResponse<CartVo> delete(String productIds, Integer userId){
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productIdList)){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILEEGAL_AGUMENT.getCode(), ResponseCode.ILEEGAL_AGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productIdList);
        return this.list(userId);
    }


    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        //查询该用户购物车集合（一条购物车记录对应一件商品）
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        //初始化购物车所有产品总价
        BigDecimal catTotalPrice = new BigDecimal("0");
        //查询到的购物车集合 cartList 转换到cartProductVoList
        if(!CollectionUtils.isEmpty(cartList)){
            for(Cart cartItem : cartList){
                //cart转换到cartProductVo
                CartProductVo cartProductVo = new CartProductVo();
                //cartProduct里面的Cart信息
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                //cartProduct里面的product信息
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStock(product.getStock());
                    //
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    //库存大于欲购买数量时，购买数量为欲购买数量;   小与时，购买数量为库存的数量
                    //buyLimitCount = cartItem.getQuantity();   buyLimitCount = product.getStock();
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    //当前购物车某产品总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选，增加到整个购物车总价中,计算购物车所有产品总价
                    System.out.println(catTotalPrice+"  "+cartProductVo.getProductTotalPrice());
                    catTotalPrice = BigDecimalUtil.add(catTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(catTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }
    private boolean getAllCheckedStatus(Integer userId ){
        if(userId == null){
            return false;
        }
        //如果返回0，说明所有都被勾选
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
    }
}
