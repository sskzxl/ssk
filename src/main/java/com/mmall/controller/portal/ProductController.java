package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_ADDPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    IProductService iProductService;
    //前台商品详情
    @RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse<ProductDetailVo> detail(Integer productId){
        return iProductService.getProductDetail(productId);
    }
    //前台商品列表
    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse<PageInfo> list(@RequestParam(value = "keyword",required = false) String keyword,
                                          @RequestParam(value = "categoryId",required = false) Integer categoryId,
                                          @RequestParam(value = "pageSize",defaultValue = "10" ) int pageSize,
                                          @RequestParam(value = "pageNum",defaultValue = "1" ) int pageNum,
                                          @RequestParam(value = "orderBy",defaultValue = "" ) String orderBy){
        return iProductService.getProductByKeyWordCategoryId(keyword,categoryId,pageNum,pageSize,orderBy);
    }

}
