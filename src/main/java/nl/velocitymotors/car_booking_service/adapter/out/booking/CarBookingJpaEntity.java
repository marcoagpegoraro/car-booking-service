package nl.velocitymotors.car_booking_service.adapter.out.booking;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity(name = "car_booking")
@Getter
@Setter
public class CarBookingJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String vehicleID;
    private OffsetDateTime rentalStartDate;
    private OffsetDateTime rentalEndDate;
    private String customerName;
    private String vehicleCategory;
    private String paymentMode;
    private String paymentReference;
    private String bookingStatus;
    @CreationTimestamp
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}