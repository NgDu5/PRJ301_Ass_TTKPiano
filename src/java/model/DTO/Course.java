package model.DTO;

import java.sql.Date;
import java.sql.Timestamp;

public class Course {
    private int courseID;
    private String courseName;
    private String image;
    private String description;
    private double tuitionFee;
    private Date startDate;
    private Date endDate;
    private int categoryID;
    private String categoryName;
    private String status;
    private int quantity;
    private Timestamp createDate;
    private Timestamp lastUpdateDate;
    private String lastUpdateUser;

    public Course() {}

    public int getCourseID()                   { return courseID; }
    public void setCourseID(int courseID)       { this.courseID = courseID; }

    public String getCourseName()                  { return courseName; }
    public void setCourseName(String courseName)   { this.courseName = courseName; }

    public String getImage()               { return image; }
    public void setImage(String image)     { this.image = image; }

    public String getDescription()                     { return description; }
    public void setDescription(String description)     { this.description = description; }

    public double getTuitionFee()                  { return tuitionFee; }
    public void setTuitionFee(double tuitionFee)   { this.tuitionFee = tuitionFee; }

    public Date getStartDate()                 { return startDate; }
    public void setStartDate(Date startDate)   { this.startDate = startDate; }

    public Date getEndDate()               { return endDate; }
    public void setEndDate(Date endDate)   { this.endDate = endDate; }

    public int getCategoryID()                 { return categoryID; }
    public void setCategoryID(int categoryID) { this.categoryID = categoryID; }

    public String getCategoryName()                    { return categoryName; }
    public void setCategoryName(String categoryName)   { this.categoryName = categoryName; }

    public String getStatus()              { return status; }
    public void setStatus(String status)   { this.status = status; }

    public int getQuantity()               { return quantity; }
    public void setQuantity(int quantity)  { this.quantity = quantity; }

    public Timestamp getCreateDate()                   { return createDate; }
    public void setCreateDate(Timestamp createDate)    { this.createDate = createDate; }

    public Timestamp getLastUpdateDate()                       { return lastUpdateDate; }
    public void setLastUpdateDate(Timestamp lastUpdateDate)    { this.lastUpdateDate = lastUpdateDate; }

    public String getLastUpdateUser()                      { return lastUpdateUser; }
    public void setLastUpdateUser(String lastUpdateUser)   { this.lastUpdateUser = lastUpdateUser; }

    public boolean isActive() { return "active".equalsIgnoreCase(status); }
}
