package ch.zli.m223.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void merge(Booking booking) {
        entityManager.merge(booking);
    }

    @Transactional
    public Booking update(Booking booking) throws ConflictException, BadRequestException {
        checkDate(booking);
        checkState(booking.getId());
        return entityManager.merge(booking);
    }

    private void checkState(Long id) {
        Booking booking = find(id);
        if (booking != null && State.CANCELED.equals(booking.getState())) {
            throw new BadRequestException("Booking has already been canceled");
        }
    }

    private void checkDate(Booking booking) throws ConflictException {
        List<Booking> bookings = entityManager
                .createQuery(
                        "SELECT b FROM Booking b WHERE b.date = :date AND b.state != :state",
                        Booking.class)
                .setParameter("date", booking.getDate())
                .setParameter("state", State.CANCELED)
                .getResultStream()
                .toList();

        if (booking.getBookingDuration().equals(BookingDuration.FULLDAY) && bookings.size() != 0) {
            booking.setState(State.DENIED);
            throw new ConflictException(
                    "Booking collides with date from another booking. Your booking has automatically been denied.");
        }

        List<Booking> collidingBookings = bookings.stream()
                .filter(x -> x.getBookingDuration() == booking.getBookingDuration()
                        || x.getBookingDuration() == BookingDuration.FULLDAY
                        || x.getId() == booking.getId())
                .toList();
        if (collidingBookings.size() == 0) {
            booking.setState(State.PENDING);
        } else {
            booking.setState(State.DENIED);
            throw new ConflictException(
                    "Booking collides with date from another booking. Your booking has automatically been denied.");
        }
    }

    public Map<LocalDate, BookingDuration> getAvailableDates() {
        Map<LocalDate, BookingDuration> dates = new HashMap<>();
        for (int i = 0; i < 30; i++) {
            dates.put(LocalDate.now().plusDays(i), BookingDuration.FULLDAY);
        }

        List<LocalDate> alreadyClearedDates = new ArrayList<>();

        List<Booking> bookings = entityManager
                .createQuery(
                        "SELECT b FROM Booking b WHERE b.date <= :date1 AND b.date >= :date2 AND b.state != :state",
                        Booking.class)
                .setParameter("date1", LocalDate.now().plusDays(30))
                .setParameter("date2", LocalDate.now())
                .setParameter("state", State.CANCELED)
                .getResultStream()
                .toList();

        bookings.forEach(x -> {
            BookingDuration duration = dates.get(x.getDate());
            if (!alreadyClearedDates.contains(x.getDate())) {
                swapDurationOrRemove(duration, x, dates);
                alreadyClearedDates.add(x.getDate());
            } else {
                dates.remove(x.getDate());
            }
        });

        return dates;
    }

    private void swapDurationOrRemove(BookingDuration durationA, Booking booking,
            Map<LocalDate, BookingDuration> dates) {
        switch (booking.getBookingDuration()) {
            case MORNING -> durationA = BookingDuration.NOON;
            case NOON -> durationA = BookingDuration.MORNING;
            case FULLDAY -> dates.remove(booking.getDate());
        }
    }

    public void setState(Long id, State state) {
        Booking booking = find(id);
        if (booking != null) {
            if (booking.getState().equals(State.CANCELED)) {
                throw new BadRequestException("Booking has already been canceled");
            }
            booking.setState(state);
            merge(booking);
        } else {
            throw new BadRequestException("No booking with this id");
        }
    }
}