package com.bitms.api.client.trade.fund.bean;


import com.bitms.api.client.bean.sign.BitmsObject;
import com.bitms.api.client.mapping.ApiField;

/**
 * @author : yukai
 * @version : 1.0
 * @discription : 提币提现申请bean
 * @create : 2018-07-05-10
 **/
public class WithdrawBean extends BitmsObject {

    @ApiField("address")
    private String address;

    @ApiField("currency")
    private String currency;

    @ApiField("amount")
    private String amount;

    @ApiField("fundPwd")
    private String fundPwd;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFundPwd() {
        return fundPwd;
    }

    public void setFundPwd(String fundPwd) {
        this.fundPwd = fundPwd;
    }
}