package model.DTO;

public class User {
    private String userID;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private boolean status;

    public User() {}

    public User(String userID, String fullName, String email, String phone, String role) {
        this.userID = userID;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = true;
    }

    public String getUserID()              { return userID; }
    public void setUserID(String userID)   { this.userID = userID; }

    public String getPassword()                { return password; }
    public void setPassword(String password)   { this.password = password; }

    public String getFullName()                { return fullName; }
    public void setFullName(String fullName)   { this.fullName = fullName; }

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }

    public String getPhone()               { return phone; }
    public void setPhone(String phone)     { this.phone = phone; }

    public String getRole()                { return role; }
    public void setRole(String role)       { this.role = role; }

    public boolean isStatus()              { return status; }
    public void setStatus(boolean status)  { this.status = status; }

    public boolean isAdmin() { return "admin".equalsIgnoreCase(role); }
}
