package IRBS;

import java.util.*;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import java.security.MessageDigest;

/**
 * The main controller to handle all the booking and querying.
 * This class is a singleton class.
 */
public class BookingController {
	private static BookingController instance = null;
	private List<Booking> bookingRecords;
	private List<Room> roomRecords;
	private List<User> userRecords;
	private Map<User, String> hashedPasswordRecords;

	/**
	 * This function provide the access to the singleton instance.
	 * @return the singleton instance of the BookingController.
	 */
	public static BookingController getInstance() {
		if (instance  == null) {
			instance = new BookingController();
		}
		return instance;
	}

	/**
	 * Constructor of the BookingController. Responsible for the initialization
	 * of the private members and load the data into the program.
	 */
	private BookingController() {
		roomRecords = new ArrayList<Room>();
		userRecords = new ArrayList<User>();
		bookingRecords = new ArrayList<Booking>();
		hashedPasswordRecords = new HashMap<User, String>();
		//The order of loading is important. There are dependency between the data.
		loadRoomData(Settings.getInstance().roomRecordFile);
		loadUserData(Settings.getInstance().userRecordFile);
		loadBookingData(Settings.getInstance().bookingRecordFile);
	}

	/**
	* Add a new booking into the database. Do not provide checking on data integrity.
	* @param user user who is making this booking.
	* @param timeslot the room and the timeslot to be booked.
	* @return true if nothing the adding is successful.
	*/
	public boolean addBooking(User user, Timeslot timeslot) {
		if (user == null || timeslot == null)
			return false;
		if ( queryCurrentBookings(user).size() >= Settings.getInstance().maxBooking)
			return false;
		String bookingID = Integer.toString(bookingRecords.size());
		Booking newBooking = new Booking(bookingID, user, timeslot);
		bookingRecords.add(newBooking);
		return true;
	}


	/**
	 * Authenticate the login request by comparing the staffID and password
	 * and the entities in the database.
	 * @param staffID the staffID should be unique.
	 * @param password the password that matches the staffID.
	 * @return true if the entity exist in database, otherwise return false.
	 */
	public User authenticate(String staffID, String password) {
		StringBuffer sb = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes("UTF-8"));
			byte[] mdbytes = md.digest();

			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		String hashedPassword = sb.toString();
		for (User user : userRecords) {
			if (user.getStaffID().equals(staffID) && hashedPasswordRecords.get(user).equals(hashedPassword))
				return user;
		}
		return null;
	}

	/**
	* Return all the current booking records of the user.
	* @param roomType a string that specified the room type. Should
	*        be match a value returned by queryRoomTypes().
	* @param date search the timeslots in which date.
	* @return a list of slots which contains all the available timeslots in that date.
	*/
	public List<Timeslot> queryAvailableTimeslot(String roomType, Calendar date) {
		List<Timeslot> availableSlot = new ArrayList<Timeslot>();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		Calendar start = (Calendar)date.clone();
		Calendar end = (Calendar)date.clone();

		start.set(Calendar.HOUR_OF_DAY, Settings.getInstance().dayStartTime.get(Calendar.HOUR_OF_DAY));
		end.set(Calendar.HOUR_OF_DAY, Settings.getInstance().dayEndTime.get(Calendar.HOUR_OF_DAY));

		//generate all timeslot for the rooms
		for (Room room : roomRecords) {
			if (room.getRoomType().equals(roomType)) {
				Calendar tempCal = (Calendar)start.clone();
				while (tempCal.compareTo(end) <= 0) {
					if (tempCal.compareTo(Calendar.getInstance()) >= 0) {
						availableSlot.add(new Timeslot(room, tempCal));
						tempCal = (Calendar)tempCal.clone();
					}
					tempCal.add(Calendar.HOUR_OF_DAY, 1);
				}
			}
		}
		//remove the booked timeslot
		for (Iterator<Timeslot> iterator = availableSlot.iterator(); iterator.hasNext(); ) {
			Timeslot timeslot = iterator.next();
			for (Booking booking : bookingRecords) {
				if (booking.getTimeslot().compareTo(timeslot) == 0) {
					iterator.remove();
					break;
				}
			}
		}
		return availableSlot;
	}

	/**
	 * Return all the current booking records of the user.
	 * @param user which user booked the room.
	 * @return a list of slots which contains the Room id, timeslot, etc.
	 */
	public List<Booking> queryCurrentBookings(User user) {
		List<Booking> currentBookings = new ArrayList<Booking>();
		for (Booking booking : bookingRecords) {
			if (booking.getBookingUser().equals(user) && booking.getTimeslot().getDatetime().compareTo(Calendar.getInstance()) >= 0)
				currentBookings.add(booking);
		}
		return currentBookings;
	}
	/**
		 * Return all the type of room that that exist in the room list.
		 * @return a set of room types (eg. "Computer Labs", "Classroom").
		 */
	public Set<String> queryRoomTypes() {
		Set<String> roomSet = new HashSet<String>();
		for (Room room : roomRecords) {
			roomSet.add(room.getRoomType());
		}
		return roomSet;
	}
	/**
	* Load the room data from the file.
	* Schema: String roomNumber (primary key), String roomType, String roomLocation
	* @param filename the txt file which stored the data.
	* @return true if the loading is successful.
	*/
	private boolean loadRoomData(String filename) {
		Path path = Paths.get("", filename);
		Charset charset = Charset.forName("ISO-8859-1");
		try {
			List<String> lines = Files.readAllLines(path, charset);
			for (String line : lines) {
				String[] tokens = line.split(";");
				roomRecords.add(new Room(tokens[0], tokens[1], tokens[2]));
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		Collections.sort(roomRecords);
		return true;
	}
	/**
	 * Load the User data from the file.
	 * Schema: String userID (primary key), String firstName, String lastName, String password
	 * @param filename the txt file which stored the data.
	 * @return true if the loading is successful.
	 */
	private boolean loadUserData(String filename) {
		Path path = Paths.get("", filename);
		Charset charset = Charset.forName("ISO-8859-1");
		try {
			List<String> lines = Files.readAllLines(path, charset);

			for (String line : lines) {
				String[] tokens = line.split(";");
				String staffID = tokens[0];
				String firstName = tokens[1];
				String lastName = tokens[2];
				String hashedPassword = tokens[3];
				User user = new User(staffID, firstName, lastName);
				userRecords.add(user);
				hashedPasswordRecords.put(user, hashedPassword);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		Collections.sort(userRecords);
		return true;
	}
	/**
	 * Load the booking data from the file.
	 * Schema: String bookingID (primary key), String userID, String RoomID,
	 *         Date Timeslot
	 * @param filename the txt file which stored the data.
	 * @return true if the loading is successful.
	 */
	private boolean loadBookingData(String filename) {
		Path path = Paths.get("", filename);
		Charset charset = Charset.forName("ISO-8859-1");
		try {
			List<String> lines = Files.readAllLines(path, charset);

			for (String line : lines) {
				String[] tokens = line.split(";");

				String bookingID = tokens[0];
				String staffID = tokens[1];
				String roomNumber = tokens[2];
				String timeString = tokens[3];

				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", new  Locale("en", "US"));
				try {
					Date date = sdf.parse(timeString);
					calendar.setTime(date);
				} catch (Exception e) {
					System.out.println(e);
				}
				Room room = searchRoom(roomNumber);
				User user = searchUser(staffID);
				if (room == null || user == null) {
					System.out.print("Database corrupted!");
					return false;
				}
				Timeslot timeslot = new Timeslot(room, calendar);
				bookingRecords.add(new Booking(bookingID, user, timeslot));
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		return true;
	}

	private Room searchRoom(String roomNumber) {
		Room key = new Room(roomNumber, "", "");
		int resultPos = Collections.binarySearch(roomRecords, key);
		return resultPos<0 ? null : roomRecords.get(resultPos);
	}

	private User searchUser(String staffID) {
		User key = new User(staffID, "", "");
		int resultPos = Collections.binarySearch(userRecords, key);
		return resultPos<0 ? null : userRecords.get(resultPos);
	}

}

/**
 * Contain all the global settings (eg. the maximum booking
 * count for each individual...).
 * This class is a singleton class.
 */
class Settings {
	private static Settings instance = null;
	/**
	 * This function provide the access to the singleton object.
	 * @return the singleton object of the BookingController.
	 */
	public static synchronized Settings getInstance() {
		if (instance  == null) {
			instance = new Settings();
		}
		return instance;
	}

	public final int maxBooking = 5;
	public final String roomRecordFile = "room.txt";
	public final String userRecordFile = "user.txt";
	public final String bookingRecordFile = "booking.txt";
	public final Calendar dayStartTime;
	public final Calendar dayEndTime;

	/**
	 * Init the complex data type.
	 */
	private Settings() {
		//0800
		dayStartTime = Calendar.getInstance();
		dayStartTime.set(Calendar.HOUR_OF_DAY, 8);
		dayStartTime.set(Calendar.MINUTE, 0);
		dayStartTime.set(Calendar.SECOND, 0);
		dayStartTime.set(Calendar.MILLISECOND, 0);

		//1700
		dayEndTime = Calendar.getInstance();
		dayEndTime.set(Calendar.HOUR_OF_DAY, 17);
		dayEndTime.set(Calendar.MINUTE, 0);
		dayEndTime.set(Calendar.SECOND, 0);
		dayEndTime.set(Calendar.MILLISECOND, 0);
	}
}