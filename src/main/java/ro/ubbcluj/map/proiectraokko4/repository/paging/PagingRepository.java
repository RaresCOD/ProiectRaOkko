package ro.ubbcluj.map.proiectraokko4.repository.paging;


import ro.ubbcluj.map.proiectraokko4.domain.Entity;
import ro.ubbcluj.map.proiectraokko4.repository.Repository;

public interface PagingRepository<ID ,
        E extends Entity<ID>>
        extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);   // Pageable e un fel de paginator
}
