package ro.ubbcluj.map.proiectraokko4.service;

import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.domain.Event;
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

    Repository<Long, Message> messagesRepo;
    PagingRepository<Long, Utilizator> userRepo;

    public MessageService(PagingRepository<Long, Utilizator> userRepo, Repository<Long, Message> messagesRepo) {
        this.userRepo = userRepo;
        this.messagesRepo = messagesRepo;
    }

    public void addMessage(Long id1, List<Long> Listid, String msg) {
        Utilizator from = userRepo.findOne(id1);
        List<Utilizator> to = new ArrayList<Utilizator>();
        Listid.stream().forEach(x -> to.add(userRepo.findOne(x)));

        Message message = new Message(from, to, msg);
        message.setReplyMsg(null);
        message.setData(LocalDateTime.now());
        messagesRepo.save(message);
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

    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }

    public List<Message> findMsgs(List<Long> userIds) {
        List<Message> rez = new ArrayList<>();
        List<Message> messages = messagesRepo.findAll();
        for(Message message : messages)
        {
            List<Long> toUsersIds = message.getTo().stream().map(x->x.getId()).toList();
            List<Long> toUsersIdsM = new ArrayList<>();
            toUsersIdsM.addAll(toUsersIds);
            toUsersIdsM.add(message.getFrom().getId());
            if(listEqualsIgnoreOrder(toUsersIdsM, userIds) && message.getMsg() != null)
            {
                rez.add(message);
            }
        }
        rez = rez.stream().sorted(new Comparator<Message>() {
            @Override
            public int compare(Message e1, Message e2) {
                return e1.getData().compareTo(e2.getData());
            }
        }).toList();
        return rez;
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
                        .reduce("", (u,v) -> u.concat(v + ";"));

                rezBun.add(grup);
            }

        }
        rezBun = rezBun.stream().sorted(new Comparator<String>() {
            @Override
            public int compare(String e1, String e2) {
                return e1.compareTo(e2);
            }
        }).toList();
        return rezBun;
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
