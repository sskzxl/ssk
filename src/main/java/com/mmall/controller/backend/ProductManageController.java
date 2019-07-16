package com.mmall.controller.backend;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    IUserService iUserService;
    @Autowired
    IProductService iProductService;
    @Autowired
    IFileService iFileService;

    //更新或添加产品
    @RequestMapping("/save.do")
    @ResponseBody
    public ServiceResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //业务逻辑
            return iProductService.productSaveOrUpdate(product);
        } else {
            return ServiceResponse.createByErrorMessage("用户无权限");
        }
    }

    //修改产品销售状态
    @RequestMapping("/set_sale_status.do")
    @ResponseBody
    public ServiceResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //业务逻辑
            return iProductService.setSaleStatus(productId, status);
        } else {
            return ServiceResponse.createByErrorMessage("用户无权限");
        }
    }

    //商品详情
    @RequestMapping("/detail.do")
    @ResponseBody
    public ServiceResponse getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //业务逻辑
            return iProductService.manageProductDetail(productId);
        } else {
            return ServiceResponse.createByErrorMessage("用户无权限");
        }
    }

    //商品页
    @RequestMapping("/list.do")
    @ResponseBody
    public ServiceResponse getList(HttpSession session,
                                   @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //业务逻辑
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ServiceResponse.createByErrorMessage("用户无权限");
        }
    }


    @RequestMapping("/search.do")
    @ResponseBody
    public ServiceResponse searchProduct(HttpSession session, String productName, Integer productId,
                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //业务逻辑
            return iProductService.searchProduct(pageNum, pageSize, productName, productId);
        } else {
            return ServiceResponse.createByErrorMessage("用户无权限");
        }
    }

    @RequestMapping("/upload.do")
    @ResponseBody
    ServiceResponse upload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //业务逻辑
            //业务逻辑
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServiceResponse.createBySuccess(fileMap);

        } else {
            return ServiceResponse.createByErrorMessage("用户无权限");
        }

    }

    @RequestMapping("/rich_text_image_upload.do")
    @ResponseBody
    public Map richTextImageUpload(@RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "用户未登录");
            return resultMap;
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //业务逻辑
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传文件失败");
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传文件成功");
            resultMap.put("file_path", url);
            response.setHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;

        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "用户无权限操作");
            return resultMap;
        }

    }



}
