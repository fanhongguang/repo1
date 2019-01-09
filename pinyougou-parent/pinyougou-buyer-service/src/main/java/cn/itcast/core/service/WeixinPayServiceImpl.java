package cn.itcast.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.itheima.util.HttpClient;
import com.pinyougou.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid; //微信公众账号或开放平台APP的唯一标识

    @Value("${partner}")
    private String partner; //财付通平台的商户账号

    @Value("${partnerkey}")
    private String partnerkey;//财付通平台的商户密钥


    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //设置参数（设置订单金额等纤细信息 根据统一下单ADP参数）
        Map<String, String> param = new HashMap<>();
        param.put("appid",appid);
        param.put("mch_id",partner);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        param.put("body","品优购");//sahngpinmiaoshu
        param.put("total_fee",total_fee);
        param.put("out_trade_no",out_trade_no);
        param.put("spbill_create_ip","127.0.0.1");
        param.put("notify_url","http://www.itcast.cn");
        param.put("trade_type","NATIVE");
        //发送请求（调统一下单API）生成的xml
        try {
            //签名
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("生成的xml"+signedXml);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(signedXml);
            client.post();
            //获取结果
            String content = client.getContent();
            System.out.println("获取到的结果"+content);
            //将结果转换为map格式
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            Map<String, String> hashMap = new HashMap<>();
            //返回页面需要的数据
            hashMap.put("code_url",map.get("code_url"));
            hashMap.put("out_trade_no",out_trade_no);
            hashMap.put("total_fee",total_fee);
            return hashMap;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }


    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        //设置参数
        Map param=new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/orderquery";
        //发送请求
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("sign"+xmlParam);
            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();

            //获取结果
            String resoult = client.getContent();
            System.out.println(resoult);
            Map<String, String> map = WXPayUtil.xmlToMap(resoult);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
