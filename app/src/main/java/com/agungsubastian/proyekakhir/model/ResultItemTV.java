package com.agungsubastian.proyekakhir.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultItemTV implements Parcelable {

    @SerializedName("original_name")
    @Expose
    private String originalName;
    @SerializedName("first_air_date")
    @Expose
    private String firstAirDate;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("vote_average")
    @Expose
    private Float voteAverage;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    public ResultItemTV() {}

    public String getOriginalName() {
        return originalName;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public Integer getId() {
        return id;
    }

    public Float getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setVoteAverage(Float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(originalName);
        parcel.writeString(firstAirDate);
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        if (voteAverage == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(voteAverage);
        }
        parcel.writeString(overview);
        parcel.writeString(posterPath);
    }

    private ResultItemTV(Parcel in) {
        originalName = in.readString();
        firstAirDate = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            voteAverage = null;
        } else {
            voteAverage = in.readFloat();
        }
        overview = in.readString();
        posterPath = in.readString();
    }

    public static final Creator<ResultItemTV> CREATOR = new Creator<ResultItemTV>() {
        @Override
        public ResultItemTV createFromParcel(Parcel in) {
            return new ResultItemTV(in);
        }

        @Override
        public ResultItemTV[] newArray(int size) {
            return new ResultItemTV[size];
        }
    };
}
