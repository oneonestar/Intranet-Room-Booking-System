package domain;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class BookingSlot {
	public String bookingID;
	public User user;
	public Calendar time;
	public Room room;
	public BookingSlot(Room room, Calendar time) {
		this.room = room;
		this.time = time;
	}
	public BookingSlot(String bookingID, User user, Calendar time, Room room) {
		this.bookingID = bookingID;
		this.user = user;
		this.time = time;
		this.room = room;
	}
}

class User {
	String username;
	String firstName;
	String lastName;
	String password;
	public User(String username, String firstName, String lastName, String password) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
	}
}

class Room {

	String roomID;
	String roomType;
	String roomLocation;
	public Room(String roomID, String roomType, String roomLocation) {
		this.roomID = roomID;
		this.roomType = roomType;
		this.roomLocation = roomLocation;
	}
}

class Settings {
	private static Settings settings = null;

	public static synchronized Settings getInstance() {
		if (settings  == null) {
			settings = new Settings();
		}
		return settings;
	}

	public final Calendar dayStartTime;
	public final Calendar dayEndTime;
	public final int maxBooking;
	public Settings() {
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

		maxBooking = 5;
	}
}
/**
	 * Load the room data from the database file.
	 * Schema: String roomID (primary key), String roomType, String roomLocation
	 */
public class BookingController {
	private static BookingController settings = null;
	public List<BookingSlot> bookingSlots;

	public static synchronized BookingController getInstance() {
		if (settings  == null) {
			settings = new BookingController();
		}
		return settings;
	}

	private List<Room> rooms;
	private List<User> users;
	private int bookingCount;
	public BookingController() {
		rooms = new ArrayList<Room>();
		users = new ArrayList<User>();
		bookingSlots = new ArrayList<BookingSlot>();
	}
	/**
	 * Load the room data from the database file.
	 * Schema: String roomID (primary key), String roomType, String roomLocation
	 * @param filename the txt file which stored the data.
	 * @return true if the loading is successful.
	 */
	public boolean loadRoomData(String filename) {
		Path path = Paths.get("", filename);
		Charset charset = Charset.forName("ISO-8859-1");
		try {
			List<String> lines = Files.readAllLines(path, charset);

			for (String line : lines) {
				StringTokenizer st = new StringTokenizer(line, ";");
				rooms.add(new Room(st.nextToken(), st.nextToken(), st.nextToken()));
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		return true;
	}
	/**
	 * Load the User data from the database file.
	 * Schema: String userID (primary key), String firstName, String lastName, String password
	 * @param filename the txt file which stored the data.
	 * @return true if the loading is successful.
	 */
	public boolean loadUserData(String filename) {
		Path path = Paths.get("", filename);
		Charset charset = Charset.forName("ISO-8859-1");
		try {
			List<String> lines = Files.readAllLines(path, charset);

			for (String line : lines) {
				StringTokenizer st = new StringTokenizer(line, ";");
				users.add(new User(st.nextToken(), st.nextToken(), st.nextToken(), st.nextToken()));
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		return true;
	}
	/**
	 * Load the booking data from the database file.
	 * Schema: String bookingID (primary key), String userID, String RoomID,
	 *         Date Timeslot
	 * @param filename the txt file which stored the data.
	 * @return true if the loading is successful.
	 */
	public boolean loadBookingData(String filename) {
		Path path = Paths.get("", filename);
		Charset charset = Charset.forName("ISO-8859-1");
		try {
			List<String> lines = Files.readAllLines(path, charset);

			for (String line : lines) {
				StringTokenizer st = new StringTokenizer(line, ";");

				String bookingID = st.nextToken();
				String userID = st.nextToken();
				String room = st.nextToken();
				String time = st.nextToken();



				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", new  Locale("en", "US"));
				try {
					Date date = sdf.parse(time);
					cal.setTime(date);// all done
					cal.set(Calendar.MILLISECOND, 0);
				} catch (Exception e) {
				}
				Room r = searchRoom(room);
				User u = searchUser(userID);
				if (r == null || u == null) {
					System.out.print("Database corrupted!");
					return false;
				}
				bookingSlots.add(new BookingSlot(bookingID, u, cal, r));

				bookingCount++;
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		return true;
	}
	/**
	 * Authenticate the login request by comparing the username and password
	 * and the entities in the database.
	 * @param username the username should be unique.
	 * @param password the password that matches the username.
	 * @return true if the entity exist in database, otherwise return false.
	 */
	public User authenticate(String username, String password) {
		for (User user : users) {
			if (user.username.equals(username) && user.password.equals(password))
				return user;
		}
		return null;
	}

	/**
	 * Return all the type of rooms that that exist in the room list.
	 * @return a set of room types (eg. "Computer Labs", "Classroom").
	 */
	public Set<String> queueRoomTypes() {
		Set<String> roomSet = new HashSet<>();
		for (Room room : rooms) {
			roomSet.add(room.roomType);
		}
		return roomSet;
	}

	/**
	 * Return all the current booking records of the user.
	 * @param user which user booked the rooms.
	 * @return a list of slots which contains the Room id, timeslot, etc.
	 */
	public List<BookingSlot> queueCurrentBookings(User user) {
		List<BookingSlot> currentBookings = new ArrayList<BookingSlot>();
		for (BookingSlot slot : bookingSlots) {
			if (slot.user.equals(user) && slot.time.compareTo(Calendar.getInstance()) >= 0)
				currentBookings.add(slot);
		}
		return currentBookings;
	}

	public Room searchRoom(String roomID) {
		for (Room room : rooms) {
			if (room.roomID.equals(roomID))
				return room;
		}
		return null;
	}

	public User searchUser(String username) {
		for (User user : users) {
			if (user.username.equals(username))
				return user;
		}
		return null;
	}
	/**
	 * Return all the current booking records of the user.
	 * @param roomType a string that specified the room type. Should
	 *        be match a value returned by queueRoomTypes().
	 * @param date search the timeslots in which date.
	 * @return a list of slots which contains all the available timeslots in that date.
	 */

	public List<BookingSlot> queueAvailableSlot(String roomType, Calendar date) {
		List<BookingSlot> availableSlot = new ArrayList<BookingSlot>();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		Calendar start = (Calendar)date.clone();
		Calendar end = (Calendar)date.clone();

		start.set(Calendar.HOUR_OF_DAY, Settings.getInstance().dayStartTime.get(Calendar.HOUR_OF_DAY));
		end.set(Calendar.HOUR_OF_DAY, Settings.getInstance().dayEndTime.get(Calendar.HOUR_OF_DAY));

		//generate all timeslot for the rooms
		for (Room room : rooms) {
			if (room.roomType.equals(roomType)) {
				Calendar tempCal = (Calendar)start.clone();
				while (tempCal.compareTo(end) <= 0) {
					if (tempCal.compareTo(Calendar.getInstance()) >= 0) {
						availableSlot.add(new BookingSlot(room, tempCal));
						tempCal = (Calendar)tempCal.clone();
					}
					tempCal.add(Calendar.HOUR_OF_DAY, 1);
				}
			}
		}
		//remove the booked timeslot
		for (Iterator<BookingSlot> iterator = availableSlot.iterator(); iterator.hasNext(); ) {
			BookingSlot slot = iterator.next();
			for (BookingSlot bookedslot : bookingSlots) {
				if (bookedslot.room.roomID.equals(slot.room.roomID) && bookedslot.time.compareTo(slot.time) == 0) {
					iterator.remove();
					break;
				}
			}
		}
		return availableSlot;
	}

	/**
	 * Add a new booking into the database.
	 * @param user user who is making this booking.
	 * @param slot the room and the timeslot to be booked.
	 * @return true if nothing the adding is successful.
	 */
	public boolean addBooking(User user, BookingSlot slot) {
		if (user == null || slot == null)
			return false;
		bookingSlots.add(new BookingSlot(Integer.toString(bookingCount), user, slot.time, slot.room));
		return true;
	}
}