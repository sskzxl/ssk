package com.mmall.controller.portal;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.OrderMapper;
import com.mmall.pojo.Order;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    IOrderService iOrderService;
    private static final  Logger logger = LoggerFactory.getLogger(OrderController.class);

    @RequestMapping("create.do")
    @ResponseBody
    public ServiceResponse create(HttpSession session, Integer shippingId){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServiceResponse cancel(HttpSession session, Long orderNo){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(),orderNo);
    }
    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServiceResponse getOrderCartProduct(HttpSession session){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse getOrderDetail(HttpSession session, Long orderNo){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse list(HttpSession session, @RequestParam(value = "pageNum" ,defaultValue = "1")int pageNum,
                                @RequestParam(value = "pageSize" ,defaultValue = "10")int pageSize){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }

    @RequestMapping("pay.do")
    @ResponseBody
    public ServiceResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(),orderNo,path);
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        //requestParams是value是string数组。
        for(Iterator iterator = requestParams.keySet().iterator(); iterator.hasNext();){
            String name = (String)iterator.next();
            String [] values = (String[])requestParams.get(name);
            String valueStr = "";
            for(int i=0; i<values.length; i++){
                //第二个元素开始用,分隔
                valueStr = (i == values.length-1)? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name,valueStr);
        }
        logger.info("支付宝回调，sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //非常重要，验证回调的正确性，是不是支付宝发的，并且呢还要避免重复通知
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                System.out.println("回调失败");
                return ServiceResponse.createByErrorMessage("非法请求，验证不通过，再恶意请求即报警");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调异常",e);
        }
        ServiceResponse serviceResponse = iOrderService.aliCallback(params);

        if(serviceResponse.isSuccess()){
            return Const.AliPayCallBack.RESPONSE_SUCCESS;

        }
        return Const.AliPayCallBack.RESPONSE_FAILED;
    }


    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServiceResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
        User user= (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        ServiceResponse serviceResponse= iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serviceResponse.isSuccess()){
            return ServiceResponse.createBySuccess(true);
        }
        //不需要报错
        return ServiceResponse.createBySuccess(false);
    }
}
