package ro.ubbcluj.map.proiectraokko4.service;

import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
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

import java.time.LocalDateTime;
import java.util.*;

public class MessageService implements Observable {

    PagingRepository<Long, Message> messagesRepo;
    PagingRepository<Long, Utilizator> userRepo;

    public MessageService(PagingRepository<Long, Utilizator> userRepo, PagingRepository<Long, Message> messagesRepo) {
        this.userRepo = userRepo;
        this.messagesRepo = messagesRepo;
    }

    public void addMessage(Long id1, Long id2, String msg) {
        Utilizator from = userRepo.findOne(id1);
        Utilizator user2 = userRepo.findOne(id2);
        List<Utilizator> to = new ArrayList<Utilizator>();
        to.add(user2);
        Message message = new Message(from, to, msg);
        message.setReplyMsg(null);
        message.setData(LocalDateTime.now());
        try {
            messagesRepo.save(message);
        } catch (ValidationException e) {
            System.out.println(e);
        }
        notifyObservers();
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
        try {
            messagesRepo.save(message);
        } catch (ValidationException e) {
            System.out.println(e);
        }
        notifyObservers();
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
        Collections.sort(rez, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return m1.getData().compareTo(m2.getData());
            }
        });
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
        notifyObservers();
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

    private int pageNumber = 0;
    private int pageSize = 3;

    public List<Message> getNextMessages() {
        this.pageNumber++;
        return getMessagesOnPage(this.pageNumber);
    }

    public List<Message> getMessagesOnPage(int page) {
        this.pageNumber = page;
        Pageable pageable = new PageableImplementation(page, this.pageSize);
        Page<Message> messagesPage = messagesRepo.findAll(pageable);
        return messagesPage.getContent().toList();
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
        observers.stream().forEach(x->x.update(TypeOfObservation.MESSAGE));
    }
}
