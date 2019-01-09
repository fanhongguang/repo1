package com.pinyougou.sellergoods.service.impl;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.entity.Goods_Desc;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private GoodsDescDao goodsDescDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private ItemCatDao itemCatDao;

    @Autowired
    private SellerDao sellerDao;

    /**
     * 增加
     */
    @Override
    public void save(Goods_Desc goods) {
        System.out.println(goods.getItemList().size());
        goods.getGoods().setAuditStatus("0");
        goodsDao.insert(goods.getGoods());    //插入商品表
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescDao.insert(goods.getGoodsDesc());//插入商品扩展数据
        setItemList(goods);
    }

    ///更新操作
    @Override
    public void update(Goods_Desc goods_desc) {
        goodsDao.updateByPrimaryKey(goods_desc.getGoods());
        goodsDescDao.updateByPrimaryKey(goods_desc.getGoodsDesc());
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(goods_desc.getGoods().getId());
        itemDao.deleteByExample(itemQuery);
        setItemList(goods_desc);
    }

    @Override
    public void updateStatus(Long[] ids,String status) {
        for (Long id : ids) {
            Goods goods = goodsDao.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsDao.updateByPrimaryKey(goods);
        }
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            Goods goods = goodsDao.selectByPrimaryKey(id);
            goods.setIsDelete("1");
            goodsDao.updateByPrimaryKey(goods);
        }
    }

    @Override
    public List<Item> findItemListByGoodsIdAndStatus(Long[] goodsIds, String status) {
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andStatusEqualTo(status);
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        return itemDao.selectByExample(query);
    }

    //根据goodsid查询
    @Override
    public Goods_Desc findOne(Long id) {
        Goods_Desc goods_desc = new Goods_Desc();
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goods_desc.setGoods(goods);
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        goods_desc.setGoodsDesc(goodsDesc);

        Item item = new Item();
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(query);
        goods_desc.setItemList(itemList);
        return goods_desc;
    }
    //分页查询
    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {
        PageHelper.startPage(page, rows);
        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria createCriteria = query.createCriteria();
        if(goods != null){
            if(goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus())){
                createCriteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if(goods.getGoodsName() != null && !"".equals(goods.getGoodsName())){
                createCriteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            if(goods.getSellerId() != null && !"".equals(goods.getSellerId()) && !"admin".equals(goods.getSellerId())){
                createCriteria.andSellerIdEqualTo(goods.getSellerId());
            }
            createCriteria.andIsDeleteIsNull();
        }

        Page<Goods> goodsList = (Page<Goods>)goodsDao.selectByExample(query);
        return new PageResult(goodsList.getTotal(), goodsList.getResult());
    }

    //其他数据添加
    private void add(Item item,Goods_Desc goods){
        item.setGoodsId(goods.getGoods().getId());//商品SPU编号
        item.setSellerId(goods.getGoods().getSellerId());//商家编号
        item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
        item.setCreateTime(new Date());//创建日期
        item.setUpdateTime(new Date());//修改日期
        //品牌名称
        Brand brand = brandDao.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //分类名称
        ItemCat itemCat = itemCatDao.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //商家名称
        Seller seller = sellerDao.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getNickName());
        //图片地址（取spu的第一个图片）
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList.size() > 0) {
            item.setImage((String) imageList.get(0).get("url"));
        }
    }

    //SKU规格详情添加
    private void setItemList(Goods_Desc goods){
        if ("1".equals(goods.getGoods().getIsEnableSpec())){
            for (Item item : goods.getItemList()) {
                //标题
                String title = goods.getGoods().getGoodsName();
                Map<String, Object> specMap = JSON.parseObject(item.getSpec());
                for (String key : specMap.keySet()) {
                    title += " " + specMap.get(key);
                }
                item.setTitle(title);
                item.setStatus("1");
                add(item,goods);
                itemDao.insert(item);
            }
        }else {
            Item item = new Item();
            item.setSpec("{}");
            item.setTitle(goods.getGoods().getGoodsName());
            item.setPrice(goods.getGoods().getPrice());
            item.setIsDefault("1");
            item.setNum(9999);
            add(item,goods);
            itemDao.insert(item);
        }
    }
}