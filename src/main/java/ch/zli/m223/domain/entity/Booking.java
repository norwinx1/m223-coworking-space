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
}
