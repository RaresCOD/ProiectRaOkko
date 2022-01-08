package ro.ubbcluj.map.proiectraokko4.service;

import ro.ubbcluj.map.proiectraokko4.Conexitate.DFS;
import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
import ro.ubbcluj.map.proiectraokko4.repository.Repository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public class FriendshipService {

    Repository<Long, Utilizator> userRepo;
    Repository<Tuple<Long, Long>, Prietenie> friendRepo;

    /**
     *
     * @param userRepo user userRepo
     * @param friendRepo friendship userRepo
     */
    public FriendshipService(Repository<Long, Utilizator> userRepo, Repository<Tuple<Long, Long>, Prietenie> friendRepo) {
        this.userRepo = userRepo;
        this.friendRepo = friendRepo;
    }

    /**
     *
     * @param id1 - id ul primului prieten
     * @param id2 - id ul celui de al doilea prieten
     * @return - prietenia daca exista, altfel null
     */
    public Prietenie FindOneFriend(Long id1, Long id2) {
        return friendRepo.findOne(new Tuple(id1, id2));
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
        if (userRepo.findOne(id1) == null) {
            throw new ValidationException("User inexistent!");
        }
        if (userRepo.findOne(id2) == null) {
            throw new ValidationException("User inexistent!");
        }
        if(friendRepo.findOne(new Tuple<>(id1, id2)) != null){
            throw new ValidationException("Cerere de prietenie deja trimisa!");
        }

        prietenie.setId(tuple);
        LocalDateTime currentDate = LocalDateTime.now();
        prietenie.setDate(new Date(currentDate.getYear() - 1900, currentDate.getMonthValue(), currentDate.getDayOfMonth()));
        prietenie.setStatus(1);

        friendRepo.save(prietenie);
    }

    /**
     *
     * @param id1 user id
     * @param id2 user id
     */
    public void deleteFriend(Long id1, Long id2) {
        if (userRepo.findOne(id1) == null) {
            throw new ValidationException("User inexistent!");
        }
        if (userRepo.findOne(id2) == null) {
            throw new ValidationException("User inexistent!");
        }
        if (friendRepo.findOne(new Tuple(id1, id2)) == null) {
            throw new ValidationException("Prietenie inexistenta!");
        }
        friendRepo.delete(new Tuple(id1, id2));
    }

    /**
     *
     * @param id id
     * @return specific friends
     */
    public List<Tuple<Utilizator, Prietenie>> getFriendRequests(Long id) {
        List<Tuple<Utilizator, Prietenie>> rez = friendRepo.findAll().stream()
                .filter(x -> {
                    if (x.getId().getRight() == id || x.getId().getLeft() == id) {
                        return true;
                    } else return false;
                })
                .map(x -> {
                            if (x.getId().getRight() == id)
                                return new Tuple<Utilizator, Prietenie>(userRepo.findOne(x.getId().getLeft()), x);
                            else
                                return new Tuple<Utilizator, Prietenie>(userRepo.findOne(x.getId().getRight()), x);
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
        if(friendRepo.findOne(new Tuple<>(id1, id2)).getStatus() == 3 && answer == 3) return;
        if(friendRepo.findOne(new Tuple<>(id1, id2)).getStatus() == 2) return;
        if(answer == 1) return;
        Prietenie updated = new Prietenie();
        updated.setId(new Tuple<>(id1, id2));
        LocalDateTime currentDate = LocalDateTime.now();
        updated.setDate((java.sql.Date) new Date(currentDate.getYear() - 1900, currentDate.getMonthValue(), currentDate.getDayOfMonth()));
        updated.setStatus(answer);
        friendRepo.update(updated);
    }

    /**
     *
     * @param id id
     * @return specific friends
     */
    public List<Tuple<Utilizator, Date>> getFriends(Long id) {
        List<Tuple<Utilizator, Date>> rez = friendRepo.findAll().stream()
                .filter(x -> {
                    if (x.getId().getLeft() == id && x.getStatus() == 2) {
                        return true;
                    } else if (x.getId().getRight() == id && x.getStatus() == 2) {
                        return true;
                    } else return false;
                })
                .map(x -> {
                            if (x.getId().getRight() == id)
                                return new Tuple<Utilizator, Date>(userRepo.findOne(x.getId().getLeft()), x.getDate());
                            else
                                return new Tuple<Utilizator, Date>(userRepo.findOne(x.getId().getRight()), x.getDate());
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

    public boolean areFriends(Long id1, Long id2) {
        Prietenie p = friendRepo.findOne(new Tuple<>(id1,id2));
        if (p == null || p.getStatus() != 2) return false;
        return true;
    }


    /**
     *
     * @return all friends
     */
    public Iterable<Prietenie> getAllFriends() {
        return friendRepo.findAll();
    }

    /**
     * @return nb of communites
     */
    public int numarComunitati() {
        List<Utilizator> users = userRepo.findAll();
        DFS dfs = new DFS(users.size(), friendRepo.findAll(), users);
        return dfs.execute1();
    }

    /**
     * largest community
     *
     * @return int
     */
    public int JonnyVorbaretu() {
        List<Utilizator> users = userRepo.findAll();
        DFS dfs = new DFS(users.size(), friendRepo.findAll(), users);
        return dfs.execute2();
    }
}
