package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    ICartService iCartService;

    @RequestMapping("add")
    @ResponseBody
    public ServiceResponse<CartVo> add(HttpSession session, Integer productId, Integer count){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(productId,user.getId(),count);
    }

    @RequestMapping("update")
    @ResponseBody
    public ServiceResponse<CartVo> update(HttpSession session, Integer productId, Integer count){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(productId,user.getId(),count);
    }

    @RequestMapping("delete_product")
    @ResponseBody
    public ServiceResponse<CartVo> deleteProduct(HttpSession session, String productIds){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.delete(productIds,user.getId());
    }

    @RequestMapping("list")
    @ResponseBody
    public ServiceResponse<CartVo> list(HttpSession session){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }
    @RequestMapping("select_all")
    @ResponseBody
    public ServiceResponse<CartVo> selectAll(HttpSession session){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectAll(user.getId(),null,Const.Cart.CHECKED);
    }
    @RequestMapping("un_select_all")
    @ResponseBody
    public ServiceResponse<CartVo> unSelectAll(HttpSession session){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectAll(user.getId(),null,Const.Cart.UNCHECKED);
    }

    @RequestMapping("select")
    @ResponseBody
    public ServiceResponse<CartVo> select(HttpSession session,Integer productId){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectAll(user.getId(),productId,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select")
    @ResponseBody
    public ServiceResponse<CartVo> unSelect(HttpSession session,Integer productId){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectAll(user.getId(),productId,Const.Cart.UNCHECKED);
    }
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServiceResponse<Integer> getCartProductCount(HttpSession session){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.getCartProductCount(user.getId());
    }



}
