package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingTest {
    @Test
    void bookingEquals() {
        Booking booking = new Booking();
        Booking booking1 = new Booking();

        assertThat(booking.getId(), equalTo(booking1.getId()));
        assertThat(booking.hashCode(), equalTo(booking1.hashCode()));
    }
}
