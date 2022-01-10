package ro.ubbcluj.map.proiectraokko4.service;


import javafx.util.Pair;
import ro.ubbcluj.map.proiectraokko4.Conexitate.DFS;
import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
import ro.ubbcluj.map.proiectraokko4.repository.Repository;
import ro.ubbcluj.map.proiectraokko4.utils.Crypt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * service
 */
public class UtilizatorService {
    Repository<Long, Utilizator> repo;
    Repository<Tuple<Long, Long>, Prietenie> repoFriend;
    Repository<Long, Message> repoMessages;

    private Long lastID() {
        Long lID = 0L;
        for(Utilizator util : repo.findAll()) {
            lID = util.getId();
        }
        return lID;
    }

    /**
     *
     * @param id1 - id ul primului prieten
     * @param id2 - id ul celui de al doilea prieten
     * @return - prietenia daca exista, altfel null
     */
    public Prietenie FindOneFriend(Long id1, Long id2) {
        return repoFriend.findOne(new Tuple(id1, id2));
    }

    /**
     *
     * @param repo user repo
     * @param repoFriend friendship repo
     */
    public UtilizatorService(Repository<Long, Utilizator> repo, Repository<Tuple<Long, Long>, Prietenie> repoFriend, Repository<Long, Message> repoMessages) {
        this.repo = repo;
        this.repoFriend = repoFriend;
        this.repoMessages = repoMessages;
    }

    public Utilizator finduser(Long id) {
        return repo.findOne(id);
    }

    /**
     *
     * @param firstName fn
     * @param lastName ln
     * @return add user
     */
    public Utilizator addUtilizator(String username, String firstName, String lastName, String password) {
        Utilizator nou = new Utilizator(username, firstName, lastName, password);
        Long id = lastID() + 1L;
        nou.setId(id);
        try {
            Utilizator util = repo.save(nou);
            return util;
        } catch (IllegalArgumentException i) {
            System.out.println(i.getMessage());
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param id id
     * @return delete user
     */
    public Utilizator deleteUtilizator(Long id) {
        try {
            Utilizator utilizator = repo.findOne(id);
            if (utilizator == null) {
                System.out.println("Id inexistent");
                return null;
            }
            for(Utilizator util : repo.findAll()) {

                    util.deleteFriend(utilizator);
                    repoFriend.delete(new Tuple(util.getId(), utilizator.getId()));
                    repoFriend.delete(new Tuple(utilizator.getId(), util.getId()));

            }
            repo.delete(id);
            return utilizator;
        } catch (IllegalArgumentException i) {
            System.out.println(i.getMessage());
        }
        return null;
    }

    /**
     *
     * @param id id
     * @param firstName fn
     * @param lastName ln
     * @return update user
     */
    public Utilizator updateUtilizator(Long id, String username, String firstName, String lastName, String password) {
        Utilizator nou = new Utilizator(username, firstName, lastName, password);
        nou.setId(id);
        try {
            Utilizator util = repo.update(nou);
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
     *
     * @param id1 user id
     * @param id2 user id
     */
    public void addFriend(Long id1, Long id2) {
        Prietenie prietenie = new Prietenie();
        Tuple<Long, Long> tuple = new Tuple(id1, id2);

        if(id1 == id2){
            throw new ValidationException("Nu-ti poti trimite o cerere de prietenie singur!");
        }
        if (repo.findOne(id1) == null) {
            throw new ValidationException("User inexistent!");
        }
        if (repo.findOne(id2) == null) {
            throw new ValidationException("User inexistent!");
        }
        if(repoFriend.findOne(new Tuple<>(id1, id2)) != null){
            throw new ValidationException("Cerere de prietenie deja trimisa!");
        }

        prietenie.setId(tuple);
        LocalDateTime currentDate = LocalDateTime.now();
        prietenie.setDate(new Date(currentDate.getYear() - 1900, currentDate.getMonthValue(), currentDate.getDayOfMonth()));
        prietenie.setStatus(1);

        repoFriend.save(prietenie);
    }

    /**
     *
     * @param id1 user id
     * @param id2 user id
     */
    public void deleteFriend(Long id1, Long id2) {
        if (repo.findOne(id1) == null) {
            throw new ValidationException("User inexistent!");
        }
        if (repo.findOne(id2) == null) {
            throw new ValidationException("User inexistent!");
        }
        if (repoFriend.findOne(new Tuple(id1, id2)) == null) {
            throw new ValidationException("Prietenie inexistenta!");
        }
        repoFriend.delete(new Tuple(id1, id2));
    }

    /**
     *
     * @param id id
     * @return specific friends
     */
    public List<Tuple<Utilizator, Prietenie>> getFriendRequests(Long id) {
        List<Tuple<Utilizator, Prietenie>> rez = repoFriend.findAll().stream()
                .filter(x -> {
                    if (x.getId().getRight() == id || x.getId().getLeft() == id) {
                        return true;
                    } else return false;
                })
                .map(x -> {
                            if (x.getId().getRight() == id)
                                return new Tuple<Utilizator, Prietenie>(repo.findOne(x.getId().getLeft()), x);
                            else
                                return new Tuple<Utilizator, Prietenie>(repo.findOne(x.getId().getRight()), x);
                        }
                )
                .toList();

        return rez;
    }


    /**
     * Answer a friend request.
     * @param id1 id user1
     * @param id2 id user2
     * @param answer the status to be set
     */
    public void answerFriendRequest(Long id1, Long id2, int answer)
    {
        if(repoFriend.findOne(new Tuple<>(id1, id2)).getStatus() == 3 && answer == 3) return;
        if(repoFriend.findOne(new Tuple<>(id1, id2)).getStatus() == 2) return;
        if(answer == 1) return;
        Prietenie updated = new Prietenie();
        updated.setId(new Tuple<>(id1, id2));
        LocalDateTime currentDate = LocalDateTime.now();
        updated.setDate((java.sql.Date) new Date(currentDate.getYear() - 1900, currentDate.getMonthValue(), currentDate.getDayOfMonth()));
        updated.setStatus(answer);
        repoFriend.update(updated);
    }

    /**
     *
     * @param id id
     * @return specific friends
     */
    public List<Tuple<Utilizator, Date>> getFriends(Long id) {
        List<Tuple<Utilizator, Date>> rez = repoFriend.findAll().stream()
                .filter(x -> {
                    if (x.getId().getLeft() == id && x.getStatus() == 2) {
                        return true;
                    } else if (x.getId().getRight() == id && x.getStatus() == 2) {
                        return true;
                    } else return false;
                })
                .map(x -> {
                    if (x.getId().getRight() == id)
                        return new Tuple<Utilizator, Date>(repo.findOne(x.getId().getLeft()), x.getDate());
                    else
                        return new Tuple<Utilizator, Date>(repo.findOne(x.getId().getRight()), x.getDate());
                        }
                )
                .toList();

        return rez;
    }

    /**
     *
     * @param id id, int month
     * @return specific friends
     */
    public List<Tuple<Utilizator, Date>> getFriendsFromMonth(Long id, int month)  {
        return getFriends(id).stream().filter(x -> x.getRight().toLocalDate().getMonthValue() == month).toList();
    }

    /**
     *
     * @return all users
     */
    public Iterable<Utilizator> getAll() {
        return repo.findAll();
    }

    /**
     *
     * @return all friends
     */
    public Iterable<Prietenie> getAllFriends() {
        return repoFriend.findAll();
    }

    /**
     *
     * @return nb of communites
     */
    public int numarComunitati() {
        DFS dfs = new DFS(Math.toIntExact(lastID()), repoFriend.findAll(), repo.findAll());
        return dfs.execute1();
    }

    /**
     * largest community
     * @return int
     */
    public int JonnyVorbaretu() {
        DFS dfs = new DFS(Math.toIntExact(lastID()), repoFriend.findAll(), repo.findAll());
        return dfs.execute2();
    }

    public Long getUserId(String userName) {
        Iterable<Utilizator> list = repo.findAll();
        for (Utilizator curent: list) {
            if(curent.getUsername().equals(userName)) {
                return curent.getId();
            }
        }
        return null;
    }

    public Long Login(String userName, String pass) {
        Long id = getUserId(userName);
        if(id!=null) {
            Utilizator user = repo.findOne(id);
            if(Crypt.checkpw(pass, user.getPassword())) {
                return id;
            } else {
                return null;
            }

        }
        return null;
    }

    public void addMessage(Long id1, Long id2, String msg) {
        Utilizator from = repo.findOne(id1);
        Utilizator user2 = repo.findOne(id2);
        List<Utilizator> to = new ArrayList<Utilizator>();
        to.add(user2);
        Message message = new Message(from, to, msg);
        message.setReplyMsg(null);
        message.setData(LocalDateTime.now());
        try{
            repoMessages.save(message);
        } catch (ValidationException e) {
            System.out.println(e);
        }
    }

    public void addGroupMessage(Long id1, List<Long> Listid, String msg) {
        Utilizator from = repo.findOne(id1);
        List<Utilizator> to = new ArrayList<Utilizator>();
        Listid
                .stream()
                .forEach(x -> to.add(repo.findOne(x)));
//        Utilizator user2 = repo.findOne(id2);

//        to.add(user2);
        Message message = new Message(from, to, msg);
        message.setReplyMsg(null);
        message.setData(LocalDateTime.now());
        try{
            repoMessages.save(message);
        } catch (ValidationException e) {
            System.out.println(e);
        }
    }

    public List<Message> getAllMessages(Long userId) {
        Iterable<Message> all = repoMessages.findAll();
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
        Iterable<Message> all = repoMessages.findAll();
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
        Iterable<Message> all = repoMessages.findAll();

        for (Message curent: all) {

        }
    }

    public boolean areFriends(Long id1, Long id2) {
        Prietenie p = repoFriend.findOne(new Tuple<>(id1,id2));
        if (p == null || p.getStatus() != 2) return false;
        return true;
    }


    public void sendReply(Long msgId, Long userId, String msg) {
        Utilizator from = repo.findOne(userId);
        Message message = repoMessages.findOne(msgId);
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
            repoMessages.save(newReply);
        } catch (ValidationException e) {
            System.out.println(e);
        }

    }

    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }

    public List<Message> findMsgs(Long userId, Long curentId) {
        List<Message> allMsg = repoMessages.findAll();
        List<Utilizator> userList = new ArrayList<>();
        userList.add(repo.findOne(userId));
        userList.add(repo.findOne(curentId));
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
        List<Message> allMsg = repoMessages.findAll();
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
}
