CREATE SEQUENCE IF NOT EXISTS booking_reference_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS car_booking (
                                           id                  varchar(255)    NOT NULL,
    vehicle_id          varchar(255),
    customer_name       varchar(255),
    vehicle_category    varchar(255),
    payment_mode        varchar(255),
    payment_reference   varchar(255),
    booking_status      varchar(255),
    rental_start_date   timestamp(6) with time zone,
                                         rental_end_date     timestamp(6) with time zone,
                                         created_at          timestamp(6) with time zone  NOT NULL,
                                         updated_at          timestamp(6) with time zone  NOT NULL,
                                         PRIMARY KEY (id)
    );