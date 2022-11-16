package com.example.chatapp;

import com.example.chatapp.models.User;

public class AddGroupUser {
    private User info;
    private boolean isChecked;

    public AddGroupUser(User info, boolean isChecked) {
        this.info = info;
        this.isChecked = isChecked;
    }

    public User getInfo() {
        return info;
    }

    public void setInfo(User info) {
        this.info = info;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
