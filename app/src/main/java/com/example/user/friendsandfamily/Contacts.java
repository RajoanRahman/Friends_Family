package com.example.user.friendsandfamily;

public class Contacts {
    public String name,status;
    public String userImage;

    public Contacts(){

    }
    public Contacts(String name, String status,String userImage) {
        this.name = name;
        this.status = status;
        this.userImage=userImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
