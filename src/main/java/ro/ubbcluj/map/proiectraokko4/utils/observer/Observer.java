package ro.ubbcluj.map.proiectraokko4.utils.observer;


import ro.ubbcluj.map.proiectraokko4.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}