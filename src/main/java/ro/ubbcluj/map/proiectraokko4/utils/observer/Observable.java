package ro.ubbcluj.map.proiectraokko4.utils.observer;

import ro.ubbcluj.map.proiectraokko4.utils.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
