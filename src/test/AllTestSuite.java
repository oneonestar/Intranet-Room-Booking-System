package IRBS;

import IRBS.*;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTestSuite extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("All JUnit Tests");
		suite.addTestSuite(UserTest.class);
		suite.addTestSuite(RoomTest.class);
		suite.addTestSuite(TimeslotTest.class);
		suite.addTestSuite(BookingTest.class);
		suite.addTestSuite(BookingControllerTest.class);
		return suite;
	}
}
