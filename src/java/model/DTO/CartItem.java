package model.DTO;

import java.io.Serializable;

public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int courseID;
    private String courseName;
    private String image;
    private int quantity;
    private double tuitionFee;

    public CartItem() {}

    public CartItem(int courseID, String courseName, String image, int quantity, double tuitionFee) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.image = image;
        this.quantity = quantity;
        this.tuitionFee = tuitionFee;
    }

    public int getCourseID()               { return courseID; }
    public void setCourseID(int courseID)  { this.courseID = courseID; }

    public String getCourseName()                  { return courseName; }
    public void setCourseName(String courseName)   { this.courseName = courseName; }

    public String getImage()               { return image; }
    public void setImage(String image)     { this.image = image; }

    public int getQuantity()               { return quantity; }
    public void setQuantity(int quantity)  { this.quantity = quantity; }

    public double getTuitionFee()                  { return tuitionFee; }
    public void setTuitionFee(double tuitionFee)   { this.tuitionFee = tuitionFee; }

    public double getTotal() { return quantity * tuitionFee; }
}
