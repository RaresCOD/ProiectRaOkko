package ro.ubbcluj.map.proiectraokko4.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User class
 */
public class User extends Entity<Long>{
    private String username;
    /**
     * firstname
     */
    private String firstName;
    /**
     * lastname
     */
    private String lastName;
    /**
     * friends
     */
    private List<User> friends = new ArrayList<User>();
    private String password;

    public String getPassword() {
        return password;
    }

    /**
     * constructor
     * @param firstName fn
     * @param lastName ln
     */
    public User(String username, String firstName, String lastName, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    /**
     *
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * setter
     * @param firstName first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * getter
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * setter
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * getter
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * setter
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return all friends
     */
    public List<User> getFriends() {
        return friends;
    }

    /**
     *
     * @param e user
     */
    public void addFriend(User e) {
        this.friends.add(e);
    }

    /**
     *
     * @param e user
     */
    public void deleteFriend(User e) {
        friends.remove(e);
    }

    @Override
    public String toString() {
        return getId() + " " + firstName + " " + lastName + " (username: " + username + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}