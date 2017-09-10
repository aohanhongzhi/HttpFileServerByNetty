package com.battcn.netty;

/**
 * @author Levin
 * @date 2017-09-10.
 */
public class OrderResponse implements java.io.Serializable {

    private static final long serialVersionUID = -5003946216600820264L;
    private Integer orderId;
    private String respCode;
    private String desc;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "orderId=" + orderId +
                ", respCode='" + respCode + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
