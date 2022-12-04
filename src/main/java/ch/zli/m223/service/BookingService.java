package ch.zli.m223.service;

import java.util.List;

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
public class BookingService {
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
        List<Booking> bookings = entityManager
                .createQuery(
                        "SELECT b FROM Booking b WHERE b.date = :date",
                        Booking.class)
                .setParameter("date", booking.getDate())
                .getResultStream()
                .filter(x -> x.getBookingDuration() == booking.getBookingDuration()
                        || x.getBookingDuration() == BookingDuration.FULLDAY
                        || booking.getBookingDuration() == BookingDuration.FULLDAY)
                .toList();
        if (bookings.size() == 0 || (bookings.size() == 1 && bookings.get(0).getId() == booking.getId())) {
            booking.setState(State.PENDING);
        } else {
            booking.setState(State.DENIED);
            throw new ConflictException(
                    "Booking collides with date from another booking. Your booking has automatically been denied.");
        }
    }
}