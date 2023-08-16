package com.example.wages;

public class Review {
    String workerid,rating;

    public Review(){}
    public Review(String workerid, String rating) {
        this.workerid = workerid;
        this.rating = rating;
    }

    public String getWorkerid() {
        return workerid;
    }

    public void setWorkerid(String workerid) {
        this.workerid = workerid;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
