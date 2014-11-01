/**
 *
 */
public class User implements Comparable<User> {
	private String staffID;
	private String firstName;
	private String lastName;
	@Override
	public int compareTo(User other) {
		return this.staffID.compareTo(other.staffID);
	}

	public User(String staffID, String firstName, String lastName) {
		this.staffID = staffID;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	public String getStaffID() {
		return staffID;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
}