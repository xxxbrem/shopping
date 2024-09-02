package com.xia.springboot.controller;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.xia.springboot.controller.dto.AliPay;
import com.xia.springboot.mapper.OrderMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
public class AliPayController {

    @Resource
    private OrderMapper orderMapper;

    /*
    支付接口，调用发起支付
     */
    @GetMapping("/pay")
    public String pay(AliPay aliPay){
        //        创建支付响应
        AlipayTradePagePayResponse response;
        //        发起支付，通过前端传过来的数据封装成AliPay实体类
        try {
            response = Factory.Payment.Page().pay(aliPay.getSubject(),aliPay.getTraceNo(),aliPay.getTotalAmount(), "");
        } catch (Exception e) {
            System.err.println("调用异常，原因是："+e.getMessage());
            throw new RuntimeException(e.getMessage(),e);
        }
        return response.getBody();
    }

    /*
    第二个接口是支付成功回调的接口，我们在这个接口可以获取到支付订单的订单编号和支付时间，然后我们可以修改本地订单的支付状态。
     */
    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")){
            System.out.println("============支付宝异步回调==========================");

//            从request里面取出返回的参数
            Map<String,String> params = new HashMap<>();
            Map<String,String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()){
                params.put(name,request.getParameter(name));
            }

            // 获取到订单号
            String tradeNo = params.get("out_trade_no");
            // 获取到支付时间
            String gmtPayment = params.get("gmt_payment");

            // 支付宝验签
            if (Factory.Payment.Common().verifyNotify(params)){
                //验签通过
                System.out.println("交易名称: " + params.get("subject"));
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));

                //更新你的订单状态，这里写一个orderMapper来处理
                orderMapper.updateState(tradeNo,1,gmtPayment);
            }
        }
        return "success";
    }
}
