package ro.ubbcluj.map.proiectraokko4.service;


import ro.ubbcluj.map.proiectraokko4.Conexitate.DFS;
import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
import ro.ubbcluj.map.proiectraokko4.repository.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

/**
 * service
 */
public class UtilizatorService {
    Repository<Long, Utilizator> userRepo;
    //Repository<Tuple<Long, Long>, Prietenie> friendRepo;
    //Repository<Long, Message> messagesRepo;

    /**
     * @param userRepo   user userRepo
     * @param friendRepo friendship userRepo
     */
    public UtilizatorService(Repository<Long, Utilizator> userRepo, Repository<Tuple<Long, Long>, Prietenie> friendRepo) {
        this.userRepo = userRepo;
        //this.friendRepo = friendRepo;
    }

    public Utilizator finduser(Long id) {
        return userRepo.findOne(id);
    }

    /**
     * @param firstName fn
     * @param lastName  ln
     * @return add user
     */
    public Utilizator addUtilizator(String username, String firstName, String lastName) {
        Utilizator newUser = new Utilizator(username, firstName, lastName);
        try {
            Utilizator util = userRepo.save(newUser);
            return util;
        } catch (IllegalArgumentException i) {
            System.out.println(i.getMessage());
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * @param id id
     * @return delete user
     */
    public Utilizator deleteUtilizator(Long id) {
        try {
            Utilizator utilizator = userRepo.findOne(id);
            if (utilizator == null) {
                System.out.println("Id inexistent");
                return null;
            }
            userRepo.delete(id);
            return utilizator;
        } catch (IllegalArgumentException i) {
            System.out.println(i.getMessage());
        }
        return null;
    }

    /**
     * @param id        id
     * @param firstName fn
     * @param lastName  ln
     * @return update user
     */
    public Utilizator updateUtilizator(Long id, String username, String firstName, String lastName) {
        Utilizator nou = new Utilizator(username, firstName, lastName);
        nou.setId(id);
        try {
            Utilizator util = userRepo.update(nou);
            if (util != null) {
                System.out.println("Utilizator inexistent");
            }
            return util;
        } catch (IllegalArgumentException i) {
            System.out.println(i.getMessage());
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * @return all users
     */
    public Iterable<Utilizator> getAll() {
        return userRepo.findAll();
    }

    public Long getUserId(String userName) {
        Iterable<Utilizator> list = userRepo.findAll();
        for (Utilizator curent : list) {
            if (curent.getUsername().equals(userName)) {
                return curent.getId();
            }
        }
        return null;
    }

    public Long Login(String userName) {
        return getUserId(userName);
    }

}
