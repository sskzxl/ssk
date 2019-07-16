package com.mmall.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.RequestingUserName;
import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ICategoryService iCategoryService;
    //保存或更新产品
    public ServiceResponse productSaveOrUpdate(Product product){
        if(product != null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String [] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length > 0 ){
                    //子图的第一张图给主图
                    product.setMainImage(subImageArray[0]);
                }
            }
            if(product.getId() != null){
                //id不为空的话为更新操作
                int rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount > 0){
                    return ServiceResponse.createBySuccessMsg("更新产品成功");
                }
                return ServiceResponse.createByErrorMessage("更新产品失败");
            }else {
                int rowCount = productMapper.insert(product);
                if(rowCount > 0) {
                    return ServiceResponse.createBySuccessMsg("新增产品成功");
                }
                return ServiceResponse.createByErrorMessage("新增产品失败");

            }
        }
        return ServiceResponse.createByErrorMessage("更新或新增产品参数错误");
    }
    //修改产品销售状态
    public ServiceResponse setSaleStatus(Integer productId, Integer status){
        if(productId == null || status ==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILEEGAL_AGUMENT.getCode(),ResponseCode.ILEEGAL_AGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount > 0){
            return ServiceResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServiceResponse.createByErrorMessage("修改产品销售状态失败");
    }

    //返回商品详情
    public ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId == null ){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILEEGAL_AGUMENT.getCode(),ResponseCode.ILEEGAL_AGUMENT.getDesc());
        }
        Product product =  productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServiceResponse.createByErrorMessage("产品已下架或被删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServiceResponse.createBySuccess(productDetailVo);
    }
    //产品列表
    public ServiceResponse<PageInfo> getProductList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        //查找产品列表
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        //vo是View Object|Value Object 为视图用的对象模型 pojo是Plain Ordinary Java Object和数据库表对应的对象模型
        //bo是Business Object 为实际业务方法使用的对象模型
        for (Product productItem : productList){
            //把selectList查出来的所有pojo对象依次转换为vo对象放到集合
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServiceResponse.createBySuccess(pageResult);
    }


    //根据产品名和id搜索产品列表
    public ServiceResponse<PageInfo> searchProduct(int pageNum,int pageSize,String productName,Integer productId){
        PageHelper.startPage(pageNum,pageSize);
        //productMapper里控制空参数问题
        //查找产品列表
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuffer().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId (productName,productId);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList){
            //把selectList查出来的所有pojo对象依次转换为vo对象放到集合
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServiceResponse.createBySuccess(pageResult);
    }

    //前台商品详情
    public ServiceResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null ){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILEEGAL_AGUMENT.getCode(),ResponseCode.ILEEGAL_AGUMENT.getDesc());
        }
        Product product =  productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServiceResponse.createByErrorMessage("产品已下架或被删除");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServiceResponse.createByErrorMessage("产品已下架或被删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServiceResponse.createBySuccess(productDetailVo);
    }
    //根据关键词和分类查找产品列表
    public ServiceResponse<PageInfo> getProductByKeyWordCategoryId(String keyword, Integer categoryId, int pageNum, int pageSize,String orderBy){
        //参数错误情况
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILEEGAL_AGUMENT.getCode(),ResponseCode.ILEEGAL_AGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<Integer>();
        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类，并且还没有关键字，可能前端传了db中没有的分类。返回一个空结果集，不报错，
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServiceResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuffer().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String [] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }

        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank("keyword")?null:keyword,
                                                                             categoryIdList.size()==0?null:categoryIdList);//categoryIdList不为null，上面已经new出来
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        //待
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServiceResponse.createBySuccess(pageInfo);

    }


    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setId(product.getId());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setName(product.getName());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        //imageHost
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return  productDetailVo;
    }

    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setId(product.getId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        return productListVo;
    }
}
