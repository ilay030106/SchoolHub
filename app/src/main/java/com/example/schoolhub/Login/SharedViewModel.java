package com.example.schoolhub.Login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> firstName = new MutableLiveData<>();
    private final MutableLiveData<String> userID = new MutableLiveData<>();

    public void setFirstName(String name) {
        firstName.setValue(name);
    }

    public LiveData<String> getFirstName() {
        return firstName;
    }
    public void setUserID(String ID) {
        userID.setValue(ID);
    }

    public LiveData<String> getUserID() {
        return userID;
    }
}