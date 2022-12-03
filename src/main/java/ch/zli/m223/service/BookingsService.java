package ch.zli.m223.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import ch.zli.m223.domain.entity.Booking;
import ch.zli.m223.domain.entity.State;
import ch.zli.m223.domain.exception.ConflictException;

@ApplicationScoped
public class BookingsService {
    @Inject
    private EntityManager entityManager;

    @Transactional
    public Booking createBooking(Booking booking) throws ConflictException {
        Optional<Booking> optional = entityManager
                .createQuery("SELECT b FROM Booking b WHERE b.date = :date AND b.state = :state", Booking.class)
                .setParameter("date", booking.getDate())
                .setParameter("state", booking.getState())
                .getResultStream()
                .findFirst();
        if (!optional.isPresent()) {
            booking.setState(State.PENDING);
            entityManager.persist(booking);
            return booking;
        } else {
            booking.setState(State.DENIED);
            entityManager.persist(booking);
            throw new ConflictException(
                    "Booking collides with date from another booking. Your booking has automatically been denied.");
        }
    }

    @Transactional
    public void deleteBooking(Long id) {
        entityManager.remove(entityManager.find(Booking.class, id));
    }

    public List<Booking> findAll() {
        var query = entityManager.createQuery("FROM Booking", Booking.class);
        return query.getResultList();
    }

    public Booking find(long id) {
        return entityManager.find(Booking.class, id);
    }

    @Transactional
    public Booking update(Booking user) {
        return entityManager.merge(user);
    }
}
