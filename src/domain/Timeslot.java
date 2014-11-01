import java.util.Calendar;
/**
 *
 */
class Timeslot {
	private Room room;
	private Calendar datetime;
	private Timeslot() {}
	Timeslot(Room room, Calendar datetime) {
		this.room = room;
		this.datetime = datetime;
	}
	public Room getRoom() {
		return room;
	}
	public Calendar getDatetime() {
		return (Calendar)datetime.clone();
	}
}
