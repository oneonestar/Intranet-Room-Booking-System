public class Room implements Comparable<Room> {
	private String roomNumber;
	private String roomType;
	private String roomLocation;

	@Override
	public int compareTo(Room other) {
		return this.roomNumber.compareTo(other.roomNumber);
	}

	public Room(String roomNumber, String roomType, String roomLocation) {
		this.roomNumber = roomNumber;
		this.roomType = roomType;
		this.roomLocation = roomLocation;
	}
	public String getRoomNumber() {
		return roomNumber;
	}
	public String getRoomType() {
		return roomType;
	}
	public String getRoomLocation() {
		return roomLocation;
	}
}