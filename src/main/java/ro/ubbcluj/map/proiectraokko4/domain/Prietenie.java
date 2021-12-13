package ro.ubbcluj.map.proiectraokko4.domain;

import java.sql.Date;

/**
 * friendship class
 */
public class Prietenie extends Entity<Tuple<Long,Long>> {

    /**
     * date
     */
    Date date;
    int status;

    /**
     * constructor
     */
    public Prietenie() {}

    @Override
    public String toString() {
        return "Prietenie{" +
                "P1 = " + this.getId().getLeft() + ", P2 = " + this.getId().getRight() + ", date = " + date.toString() +
                '}';
    }

    /**
     *
     * @return the date of the friendship
     */
    public Date getDate() {
        return date;
    }


    /**
     *
     * @param date the date of the friendship to be set
     */
    public void setDate(Date date) { this.date = date; }

    /**
     *
     * @return the status of the friendship (1 - pending, 2 - approved, 3 - rejected)
     */
    public int getStatus() {
        return status;
    }


    /**
     *
     * @param status the status of the friendship to be set (1 - pending, 2 - approved, 3 - rejected)
     */
    public void setStatus(int status) { this.status = status; }
}
