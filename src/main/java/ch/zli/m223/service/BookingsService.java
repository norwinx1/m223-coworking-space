package ch.zli.m223.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;

import ch.zli.m223.domain.entity.Booking;
import ch.zli.m223.domain.entity.BookingDuration;
import ch.zli.m223.domain.entity.State;
import ch.zli.m223.domain.exception.ConflictException;

@ApplicationScoped
public class BookingsService {
    @Inject
    private EntityManager entityManager;

    @Transactional
    public Booking createBooking(Booking booking) throws ConflictException {
        checkDate(booking);
        entityManager.persist(booking);
        return booking;
    }

    @Transactional
    public void deleteBooking(Long id) {
        entityManager
                .createQuery("DELETE FROM Booking b WHERE b.id = :id")
                .setParameter("id", id)
                .executeUpdate();

    }

    public List<Booking> findAll() {
        var query = entityManager.createQuery("FROM Booking", Booking.class);
        return query.getResultList();
    }

    public Booking find(long id) {
        return entityManager.find(Booking.class, id);
    }

    @Transactional
    public Booking update(Booking booking) throws ConflictException, BadRequestException {
        checkDate(booking);
        checkState(booking);
        return entityManager.merge(booking);
    }

    private void checkState(Booking booking) {
        if (State.CANCELED.equals(booking.getState())) {
            throw new BadRequestException("Booking has already been canceled");
        }
    }

    private void checkDate(Booking booking) throws ConflictException {
        Optional<Booking> optional = entityManager
                .createQuery(
                        "SELECT b FROM Booking b WHERE b.date = :date AND (b.bookingDuration = :bookingDuration1 OR b.bookingDuration = :bookingDuration2)",
                        Booking.class)
                .setParameter("date", booking.getDate())
                .setParameter("bookingDuration1", booking.getBookingDuration())
                .setParameter("bookingDuration2", BookingDuration.FULLDAY)
                .getResultStream()
                .findFirst();
        if (!optional.isPresent()) {
            booking.setState(State.PENDING);
        } else if (optional.get().getId() != booking.getId()) {
            booking.setState(State.DENIED);
            throw new ConflictException(
                    "Booking collides with date from another booking. Your booking has automatically been denied.");
        }
    }
}
