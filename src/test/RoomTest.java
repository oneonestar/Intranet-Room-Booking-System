package IRBS;

import junit.framework.TestCase;

public class RoomTest extends TestCase {
	Room room;
	public RoomTest() {
		room = new Room("PQ601", "Computer Lab", "PQ Floor6");
	}
	public void testGetRoomNumber() {
		assertEquals(room.getRoomNumber(), "PQ601");
	}
	public void testGetRoomType() {
		assertEquals(room.getRoomType(), "Computer Lab");
	}
	public void testgetRoomLocation() {
		assertEquals(room.getRoomLocation(), "PQ Floor6");
	}
	public void testCompareTo() {
		Room room2 = new Room("PQ601", "Computer Lab", "PQ Floor6");
		Room room3 = new Room("PQ602", "Computer Lab", "PQ Floor6");
		assertEquals(room.compareTo(room2), 0);
		assertTrue(room.compareTo(room3) != 0);

	}
}