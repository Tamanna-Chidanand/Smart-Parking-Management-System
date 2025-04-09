package controller;

import model.User;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private List<User> userList = new ArrayList<>();

    public void registerUser(String name, String contact) {
        userList.add(new User(name, contact));
    }

    public List<User> getAllUsers() {
        return userList;
    }
}
