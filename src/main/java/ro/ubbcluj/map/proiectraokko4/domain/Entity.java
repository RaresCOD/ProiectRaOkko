package ro.ubbcluj.map.proiectraokko4.domain;

import java.io.Serializable;

/**
 *
 * @param <ID> id
 */
public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;

    /**
     * id
     */
    private ID id;

    /**
     * @return return id
     */
    public ID getId() {
        return id;
    }

    /**
     *
     * @param id set id
     */
    public void setId(ID id) {
        this.id = id;
    }
}