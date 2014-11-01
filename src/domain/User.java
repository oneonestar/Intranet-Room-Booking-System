/**
 * Contail the information of a user except the password.
 */
public class User implements Comparable<User> {
	private String staffID;
	private String firstName;
	private String lastName;

	/**
	 * Compare the staffID lexicographically using the compareTo() from String class.
	 * @return 0 if the StaffID is equal
	 */
	@Override
	public int compareTo(User other) {
		return this.staffID.compareTo(other.staffID);
	}

	/**
	 * Only class in the same package can create an instance
	 */
	private User() {}

	/**
	 * The constructor is package access only to maintain data integrity.
	 */
	User(String staffID, String firstName, String lastName) {
		this.staffID = staffID;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	/**
	 * This function is responsible for returning the StaffID to the caller.
	 * @return the staff ID associate to the record
	 */
	public String getStaffID() {
		return staffID;
	}
	/**
	 * This function is responsible for returning the first name to the caller.
	 * @return the first name of the user
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * This function is responsible for returning the last name to the caller.
	 * @return the last name of the user
	 */
	public String getLastName() {
		return lastName;
	}
}