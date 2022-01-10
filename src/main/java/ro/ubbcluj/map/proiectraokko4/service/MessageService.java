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
import java.util.ArrayList;
import java.util.List;

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
    }

    public void showAllMessagesForThisUser(Long userId) {
        Iterable<Message> all = messagesRepo.findAll();
        for (Message curent : all) {
            List<Utilizator> list = curent.getTo();
            Boolean found = false;
            for (Utilizator to : list) {
                if (to.getId() == userId) {
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

        for (Message curent : all) {

        }
    }

    public void sendReply(Long msgId, Long userId, String msg) {
        Utilizator from = userRepo.findOne(userId);
        Message message = messagesRepo.findOne(msgId);
        List<Utilizator> to = new ArrayList<>();
        to.add(message.getFrom());
        for (Utilizator curent : message.getTo()) {
            if (curent.getId() != userId) {
                to.add(curent);
            }
        }
        Message newReply = new Message(from, to, msg);
        newReply.setReplyMsg(message);
        newReply.setData(LocalDateTime.now());
        try {
            messagesRepo.save(newReply);
        } catch (ValidationException e) {
            System.out.println(e);
        }
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
