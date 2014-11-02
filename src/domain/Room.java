package IRBS;

public class Room implements Comparable<Room> {
	private String roomNumber;
	private String roomType;
	private String roomLocation;

	/**
	 * Comparing the room number of two room objects.
	 * Room number should be unique for every room.
	 * @return 0 if both the room number are equal
	 */
	@Override
	public int compareTo(Room other) {
		return this.roomNumber.compareTo(other.roomNumber);
	}

	/**
	 * Only class in the same package can create an instance
	 */
	private Room() {}

	Room(String roomNumber, String roomType, String roomLocation) {
		this.roomNumber = roomNumber;
		this.roomType = roomType;
		this.roomLocation = roomLocation;
	}
	/**
	 * This function is responsible for returning the room number to the caller.
	 * @return the room number associate to the record
	 */
	public String getRoomNumber() {
		return roomNumber;
	}
	/**
	 * This function is responsible for returning the room type to the caller.
	 * @return the type of the room
	 */
	public String getRoomType() {
		return roomType;
	}
	/**
	 * This function is responsible for returning the location of the room to the caller.
	 * @return the location of the room
	 */
	public String getRoomLocation() {
		return roomLocation;
	}
}