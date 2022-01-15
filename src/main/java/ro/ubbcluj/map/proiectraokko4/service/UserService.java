package ro.ubbcluj.map.proiectraokko4.service;


import ro.ubbcluj.map.proiectraokko4.domain.User;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
import ro.ubbcluj.map.proiectraokko4.repository.paging.Page;
import ro.ubbcluj.map.proiectraokko4.repository.paging.Pageable;
import ro.ubbcluj.map.proiectraokko4.repository.paging.PageableImplementation;
import ro.ubbcluj.map.proiectraokko4.repository.paging.PagingRepository;
import ro.ubbcluj.map.proiectraokko4.utils.Crypt;
import ro.ubbcluj.map.proiectraokko4.utils.observer.Observable;
import ro.ubbcluj.map.proiectraokko4.utils.observer.Observer;
import ro.ubbcluj.map.proiectraokko4.utils.observer.TypeOfObservation;

import java.util.ArrayList;
import java.util.List;

/**
 * service
 */
public class UserService implements Observable {
    PagingRepository<Long, User> userRepo;

    /**
     * @param userRepo user userRepo
     */
    public UserService(PagingRepository<Long, User> userRepo) {
        this.userRepo = userRepo;
    }

    public User finduser(Long id) {
        return userRepo.findOne(id);
    }

    /**
     * @param firstName fn
     * @param lastName  ln
     * @return add user
     */
    public User addUtilizator(String username, String firstName, String lastName, String password) {
        User newUser = new User(username, firstName, lastName, password);
        try {
            User util = userRepo.save(newUser);
            notifyObservers();
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
    public User deleteUtilizator(Long id) {
        try {
            User utilizator = userRepo.findOne(id);
            if (utilizator == null) {
                throw new ValidationException("User-ul nu exista");
            }
            userRepo.delete(id);
            notifyObservers();
            return utilizator;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param id        id
     * @param firstName fn
     * @param lastName  ln
     * @return update user
     */
    public User updateUtilizator(Long id, String username, String firstName, String lastName, String password) {
        User nou = new User(username, firstName, lastName, password);
        nou.setId(id);
        try {
            User util = userRepo.update(nou);
            if (util != null) {
                throw new ValidationException("User-ul nu exista");
            }
            notifyObservers();
            return util;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return all users
     */
    public Iterable<User> getAll() {
        return userRepo.findAll();
    }

    public Long getUserId(String userName) {
        Iterable<User> list = userRepo.findAll();
        for (User curent : list) {
            if (curent.getUsername().equals(userName)) {
                return curent.getId();
            }
        }
        return null;
    }

    public Long Login(String userName, String pass) {
        Long id = getUserId(userName);
        if(id!=null) {
            User user = userRepo.findOne(id);
            if(Crypt.checkpw(pass, user.getPassword())) {
                return id;
            } else {
                return null;
            }

        }
        return null;
    }

    private int pageNumber = 0;
    private int pageSize = 18;

    public List<User> getNextUsers(String containsUsername)
    {
        this.pageNumber++;
        List<User> rez = getUsersOnPageWithUsername(this.pageNumber, containsUsername);
        if(rez.size() > 0)
        {
            return rez;
        }
        this.pageNumber--;
        return null;
    }

    public List<User> getPreviousUsers(String containsUsername)
    {
        this.pageNumber--;
        List<User> rez = getUsersOnPageWithUsername(this.pageNumber, containsUsername);
        if(rez.size() > 0)
        {
            return rez;
        }
        this.pageNumber++;
        return null;
    }

    public List<User> getUsersOnPageWithUsername(int page, String containsUsername)
    {
        if(page != -1) this.pageNumber = page;
        Pageable pageable = new PageableImplementation(this.pageNumber, this.pageSize);
        Page<User> studentPage = userRepo.findAllLike(pageable, new User(containsUsername, null, null, null));
        return studentPage.getContent().toList();
    }

    private List<Observer> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers() {
        observers.stream().forEach(x->x.update(TypeOfObservation.USER));
    }
}
