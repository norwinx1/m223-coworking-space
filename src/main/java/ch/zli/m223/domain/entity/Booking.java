package ch.zli.m223.domain.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(readOnly = true)
    private Long id;

    @Column(nullable = false)
    private State state;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private BookingDuration bookingDuration;

    @ManyToOne(optional = false)
    @Fetch(FetchMode.JOIN)
    private ApplicationUser applicationUser;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the state
     */
    public State getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @return the bookingDuration
     */
    public BookingDuration getBookingDuration() {
        return bookingDuration;
    }

    /**
     * @param bookingDuration the bookingDuration to set
     */
    public void setBookingDuration(BookingDuration bookingDuration) {
        this.bookingDuration = bookingDuration;
    }

    /**
     * @return the applicationUser
     */
    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }

    /**
     * @param applicationUser the applicationUser to set
     */
    public void setApplicationUser(ApplicationUser applicationUser) {
        this.applicationUser = applicationUser;
    }

    
}
