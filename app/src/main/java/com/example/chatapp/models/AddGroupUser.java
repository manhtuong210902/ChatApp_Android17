package com.example.chatapp.models;

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
        return this.isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }
}
