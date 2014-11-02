package IRBS;

import IRBS.*;
import junit.framework.TestCase;
import java.util.Calendar;

public class BookingTest extends TestCase {
	Booking booking;
	Room room;
	User user;
	Timeslot timeslot;
	public BookingTest() {
		user = new User("abc001", "John", "Chan");
		room = new Room("PQ601", "Computer Lab", "PQ Floor6");
		timeslot = new Timeslot(room, Calendar.getInstance());
		booking = new Booking("1", user, timeslot);
	}
	public void testGetBookingID() {
		assertEquals(booking.getBookingID(), "1");
	}
	public void testBookingUser() {
		assertEquals(booking.getBookingUser(), user);
	}
	public void testGetTimeslot() {
		assertEquals(booking.getTimeslot(), timeslot);
	}
}