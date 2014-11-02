package IRBS;

public class Booking {
	private String bookingID;
	private User user;
	private Timeslot timeslot;

	/**
	 * Only class in the same package can create an instance
	 */
	private Booking() {}

	/**
	 * The creator should provide a unique bookingID for each booking.
	 */
	Booking(String bookingID, User user, Timeslot timeslot) {
		this.user = user;
		this.timeslot = timeslot;
		this.bookingID = bookingID;
	}
	/**
	 * This function is responsible for returning the BookingID to the caller.
	 * @return the booking ID associate to the record
	 */
	public String getBookingID() {
		return bookingID;
	}
	/**
	 * This function is responsible for returning the booking user to the caller.
	 * @return the user associate to the record
	 */
	public User getBookingUser() {
		return user;
	}
	/**
	 * This function is responsible for returning the booked timeslot to the caller.
	 * @return the timeslot associate to the record
	 */
	public Timeslot getTimeslot() {
		return timeslot;
	}
}