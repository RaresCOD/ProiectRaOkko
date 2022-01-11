package ro.ubbcluj.map.proiectraokko4.service;


import ro.ubbcluj.map.proiectraokko4.domain.ProfilePage;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
import ro.ubbcluj.map.proiectraokko4.repository.paging.Page;
import ro.ubbcluj.map.proiectraokko4.repository.paging.Pageable;
import ro.ubbcluj.map.proiectraokko4.repository.paging.PageableImplementation;
import ro.ubbcluj.map.proiectraokko4.repository.paging.PagingRepository;
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
    //Repository<Tuple<Long, Long>, Prietenie> friendRepo;
    //Repository<Long, Message> messagesRepo;

    /**
     * @param userRepo user userRepo
     */
    public UtilizatorService(PagingRepository<Long, Utilizator> userRepo) {
        this.userRepo = userRepo;
    }

    /*
    public UtilizatorService(Repository<Long, Utilizator> userRepo, Repository<Tuple<Long, Long>, Prietenie> friendRepo, Repository<Long, Message> messagesRepo) {
        this.userRepo = userRepo;
        this.friendRepo = friendRepo;
        this.messagesRepo = messagesRepo;
    }

    private Long lastID() {
        Long lID = 0L;
        for(Utilizator util : userRepo.findAll()) {
            lID = util.getId();
        }
        return lID;
    }

    public Prietenie FindOneFriend(Long id1, Long id2) {
        return friendRepo.findOne(new Tuple(id1, id2));
    }
     */

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
        // Long id = lastID() + 1L;
        //newUser.setId(id);
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

    /*
    public void addMessage(Long id1, Long id2, String msg) {
        Utilizator from = userRepo.findOne(id1);
        Utilizator user2 = userRepo.findOne(id2);
        List<Utilizator> to = new ArrayList<Utilizator>();
        to.add(user2);
        Message message = new Message(from, to, msg);
        message.setReplyMsg(null);
        message.setData(LocalDateTime.now());
        try{
            messagesRepo.save(message);
        } catch (ValidationException e) {
            System.out.println(e);
        }
    }

    public void addGroupMessage(Long id1, List<Long> Listid, String msg) {
        Utilizator from = userRepo.findOne(id1);
        List<Utilizator> to = new ArrayList<Utilizator>();
        Listid
                .stream()
                .forEach(x -> to.add(userRepo.findOne(x)));
//        Utilizator user2 = userRepo.findOne(id2);

//        to.add(user2);
        Message message = new Message(from, to, msg);
        message.setReplyMsg(null);
        message.setData(LocalDateTime.now());
        try{
            messagesRepo.save(message);
        } catch (ValidationException e) {
            System.out.println(e);
        }
    }

    public List<Message> getAllMessages(Long userId) {
        Iterable<Message> all = messagesRepo.findAll();
        List<Message> rez = new ArrayList<>();
        for(Message curent:all) {
            List<Utilizator> list = curent.getTo();
            Boolean found = false;
            if (curent.getFrom().getId() == userId) {
                found = true;
            }
            for(Utilizator to:list) {
                if(to.getId() == userId) {
                    found = true;
                }
            }
            if (found == true) {
                rez.add(curent);
            }
        }
        return rez;
    }

    public void showAllMessagesForThisUser(Long userId) {
        Iterable<Message> all = messagesRepo.findAll();
        for(Message curent:all) {
            List<Utilizator> list = curent.getTo();
            Boolean found = false;
            for(Utilizator to:list) {
                if(to.getId() == userId) {
                    found = true;
                }
            }
            if (found == true) {
                System.out.println(curent.getFrom().getFirstName() + " sent " + curent.getMsg() + " id: " + curent.getId() + " " + curent.getData());
            }
        }
    }

    public void showAllGroupChats(Long userId) {
        Iterable<Message> all = messagesRepo.findAll();

        for (Message curent: all) {

        }
    }

    public boolean areFriends(Long id1, Long id2) {
        Prietenie p = friendRepo.findOne(new Tuple<>(id1,id2));
        if (p == null || p.getStatus() != 2) return false;
        return true;
    }


    public void sendReply(Long msgId, Long userId, String msg) {
        Utilizator from = userRepo.findOne(userId);
        Message message = messagesRepo.findOne(msgId);
        List<Utilizator> to = new ArrayList<>();
        to.add(message.getFrom());
        for(Utilizator curent: message.getTo()) {
            if(curent.getId() != userId) {
                to.add(curent);
            }
        }
        Message newReply = new Message(from, to, msg);
        newReply.setReplyMsg(message);
        newReply.setData(LocalDateTime.now());
        try{
            messagesRepo.save(newReply);
        } catch (ValidationException e) {
            System.out.println(e);
        }

    }

    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }

    public List<Message> findMsgs(Long userId, Long curentId) {
        List<Message> allMsg = messagesRepo.findAll();
        List<Utilizator> userList = new ArrayList<>();
        userList.add(userRepo.findOne(userId));
        userList.add(userRepo.findOne(curentId));
        List<Message> rezultat = new ArrayList<>();
        for(Message curent : allMsg) {
            List<Utilizator> to = curent.getTo();
            Utilizator from = curent.getFrom();
            to.add(from);
            if (listEqualsIgnoreOrder(to, userList) == true) {
                rezultat.add(curent);
            }
        }
        return rezultat;
    }

    public List<String> allChats(Long userId) {
        List<Message> allMsg = messagesRepo.findAll();
        List<List<Utilizator>> rez = new ArrayList<>();
        List<String> rezBun = new ArrayList<>();
        for(Message msg : allMsg) {
            List<Utilizator> group = msg.getTo();
            group.add(msg.getFrom());
            boolean found = false;
            for(Utilizator curent: group) {
                if(curent.getId() == userId) {
                    found = true;
                }
            }
            for(List<Utilizator> curent: rez) {
                if(listEqualsIgnoreOrder(curent, group) == true){
                    found = false;
                }
            }
            if(found == true) {
                rez.add(group);
                String grup = group.stream()
                        .filter(x -> x.getId() != userId)
                        .map(x -> x.getUsername())
                        .reduce("", (u,v) -> u.concat(v + " "));

                rezBun.add(grup);
            }

        }
        return rezBun;
    }
     */

    private int pageNumber = 0;
    private int pageSize = 12;

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
        this.pageNumber = page;
        Pageable pageable = new PageableImplementation(page, this.pageSize);
        Page<Utilizator> studentPage = userRepo.findAllLike(pageable, new Utilizator(containsUsername, null, null));
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
