package ro.ubbcluj.map.proiectraokko4.repository.paging;


import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Paginator<E> {
    private Pageable pageable;
    private Iterable<E> elements;

    public Paginator(Pageable pageable, Iterable<E> elements) {
        this.pageable = pageable;
        this.elements = elements;
    }

    public Page<E> paginate() {
        return new PageImplementation<>(pageable, StreamSupport.stream(elements.spliterator(), false));
    }
}
