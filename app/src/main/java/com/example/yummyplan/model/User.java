package com.example.yummyplan.model;

public class User {

    private int id;
    private String fullName;
    private String email;
    private String password;
    private String birthDate;
    private String role;
    private String user_img;

    public User() {
    }

    // ما حطينا ال id هان لأنه أصلا عاملينه auto increment في قاعدة البيانات فهي الي بتعطيه
    public User(String fullName, String email, String password, String birthDate, String role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.role = role;
    }

    // هان حطيناه لأنه ممكن أحتاجه لما بدي أعمل تحديث أو حذف
    public User(int id, String fullName, String email, String password, String birthDate, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.role = role;
    }

    // كونستركتور كامل مع الصورة
    public User(int id, String fullName, String email, String password, String birthDate, String role, String user_img) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.role = role;
        this.user_img = user_img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }
}


