package ro.ubbcluj.map.proiectraokko4.service;

import ro.ubbcluj.map.proiectraokko4.conexitate.DFS;
import ro.ubbcluj.map.proiectraokko4.domain.Friendship;
import ro.ubbcluj.map.proiectraokko4.domain.ProfilePage;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.User;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
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

    PagingRepository<Long, User> userRepo;
    PagingRepository<Tuple<Long, Long>, Friendship> friendRepo;

    /**
     *
     * @param userRepo user repo
     * @param friendRepo friendship repo
     */
    public FriendshipService(PagingRepository<Long, User> userRepo, PagingRepository<Tuple<Long, Long>, Friendship> friendRepo) {
        this.userRepo = userRepo;
        this.friendRepo = friendRepo;
    }

    /**
     *
     * @param id1 - id ul primului prieten
     * @param id2 - id ul celui de al doilea prieten
     * @return - prietenia daca exista, altfel null
     */
    public Friendship FindOneFriend(Long id1, Long id2) {
        return friendRepo.findOne(new Tuple(id1, id2));
    }

    /**
     *
     * @param id1 user id
     * @param id2 user id
     */
    public void addFriend(Long id1, Long id2) {
        Friendship prietenie = new Friendship();
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
        Friendship p = friendRepo.findOne(new Tuple<>(id1, id2));
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
            throw new ValidationException("Friendship inexistenta!");
        }
        friendRepo.delete(new Tuple(id1, id2));
        notifyObservers();
    }

    /**
     *
     * @param id id
     * @return specific friends
     */
    public List<Tuple<User, Friendship>> getFriendRequests(Long id) {
        List<Tuple<User, Friendship>> rez = friendRepo.findAll().stream()
                .filter(x -> {
                    if (x.getId().getRight() == id || x.getId().getLeft() == id) {
                        return true;
                    } else return false;
                })
                .map(x -> {
                            if (x.getId().getRight() == id)
                                return new Tuple<User, Friendship>(userRepo.findOne(x.getId().getLeft()), x);
                            else
                                return new Tuple<User, Friendship>(userRepo.findOne(x.getId().getRight()), x);
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
        Friendship updated = new Friendship();
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
    public List<Tuple<User, Date>> getFriends(Long id) {
        List<Tuple<User, Date>> rez = friendRepo.findAll().stream()
                .filter(x -> {
                    if (x.getId().getLeft() == id && x.getStatus() == 2) {
                        return true;
                    } else if (x.getId().getRight() == id && x.getStatus() == 2) {
                        return true;
                    } else return false;
                })
                .map(x -> {
                            if (x.getId().getRight() == id)
                                return new Tuple<User, Date>(userRepo.findOne(x.getId().getLeft()), x.getDate());
                            else
                                return new Tuple<User, Date>(userRepo.findOne(x.getId().getRight()), x.getDate());
                        }
                )
                .toList();

        return rez;
    }

    private List<Tuple<User, Date>> translateFriendsToUsersWithDate(List<Friendship> list, Long id)
    {
        List<Tuple<User, Date>> rez = list.stream()
                .map(x -> {
                            if (x.getId().getRight() == id)
                                return new Tuple<User, Date>(userRepo.findOne(x.getId().getLeft()), x.getDate());
                            else
                                return new Tuple<User, Date>(userRepo.findOne(x.getId().getRight()), x.getDate());
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
    public List<Tuple<User, Date>> getFriendsFromMonth(Long id, int month)  {
        return getFriends(id).stream().filter(x -> x.getRight().toLocalDate().getMonthValue() == month).toList();
    }

    public boolean areFriends(Long id1, Long id2) {
        Friendship p = friendRepo.findOne(new Tuple<>(id1,id2));
        if (p == null || p.getStatus() != 2) return false;
        return true;
    }


    /**
     *
     * @return all friends
     */
    public Iterable<Friendship> getAllFriends() {
        return friendRepo.findAll();
    }

    /**
     * @return nb of communites
     */
    public int numarComunitati() {
        List<User> users = userRepo.findAll();
        DFS dfs = new DFS(users.size(), friendRepo.findAll(), users);
        return dfs.execute1();
    }

    /**
     * largest community
     *
     * @return int
     */
    public int CeaMaiMareComunitate() {
        List<User> users = userRepo.findAll();
        DFS dfs = new DFS(users.size(), friendRepo.findAll(), users);
        return dfs.execute2();
    }

    public ProfilePage getProfilePage(Long userId, Long id)
    {
        User user = userRepo.findOne(id);
        List<Tuple<User, Date>> friendsList = getFriends(id);
        Friendship p = friendRepo.findOne(new Tuple<>(userId, id));
        int friendshipStatus = 0;
        if(p == null) friendshipStatus = 0;
        else if(p.getId().getLeft() == userId) friendshipStatus = p.getStatus();
        else if(p.getId().getLeft() == id && p.getStatus() == 2) friendshipStatus = 2;
        else if(p.getId().getLeft() == id && p.getStatus() == 1) friendshipStatus = 4;
        else if(p.getId().getLeft() == id && p.getStatus() == 3) friendshipStatus = 5;
        System.out.println("friendship status " + friendshipStatus);
        return new ProfilePage(user.getUsername(), user.getFirstName(), user.getLastName(), friendsList, friendshipStatus);
    }

    private int listPageNumber = 0;
    private int listPageSize = 5;

    public void setListPageSize(int pageSize)
    {
        this.listPageSize = pageSize;
    }

    public List<Tuple<User, Date>> getListNextFriends(Long id, int status)
    {
        this.listPageNumber++;
        List<Tuple<User, Date>> rez = getFriendsOnListPageWithIdAndStatus(this.listPageNumber, id, status);
        if(rez.size() > 0)
        {
            return rez;
        }
        this.listPageNumber--;
        return null;
    }

    public List<Tuple<User, Date>> getListPreviousFriends(Long id, int status)
    {
        this.listPageNumber--;
        List<Tuple<User, Date>> rez = getFriendsOnListPageWithIdAndStatus(this.listPageNumber, id, status);
        if(rez.size() > 0)
        {
            return rez;
        }
        this.listPageNumber++;
        return null;
    }

    public List<Tuple<User, Date>> getFriendsOnListPageWithIdAndStatus(int page, Long id, int status) {
        if(page != -1) this.listPageNumber = page;
        Pageable pageable = new PageableImplementation(this.listPageNumber, this.listPageSize);
        Friendship p = new Friendship();
        p.setId(new Tuple<>(id, null));
        p.setStatus(status);
        Page<Friendship> friendsPage = friendRepo.findAllLike(pageable, p);
        if(friendsPage == null) return null;
        return translateFriendsToUsersWithDate(friendsPage.getContent().toList(), id);
    }

    private int pendingListPageNumber = 0;
    private int pendingListPageSize = 5;

    public List<Tuple<User, Date>> getPendingListNextFriends(Long id, int status)
    {
        this.pendingListPageNumber++;
        List<Tuple<User, Date>> rez = getFriendsOnPendingListPageWithIdAndStatus(this.pendingListPageNumber, id, status);
        if(rez.size() > 0)
        {
            return rez;
        }
        this.pendingListPageNumber--;
        return null;
    }

    public List<Tuple<User, Date>> getPendingListPreviousFriends(Long id, int status)
    {
        this.pendingListPageNumber--;
        List<Tuple<User, Date>> rez = getFriendsOnPendingListPageWithIdAndStatus(this.pendingListPageNumber, id, status);
        if(rez.size() > 0)
        {
            return rez;
        }
        this.pendingListPageNumber++;
        return null;
    }

    public List<Tuple<User, Date>> getFriendsOnPendingListPageWithIdAndStatus(int page, Long id, int status) {
        if(page != -1) this.pendingListPageNumber = page;
        Pageable pageable = new PageableImplementation(this.pendingListPageNumber, this.pendingListPageSize);
        Friendship p = new Friendship();
        p.setId(new Tuple<>(id, null));
        p.setStatus(status);
        Page<Friendship> friendsPage = friendRepo.findAllLike(pageable, p);
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
