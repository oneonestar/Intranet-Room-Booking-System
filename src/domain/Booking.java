public class Booking {
	private String bookingID;
	private User user;
	private Timeslot timeslot;

	private Booking() {}
	Booking(String bookingID, User user, Timeslot timeslot) {
		this.user = user;
		this.timeslot = timeslot;
		this.bookingID = bookingID;
	}
	public String getBookingID() {
		return bookingID;
	}
	public User getBookingUser() {
		return user;
	}
	public Timeslot getTimeslot() {
		return timeslot;
	}
	public boolean compareTimeslot(Timeslot timeslot) {
		int compareRoom = this.timeslot.getRoom().compareTo(timeslot.getRoom());
		int compareTime = this.timeslot.getDatetime().compareTo(timeslot.getDatetime());
		return compareRoom == 0 && compareTime == 0;
	}
}