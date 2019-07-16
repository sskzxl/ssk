package com.mmall.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {

    public ServiceResponse productSaveOrUpdate(Product product);

    public ServiceResponse setSaleStatus(Integer productId, Integer status);

    public ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId);

    public ServiceResponse<PageInfo> getProductList(int pageNum, int pageSize);

    public ServiceResponse<PageInfo> searchProduct(int pageNum,int pageSize,String productName,Integer productId);

    public ServiceResponse<ProductDetailVo> getProductDetail(Integer productId);
    public ServiceResponse<PageInfo> getProductByKeyWordCategoryId(String keyword, Integer categoryId, int pageNum, int pageSize,String orderBy);
}
