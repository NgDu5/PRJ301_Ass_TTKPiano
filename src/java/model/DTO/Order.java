package model.DTO;

import java.sql.Timestamp;
import java.util.List;

public class Order {
    private int orderID;
    private String userID;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private Timestamp orderDate;
    private String paymentMethod;
    private String paymentStatus;
    private double totalAmount;

    // Joined from Users table for display
    private String customerDisplayName;

    private List<OrderDetail> orderDetails;

    public Order() {}

    public int getOrderID()                { return orderID; }
    public void setOrderID(int orderID)    { this.orderID = orderID; }

    public String getUserID()              { return userID; }
    public void setUserID(String userID)   { this.userID = userID; }

    public String getGuestName()                   { return guestName; }
    public void setGuestName(String guestName)     { this.guestName = guestName; }

    public String getGuestEmail()                  { return guestEmail; }
    public void setGuestEmail(String guestEmail)   { this.guestEmail = guestEmail; }

    public String getGuestPhone()                  { return guestPhone; }
    public void setGuestPhone(String guestPhone)   { this.guestPhone = guestPhone; }

    public Timestamp getOrderDate()                    { return orderDate; }
    public void setOrderDate(Timestamp orderDate)      { this.orderDate = orderDate; }

    public String getPaymentMethod()                       { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod)     { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus()                       { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus)     { this.paymentStatus = paymentStatus; }

    public double getTotalAmount()                 { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getCustomerDisplayName()                         { return customerDisplayName; }
    public void setCustomerDisplayName(String customerDisplayName) { this.customerDisplayName = customerDisplayName; }

    public List<OrderDetail> getOrderDetails()                     { return orderDetails; }
    public void setOrderDetails(List<OrderDetail> orderDetails)    { this.orderDetails = orderDetails; }
}
