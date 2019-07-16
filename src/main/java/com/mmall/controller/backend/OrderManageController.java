package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {
    @Autowired
    IUserService iUserService;
    @Autowired
    IOrderService iOrderService;
    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse orderList(HttpSession session, @RequestParam(value = "pageNum" ,defaultValue = "1")int pageNum,
                                       @RequestParam(value = "pageSize" ,defaultValue = "10")int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //业务逻辑
            return iOrderService.manageList(pageNum,pageSize);
        } else {
            return ServiceResponse.createByErrorMessage("用户无权限");
        }
    }
    @RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse orderDetail(HttpSession session,Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //业务逻辑
            return iOrderService.manageDetail(orderNo);
        } else {
            return ServiceResponse.createByErrorMessage("用户无权限");
        }
    }
    //根据精确订单号后台搜索订单，先分页，后期再加上模糊搜索和地址搜索等
    @RequestMapping("search.do")
    @ResponseBody
    public ServiceResponse<PageInfo> orderSearch(HttpSession session, Long orderNo, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");

        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return iOrderService.manageSearch(orderNo,pageNum,pageSize);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作");
        }
    }



    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServiceResponse<String> orderSendGoods(HttpSession session, Long orderNo){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");

        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return iOrderService.manageSendGoods(orderNo);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作");
        }
    }
}
