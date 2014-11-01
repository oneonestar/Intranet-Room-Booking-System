import java.util.Calendar;
/**
 *
 */
class Timeslot implements Comparable<Timeslot> {
	private Room room;
	private Calendar datetime;

	/**
	 * Compare the room and then datetime. If the room is
	 * the same then compare the datetime.
	 * @return 0 if both the room and datetime are equal
	 */
	@Override
	public int compareTo(Timeslot other) {
		int compareRoom = room.compareTo(other.room);
		if (compareRoom != 0 )
			return compareRoom;
		return datetime.compareTo(other.datetime);
	}

	/**
	 * Only class in the same package can create an instance
	 */
	private Timeslot() {}

	/**
	 * The constructor is package access only to maintain data integrity.
	 */
	Timeslot(Room room, Calendar datetime) {
		this.room = room;
		this.datetime = datetime;
	}
	/**
	 * This function is responsible for returning the room to the caller.
	 * @return the corresponding room in the timeslot record. 
	 */
	public Room getRoom() {
		return room;
	}
	/**
	 * This function is responsible for returning the datetime to the caller.
	 * The return 
	 * @return the datetime room in the timeslot record.
	 */
	public Calendar getDatetime() {
		return (Calendar)datetime.clone();
	}
}
