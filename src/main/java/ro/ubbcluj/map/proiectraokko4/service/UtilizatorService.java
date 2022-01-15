package ro.ubbcluj.map.proiectraokko4.service;


import ro.ubbcluj.map.proiectraokko4.domain.ProfilePage;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
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
public class UtilizatorService implements Observable {
    PagingRepository<Long, Utilizator> userRepo;

    /**
     * @param userRepo user userRepo
     */
    public UtilizatorService(PagingRepository<Long, Utilizator> userRepo) {
        this.userRepo = userRepo;
    }

    public Utilizator finduser(Long id) {
        return userRepo.findOne(id);
    }

    /**
     * @param firstName fn
     * @param lastName  ln
     * @return add user
     */
    public Utilizator addUtilizator(String username, String firstName, String lastName, String password) {
        Utilizator newUser = new Utilizator(username, firstName, lastName, password);
        try {
            Utilizator util = userRepo.save(newUser);
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
    public Utilizator deleteUtilizator(Long id) {
        try {
            Utilizator utilizator = userRepo.findOne(id);
            if (utilizator == null) {
                System.out.println("Id inexistent");
                return null;
            }
            userRepo.delete(id);
            notifyObservers();
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
    public Utilizator updateUtilizator(Long id, String username, String firstName, String lastName, String password) {
        Utilizator nou = new Utilizator(username, firstName, lastName, password);
        nou.setId(id);
        try {
            Utilizator util = userRepo.update(nou);
            if (util != null) {
                System.out.println("Utilizator inexistent");
                return null;
            }
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

    public Long Login(String userName, String pass) {
        Long id = getUserId(userName);
        if(id!=null) {
            Utilizator user = userRepo.findOne(id);
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

    public List<Utilizator> getNextUsers(String containsUsername)
    {
        this.pageNumber++;
        List<Utilizator> rez = getUsersOnPageWithUsername(this.pageNumber, containsUsername);
        if(rez.size() > 0)
        {
            return rez;
        }
        this.pageNumber--;
        return null;
    }

    public List<Utilizator> getPreviousUsers(String containsUsername)
    {
        this.pageNumber--;
        List<Utilizator> rez = getUsersOnPageWithUsername(this.pageNumber, containsUsername);
        if(rez.size() > 0)
        {
            return rez;
        }
        this.pageNumber++;
        return null;
    }

    public List<Utilizator> getUsersOnPageWithUsername(int page, String containsUsername)
    {
        if(page != -1) this.pageNumber = page;
        Pageable pageable = new PageableImplementation(this.pageNumber, this.pageSize);
        Page<Utilizator> studentPage = userRepo.findAllLike(pageable, new Utilizator(containsUsername, null, null, null));
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
