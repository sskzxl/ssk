package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import javafx.scene.Parent;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    //添加品类
    @RequestMapping(value = "/add_category.do" , method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse addCategory(HttpSession session, String categoryName,  @RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            //用户未登录
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员，执行添加品类操作
            return iCategoryService.addCategory(categoryName, parentId);
        }
        return ServiceResponse.createByErrorMessage("无权限操作，您不是管理员");
    }

    @RequestMapping(value = "/set_category_name.do",method = RequestMethod.POST)
    @ResponseBody
    //更新品类名称
    public ServiceResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            //用户未登录
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员，执行添加品类操作
            return iCategoryService.updateCategoryName(categoryName, categoryId);
        }
        return ServiceResponse.createByErrorMessage("无权限操作，您不是管理员");
    }

    @RequestMapping(value = "/get_category.do",method = RequestMethod.GET)
    @ResponseBody
    //获得子类名称
    public ServiceResponse<List<Category>> getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            //用户未登录
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员，执行添加品类操作
            //查询子节点的品类信息，并且不递归，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作，您不是管理员");
        }

    }

    @RequestMapping(value = "/get_deep_category.do" ,method = RequestMethod.GET)
    @ResponseBody
    //更新品类名称
    //待
    public ServiceResponse getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            //用户未登录
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员，执行添加品类操作
            //查询子节点的品类信息，并且不递归，保持平级
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }
        return ServiceResponse.createByErrorMessage("无权限操作，您不是管理员");
    }






}
