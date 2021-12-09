package com.example.workdemo.main;

import androidx.annotation.NonNull;

public class MainListEntity {
    String title = "";
    String content = "";
    public MainListEntity(@NonNull String title, @NonNull String content){
        this.title = title;
        this.content = content;
    }
}
