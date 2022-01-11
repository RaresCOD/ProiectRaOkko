package ro.ubbcluj.map.proiectraokko4.service;

import ro.ubbcluj.map.proiectraokko4.Conexitate.DFS;
import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.ProfilePage;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
import ro.ubbcluj.map.proiectraokko4.repository.Repository;
import ro.ubbcluj.map.proiectraokko4.repository.paging.Page;
import ro.ubbcluj.map.proiectraokko4.repository.paging.Pageable;
import ro.ubbcluj.map.proiectraokko4.repository.paging.PageableImplementation;
import ro.ubbcluj.map.proiectraokko4.repository.paging.PagingRepository;
import ro.ubbcluj.map.proiectraokko4.utils.observer.Observable;
import ro.ubbcluj.map.proiectraokko4.utils.observer.Observer;
import ro.ubbcluj.map.proiectraokko4.utils.observer.TypeOfObservation;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendshipService implements Observable {

    PagingRepository<Long, Utilizator> userRepo;
    PagingRepository<Tuple<Long, Long>, Prietenie> friendRepo;

    /**
     *
     * @param userRepo user userRepo
     * @param friendRepo friendship userRepo
     */
    public FriendshipService(PagingRepository<Long, Utilizator> userRepo, PagingRepository<Tuple<Long, Long>, Prietenie> friendRepo) {
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
        Prietenie p = friendRepo.findOne(new Tuple<>(id1, id2));
        if(p != null && p.getStatus() != 3){
            throw new ValidationException("Cerere de prietenie deja trimisa!");
        }
        else if(p!= null && p.getStatus() == 3)
        {
            friendRepo.delete(new Tuple(id1, id2));
        }

        prietenie.setId(tuple);
        LocalDateTime currentDate = LocalDateTime.now();
        prietenie.setDate(new Date(currentDate.getYear() - 1900, currentDate.getMonthValue(), currentDate.getDayOfMonth()));
        prietenie.setStatus(1);

        friendRepo.save(prietenie);
        notifyObservers();
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
        notifyObservers();
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
        notifyObservers();
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

    private List<Tuple<Utilizator, Date>> translateFriendsToUsersWithDate(List<Prietenie> list, Long id)
    {
        List<Tuple<Utilizator, Date>> rez = list.stream()
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

    public ProfilePage getProfilePage(Long userId, Long id)
    {
        Utilizator user = userRepo.findOne(id);
        List<Tuple<Utilizator, Date>> friendsList = getFriends(id);
        Prietenie p = friendRepo.findOne(new Tuple<>(userId, id));
        int friendshipStatus = 0;
        if(p == null) friendshipStatus = 0;
        else if(p.getId().getLeft() == userId) friendshipStatus = p.getStatus();
        else if(p.getId().getLeft() == id && p.getStatus() == 2) friendshipStatus = 2;
        else if(p.getId().getLeft() == id && p.getStatus() == 1) friendshipStatus = 4;
        else if(p.getId().getLeft() == id && p.getStatus() == 3) friendshipStatus = 5;
        return new ProfilePage(user.getUsername(), user.getFirstName(), user.getLastName(), friendsList, friendshipStatus);
    }

    private int listPageNumber = 0;
    private int listPageSize = 8;

    public void setListPageSize(int pageSize)
    {
        this.listPageSize = pageSize;
    }

    public List<Tuple<Utilizator, Date>> getListNextFriends(Long id, int status)
    {
        this.listPageNumber++;
        List<Tuple<Utilizator, Date>> rez = getFriendsOnListPageWithIdAndStatus(this.listPageNumber, id, status);
        if(rez.size() > 0)
        {
            return rez;
        }
        this.listPageNumber--;
        return null;
    }

    public List<Tuple<Utilizator, Date>> getListPreviousFriends(Long id, int status)
    {
        this.listPageNumber--;
        List<Tuple<Utilizator, Date>> rez = getFriendsOnListPageWithIdAndStatus(this.listPageNumber, id, status);
        if(rez.size() > 0)
        {
            return rez;
        }
        this.listPageNumber++;
        return null;
    }

    public List<Tuple<Utilizator, Date>> getFriendsOnListPageWithIdAndStatus(int page, Long id, int status) {
        this.listPageNumber = page;
        Pageable pageable = new PageableImplementation(page, this.listPageSize);
        Prietenie p = new Prietenie();
        p.setId(new Tuple<>(id, null));
        p.setStatus(status);
        Page<Prietenie> friendsPage = friendRepo.findAllLike(pageable, p);
        if(friendsPage == null) return null;
        return translateFriendsToUsersWithDate(friendsPage.getContent().toList(), id);
    }

    private int pendingListPageNumber = 0;
    private int pendingListPageSize = 14;

    public List<Tuple<Utilizator, Date>> getPendingListNextFriends(Long id, int status)
    {
        this.pendingListPageNumber++;
        List<Tuple<Utilizator, Date>> rez = getFriendsOnPendingListPageWithIdAndStatus(this.pendingListPageNumber, id, status);
        if(rez.size() > 0)
        {
            return rez;
        }
        this.pendingListPageNumber--;
        return null;
    }

    public List<Tuple<Utilizator, Date>> getPendingListPreviousFriends(Long id, int status)
    {
        this.pendingListPageNumber--;
        List<Tuple<Utilizator, Date>> rez = getFriendsOnPendingListPageWithIdAndStatus(this.pendingListPageNumber, id, status);
        if(rez.size() > 0)
        {
            return rez;
        }
        this.pendingListPageNumber++;
        return null;
    }

    public List<Tuple<Utilizator, Date>> getFriendsOnPendingListPageWithIdAndStatus(int page, Long id, int status) {
        this.pendingListPageNumber = page;
        Pageable pageable = new PageableImplementation(page, this.pendingListPageSize);
        Prietenie p = new Prietenie();
        p.setId(new Tuple<>(id, null));
        p.setStatus(status);
        Page<Prietenie> friendsPage = friendRepo.findAllLike(pageable, p);
        if(friendsPage == null) return null;
        return translateFriendsToUsersWithDate(friendsPage.getContent().toList(), id);
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
        observers.stream().forEach(x->x.update(TypeOfObservation.FRIENDSHIP));
    }
}