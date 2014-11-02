package IRBS;

import junit.framework.TestCase;

public class UserTest extends TestCase {
	User user;
	public UserTest() {
		user = new User("StaffID001", "John", "Chan");
	}
	public void testGetStaffID() {
		assertEquals(user.getStaffID(), "StaffID001");
	}
	public void testGetFirstName() {
		assertEquals(user.getFirstName(), "John");
	}
	public void testGetLastName() {
		assertEquals(user.getLastName(), "Chan");
	}
	public void testCompareTo() {
		assertEquals(user.compareTo(user), 0);
	}
}