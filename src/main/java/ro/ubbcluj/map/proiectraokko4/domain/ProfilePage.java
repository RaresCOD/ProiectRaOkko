package ro.ubbcluj.map.proiectraokko4.domain;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ProfilePage {
    private String username;
    private String firstName;
    private String lastName;
    private  List<Tuple<Utilizator, Date>> friendsList = new ArrayList<Tuple<Utilizator, Date>>();
    private int friendshipStatus;



    /**
     * constructor
     * @param firstName fn
     * @param lastName ln
     */
    public ProfilePage(String username, String firstName, String lastName, List<Tuple<Utilizator, Date>> friendsList, int friendshipStatus) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.friendsList = friendsList;
        this.friendshipStatus = friendshipStatus;
    }

    /**
     * getter
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * getter
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * getter
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * getter
     * @return all friends with the date of accept
     */
    public List<Tuple<Utilizator, Date>> getFriends() {
        return friendsList;
    }

    /**
     * getter
     * @return friendship status
     */
    public int getFriendshipStatus() { return friendshipStatus; }
}
