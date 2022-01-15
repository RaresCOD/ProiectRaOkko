package ro.ubbcluj.map.proiectraokko4.service;

import ro.ubbcluj.map.proiectraokko4.Conexitate.DFS;
import ro.ubbcluj.map.proiectraokko4.domain.*;
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
import java.util.Arrays;
import java.util.List;

public class EventService implements Observable {

    PagingRepository<Long, Utilizator> userRepo;
    Repository<Long, Event> eventRepo;

    /**
     *
     * @param userRepo user repo
     * @param eventRepo event repo
     */
    public EventService(PagingRepository<Long, Utilizator> userRepo, Repository<Long, Event> eventRepo) {
        this.userRepo = userRepo;
        this.eventRepo = eventRepo;
    }

    public void addEvent(String name, String description, String location, LocalDateTime date)
    {
        Event event = new Event(name, description, location, date, "");
        eventRepo.save(event);
        notifyObservers();
    }

    public void addEventParticipant(Long id, Long idParticipant)
    {
        Event oldEvent = eventRepo.findOne(id);
        String participants = "";
        if(oldEvent.getParticipants() == null) participants = idParticipant.toString();
        else if(oldEvent.getParticipants() != null) participants = oldEvent.getParticipants() + ";" + idParticipant.toString();
        Event newEvent = new Event(oldEvent.getName(), oldEvent.getDescription(), oldEvent.getLocation(), oldEvent.getDate(), participants);
        newEvent.setId(oldEvent.getId());
        eventRepo.update(newEvent);
        notifyObservers();
    }

    public void removeEventParticipant(Long id, Long idParticipant)
    {
        Event oldEvent = eventRepo.findOne(id);
        List<String> oldParticipants = Arrays.stream(oldEvent.getParticipants().split(";")).toList();
        List<String> newParticipants = oldParticipants.stream().filter(x -> !x.equals(idParticipant.toString())).toList();
        String newParticipantsString = "";
        if(newParticipants.size() == 0) newParticipantsString = "";
        else {
            newParticipantsString = newParticipants.get(0);
            for (String participant : newParticipants.subList(1, newParticipants.size()))
                newParticipantsString = newParticipantsString + ";" + participant;
        }
        System.out.println("Noii participanti sunt " + newParticipantsString);
        Event newEvent = new Event(oldEvent.getName(), oldEvent.getDescription(), oldEvent.getLocation(), oldEvent.getDate(), newParticipantsString);
        newEvent.setId(oldEvent.getId());
        eventRepo.update(newEvent);
        notifyObservers();
    }

    public boolean isUserParticipant(Long id, Long idParticipant) {
        boolean gasit = false;
        Event event = eventRepo.findOne(id);
        if (event.getParticipants() == null) return false;
        List<String> participants = Arrays.stream(event.getParticipants().split(";")).toList();
        if (participants.contains(idParticipant.toString())) gasit = true;

        return gasit;
    }

    public List<Event> getEventsForUser(Long idParticipant)
    {
        List<Event> rez = new ArrayList<>();
        List<Event> events = eventRepo.findAll();
        for(Event event : events)
        {
            if(event.getParticipants() == null) continue;
            List<String> participants = Arrays.stream(event.getParticipants().split(";")).toList();
            if(participants.contains(idParticipant.toString())) rez.add(event);
        }
        return rez;
    }



    public void deleteEvent(Long id)
    {
        eventRepo.delete(id);
        notifyObservers();
    }

    public List<Event> getAllEvents()
    {
        return eventRepo.findAll();
    }

    public Event getOne(Long id)
    {
        return eventRepo.findOne(id);
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
