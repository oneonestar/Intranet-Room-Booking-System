package IRBS;

import junit.framework.TestCase;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class BookingControllerTest extends TestCase {
	public void testGetInstance() {
		Object obj = BookingController.getInstance();
		assertNotNull(obj);
		assertTrue(obj instanceof BookingController);
	}
	public void testAuthenticate() {
		User user = BookingController.getInstance().authenticate("abc001", "cba001");
		assertNotNull(user);
		assertEquals("abc001", user.getStaffID());
	}
	public void testqueryRoomTypes() {
		Set<String> roomTypes = BookingController.getInstance().queryRoomTypes();
		assertNotNull(roomTypes);
		assertTrue(roomTypes.size()>0);
	}
	public void testQueueAvailableTimeslot() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		List<Timeslot> timeslots = BookingController.getInstance().queryAvailableTimeslot("Computer labs", calendar);
		assertNotNull(timeslots);
		//assume there are available timeslots
		assertTrue(timeslots.size()>0);
	}
	public void testaddBooking() {
		User user = new User("abc002", "JUnit", "TestCase");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		List<Timeslot> available_timeslots = BookingController.getInstance().queryAvailableTimeslot("Computer labs", calendar);

		//if the adding is successful, the available timeslots should be decrease by 1
		List<Timeslot> pre_timeslots = BookingController.getInstance().queryAvailableTimeslot("Computer labs", calendar);
		assertTrue(BookingController.getInstance().addBooking(user, available_timeslots.get(0)));
		List<Timeslot> post_timeslots = BookingController.getInstance().queryAvailableTimeslot("Computer labs", calendar);
		assertEquals(1, pre_timeslots.size() - post_timeslots.size());
	}
	public void testQueueCurrentBookings() {
		User user = new User("abc003", "JUnit", "TestCase2");

		//should be empty because the user is new
		List<Booking> bookings = BookingController.getInstance().queryCurrentBookings(user);
		assertEquals(0, bookings.size());

		//add one booking record to the user
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		List<Timeslot> available_timeslots = BookingController.getInstance().queryAvailableTimeslot("Computer labs", calendar);
		BookingController.getInstance().addBooking(user, available_timeslots.get(0));
		bookings = BookingController.getInstance().queryCurrentBookings(user);
		assertEquals(1, bookings.size());
	}
}