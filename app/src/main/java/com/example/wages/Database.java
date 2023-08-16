package com.example.wages;

public class Database {



    String fullName, userName, email, phoneNo, city, profession, image,userId;
    Double latitude, longitude;

    public Database(){

    }

    public Database(String fullName, String userName, String phoneNo, String city, String userId,
                    Double latitude, Double longitude, String image) {

        this.fullName = fullName;
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.city = city;
        this.image = image;
        this.userId = userId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Database(String fullName, String userName,  String email, String phoneNo, String city,
                    String  profession, String userId, Double latitude, Double longitude,
                    String image) {

        this.fullName = fullName;
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.email = email;
        this.city = city;
        this.profession = profession;
        this.image = image;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getFullName() {return fullName;}
    public void setFullName(String fullName) {this.fullName = fullName;}

    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPhoneNo() {return phoneNo;}
    public void setPhoneNo(String phoneNo) {this.phoneNo = phoneNo;}

    public String getProfession() {return profession;}
    public void setProfession(String profession) {this.profession = profession;}

    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}

    public String getImage() {return image;}
    public void setImage(String image) {this.image = image;}

    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public Double getLatitude() {return latitude;}
    public void setLatitude(Double latitude) {this.latitude = latitude;}

    public Double getLongitude() {return longitude;}
    public void setLongitude(Double longitude) {this.longitude = longitude;}
}
