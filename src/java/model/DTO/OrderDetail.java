package model.DTO;

public class OrderDetail {
    private int orderDetailID;
    private int orderID;
    private int courseID;
    private String courseName;
    private int quantity;
    private double tuitionFee;

    public OrderDetail() {}

    public int getOrderDetailID()                      { return orderDetailID; }
    public void setOrderDetailID(int orderDetailID)    { this.orderDetailID = orderDetailID; }

    public int getOrderID()                { return orderID; }
    public void setOrderID(int orderID)    { this.orderID = orderID; }

    public int getCourseID()               { return courseID; }
    public void setCourseID(int courseID)  { this.courseID = courseID; }

    public String getCourseName()                  { return courseName; }
    public void setCourseName(String courseName)   { this.courseName = courseName; }

    public int getQuantity()               { return quantity; }
    public void setQuantity(int quantity)  { this.quantity = quantity; }

    public double getTuitionFee()                  { return tuitionFee; }
    public void setTuitionFee(double tuitionFee)   { this.tuitionFee = tuitionFee; }

    public double getTotal() { return quantity * tuitionFee; }
}
