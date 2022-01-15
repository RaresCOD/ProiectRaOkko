package ro.ubbcluj.map.proiectraokko4.service;

import javafx.application.Platform;
import ro.ubbcluj.map.proiectraokko4.utils.observer.Observable;
import ro.ubbcluj.map.proiectraokko4.utils.observer.Observer;
import ro.ubbcluj.map.proiectraokko4.utils.observer.TypeOfObservation;

import java.util.ArrayList;
import java.util.List;

public class RefreshThreadService extends Thread implements Observable {

    public void run() {
        while(true) {
            try {
                System.out.println("Am dat refresh");
                sleep(3000);
                notifyObservers();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        observers.stream().forEach(x->Platform.runLater(() -> x.update(TypeOfObservation.REFRESH)));
    }
}
