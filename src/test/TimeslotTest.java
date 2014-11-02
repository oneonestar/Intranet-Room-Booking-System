package IRBS;

import junit.framework.TestCase;
import java.util.Calendar;

public class TimeslotTest extends TestCase {
	Timeslot timeslot;
	Room room;
	Calendar date;
	public TimeslotTest() {
		room = new Room("PQ601", "Computer Lab", "PQ Floor6");
		date = Calendar.getInstance();
		timeslot = new Timeslot(room, date);
	}
	public void testGetRoom() {
		assertEquals(timeslot.getRoom(), room);
	}
	public void testGetDatetime() {
		assertEquals(timeslot.getDatetime(), date);
	}
	public void testCompareTo() {
		assertEquals(timeslot.compareTo(timeslot), 0);
	}
}