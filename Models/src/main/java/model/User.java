package model;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    String username;
    String password;
    private Integer id;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public User(){

    }

    public User(Integer integer, String username, String password) {
        this.id = integer;

        this.username = username;
        this.password = password;
    }

    public User(Integer integer, String username) {
        this.id = integer;
        this.username = username;
        this.password = null;
    }

    public User(String username, String password) {
        this.id = null;
        this.username = username;
        this.password = password;
    }

    public User(Integer id){
        this.id = id;
        this.username = null;
        this.password = null;
    }

    public User(String username) {
        this.id = null;
        this.username = username;
        this.password = null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "model.User{id=" + getId() + ", " +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
