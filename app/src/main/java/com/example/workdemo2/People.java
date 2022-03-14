package com.example.workdemo2;

import android.os.Parcel;
import android.os.Parcelable;

public class People implements Parcelable {

    int age;
    String Name;

    public People(int age, String name) {
        this.age = age;
        this.Name = name;
    }

    protected People(Parcel in) {
        age = in.readInt();
        Name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(age);
        dest.writeString(Name);
    }

    public static final Creator<People> CREATOR = new Creator<People>() {
        @Override
        public People createFromParcel(Parcel in) {
            return new People(in);
        }

        @Override
        public People[] newArray(int size) {
            return new People[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "People{" +
                "age=" + age +
                ", Name='" + Name + '\'' +
                '}';
    }
}
