package com.battcn.netty.test;

/**
 * @author Levin
 * @create 2017/9/11 0011
 */
public class Order implements java.io.Serializable {

    private static final long serialVersionUID = 1826067782744144943L;

    private Integer orderId;
    private String userName;
    private String productName;
    private String phoneNumber;
    private String address;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Order(){}
    public Order(Integer orderId, String userName, String productName, String phoneNumber, String address) {
        this.orderId = orderId;
        this.userName = userName;
        this.productName = productName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userName='" + userName + '\'' +
                ", productName='" + productName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

}
