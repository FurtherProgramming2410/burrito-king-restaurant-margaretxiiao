package main.java.model;

import java.sql.SQLException;
import dao.UserDao;
import dao.UserDaoImpl;
import model.User;

public class Model {
    private UserDao userDao;
    private User currentUser; 

    public Model() {
        userDao = new UserDaoImpl();
    }

    public void setup() throws SQLException {
        userDao.setup();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }
}
