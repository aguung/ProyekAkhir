package com.agungsubastian.proyekakhir.favoritemovie.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FavoriteModel implements Parcelable {
    private String id;
    private String name;
    private String date;
    private String description;
    private String image;
    private String vote;

    public FavoriteModel() {}

    public FavoriteModel(String id, String image, String name) {
        this.id = id;
        this.image = image;
        this.name = name;
    }

    private FavoriteModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        date = in.readString();
        description = in.readString();
        image = in.readString();
        vote = in.readString();
    }

    public static final Creator<FavoriteModel> CREATOR = new Creator<FavoriteModel>() {
        @Override
        public FavoriteModel createFromParcel(Parcel in) {
            return new FavoriteModel(in);
        }

        @Override
        public FavoriteModel[] newArray(int size) {
            return new FavoriteModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(date);
        parcel.writeString(description);
        parcel.writeString(image);
        parcel.writeString(vote);
    }
}
