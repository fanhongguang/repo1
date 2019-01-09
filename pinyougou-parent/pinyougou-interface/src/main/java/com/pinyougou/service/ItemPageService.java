package com.pinyougou.service;

public interface ItemPageService {

    public boolean genItemHtml(Long goodsId);

    boolean deleteHtml(Long[] goodsIds);
}
