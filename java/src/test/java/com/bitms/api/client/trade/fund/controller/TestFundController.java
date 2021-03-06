package com.bitms.api.client.trade.fund.controller;

import com.bitms.api.client.BitmsServiceFactory;
import com.bitms.api.client.bean.resp.fund.LeveragedAccountInfoBean;
import com.bitms.api.client.bean.resp.fund.PureSpotAssetBean;
import com.bitms.api.client.bean.resp.fund.WithdrawRecordResponse;
import com.bitms.api.client.bean.sign.ApiResponse;
import com.bitms.api.client.service.FundService;
import com.bitms.api.client.service.bean.*;
import com.bitms.api.client.tool.JSONUtils;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @version : 1.0
 * @discription : Asset test class
 * @create : 2018-07-07-14
 **/
public class TestFundController {

    //Use timestamp as AES key
    BitmsServiceFactory factory = BitmsServiceFactory.newInstance(String.valueOf(System.currentTimeMillis()));

    FundService fundService = factory.newFundService();

    /**
     * Pure spot account
     *
     * @throws Exception
     */
    @Test
    public void testGetPureSpotAsset() {
        PureSpotBean data = new PureSpotBean();
        data.setCurrency("btc");
        ApiResponse response = fundService.getPureSpotAsset(data);
        System.out.println(response.getBody());
        List<PureSpotAssetBean> result = (List<PureSpotAssetBean>) response.getData();
        System.out.println(result);
    }

    /**
     * Future account information
     *
     * @throws Exception
     */
    @Test
    public void testGetFutureAccountInfo() throws IOException {
        LeveragedSpotBean data = new LeveragedSpotBean();
        data.setSymbol("btc2usd");
        ApiResponse response = fundService.getFutureAccountInfo(data);
        System.out.println(response.getBody());
        String temp = JSONUtils.writeValue(response.getData());
        LeveragedAccountInfoBean result = JSONUtils.readValue(temp, LeveragedAccountInfoBean.class);
        System.out.println(result);
    }

    /**
     * Coin withdrawal request
     *
     * @throws Exception
     */
    @Test
    public void testWithdrawBitms() {
        WithdrawBean data = new WithdrawBean();
        data.setAddress("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
        data.setAmount(new BigDecimal(0.01));
        data.setCurrency("btc");
        data.setFundPwd("123456");
        ApiResponse response = fundService.withdrawBitms(data);
        System.out.println(response.getBody());
        Long result = (Long) response.getData();
        System.out.println(result);
    }

    /**
     * Coin cancellation
     *
     * @throws Exception
     */
    @Test
    public void testWithdrawCancel() {
        WithdrawCancelBean data = new WithdrawCancelBean();
        data.setWithdrawId(120748699621003264l);
        ApiResponse response = fundService.withdrawCancel(data);
        System.out.println(response.getBody());
        Long result = (Long) response.getData();
        System.out.println(result);
    }

    /**
     * Cash withdrawal record
     *
     * @throws Exception
     */
    @Test
    public void testWithdrawRecords() {
        WithdrawRecordBean data = new WithdrawRecordBean();
        data.setCurrency("btc");
        ApiResponse response = fundService.withdrawRecords(data);
        System.out.println(response.getBody());
        Map<String, Object> result = (Map<String, Object>) response.getData();
        List<WithdrawRecordResponse> resResult = (List<WithdrawRecordResponse>) result.get("list");
        System.out.println(resResult);
    }

    /**
     * Recharge record
     *
     * @throws Exception
     */
    @Test
    public void testDepositRecords() {
        WithdrawRecordBean data = new WithdrawRecordBean();
        data.setCurrency("btc");
        ApiResponse response = fundService.depositRecords(data);
        System.out.println(response.getBody());
        Map<String, Object> result = (Map<String, Object>) response.getData();
        List<WithdrawRecordResponse> resResult = (List<WithdrawRecordResponse>) result.get("list");
        System.out.println(resResult);
    }


}
