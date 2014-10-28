import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.security.MessageDigest;

import java.util.*;
import java.util.List;
import java.util.Date;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;


class UserLogin extends JFrame {
	JButton login;
	JTextField uname;
	JPasswordField pass;
	JLabel u, p;
	public UserLogin() {
		setTitle("Login");
		setLayout(new GridLayout(3, 2));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		u = new JLabel("Username");
		p = new JLabel("Password");
		uname = new JTextField(20);
		pass = new JPasswordField(20);
		login = new JButton("Login");

		add(u);
		add(uname);
		add(p);
		add(pass);
		add(login);

		uname.requestFocus();

		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String un = uname.getText();
				String pa = new String(pass.getPassword());
				StringBuffer sb = new StringBuffer();

				try {
					MessageDigest md = MessageDigest.getInstance("SHA-256");
					md.update(pa.getBytes("UTF-8"));
					byte[] mdbytes = md.digest();

					for (int i = 0; i < mdbytes.length; i++) {
						sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
					}
				} catch (Exception e) {
					System.out.println(e);
				}

				User u = BookingController.getInstance().authenticate(un, sb.toString());
				if (u != null) {
					dispose();
					new MainFrame(u);
				}
			}
		});

		KeyAdapter k = new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER)
					login.doClick();
			}
		};

		pass.addKeyListener(k);
		uname.addKeyListener(k);

		pack();
		setLocationRelativeTo(null);
	}

	class MainFrame extends JFrame implements WindowFocusListener {
		JMenuBar mbar;
		User currentUser;
		JTable table;
		MyTableModel myTableModel;
		JLabel label_1_2;
		String currentBookingStr = "Current Bookings: ";

		public void windowGainedFocus(WindowEvent e) {
			myTableModel.updateModel();
			myTableModel.fireTableDataChanged();
			int countBooked = BookingController.getInstance().queueCurrentBookings(MainFrame.this.currentUser).size();
			label_1_2.setText(currentBookingStr + countBooked);
		}
		public void windowLostFocus(WindowEvent e) {}

		public MainFrame(User currentUser) {
			this.currentUser = currentUser;
			addWindowFocusListener(this);
			setTitle("Welcome");
			setLayout(new GridLayout(10, 1));
			JLabel label_1_1 = new JLabel("Hello, " + currentUser.firstName + " " + currentUser.lastName);
			label_1_2 = new JLabel(currentBookingStr);

			add(label_1_1);
			add(label_1_2);

			myTableModel = new MyTableModel();
			table = new JTable(myTableModel);
			table.setPreferredScrollableViewportSize(new Dimension(500, 500));

			JScrollPane scrollPane = new JScrollPane(table);

			add(scrollPane);

			JButton button = new JButton("New Booking");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					new BookingFrame(MainFrame.this.currentUser);
				}
			});
			add(button);



			setSize(800, 800);
			setVisible(true);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		class MyTableModel extends AbstractTableModel {
			private List<BookingSlot> slots;
			private String[] columnNames = {"BookingID",
			                                "UserName",
			                                "RoomID",
			                                "Booking Time"
			                               };
			public MyTableModel() {
				this.slots = BookingController.getInstance().queueCurrentBookings(currentUser);
			}
			public void updateModel() {
				this.slots = BookingController.getInstance().queueCurrentBookings(currentUser);
			}
			public int getColumnCount() {
				return columnNames.length;
			}
			public int getRowCount() {
				return slots.size();
			}
			public String getColumnName(int col) {
				return columnNames[col];
			}
			public Object getValueAt(int row, int col) {
				if (col == 0)
					return slots.get(row).bookingID;
				if (col == 1)
					return slots.get(row).user.username;
				if (col == 2)
					return slots.get(row).room.roomID;
				if (col == 3)
					return slots.get(row).time.getTime().toLocaleString();
				return null;
			}
		}
	}
	class BookingFrame extends JDialog  {
		JDatePickerImpl datePicker;
		User currentUser;
        JComboBox comboList;

		public BookingFrame(User currentUser) {
			this.currentUser = currentUser;
			setTitle("Booking");
			setLayout(new GridLayout(10, 1));
			setSize(800, 800);

			UtilDateModel model = new UtilDateModel();
			JDatePanelImpl datePanel = new JDatePanelImpl(model);
			datePicker = new JDatePickerImpl(datePanel);
			datePicker.setBounds(220, 350, 120, 30);
			add(datePicker);

			comboList = new JComboBox();
			for (String s : BookingController.getInstance().queueRoomTypes()) {
				comboList.addItem(s);
			}
            add(comboList);

			JButton button = new JButton("Search");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if ((Date)datePicker.getModel().getValue() == null) {
						JOptionPane.showMessageDialog(null,
						                              "Please select a date",
						                              "Date Error",
						                              JOptionPane.ERROR_MESSAGE);
						return;
					}
					Calendar selectedDate = Calendar.getInstance();
					selectedDate.setTime((Date)datePicker.getModel().getValue());
					Calendar today = Calendar.getInstance();
					today.set(Calendar.HOUR_OF_DAY, 0);
					today.set(Calendar.MINUTE, 0);
					today.set(Calendar.SECOND, 0);
					today.set(Calendar.MILLISECOND, 0);
					Calendar oneMonthAfterToday = Calendar.getInstance();
					oneMonthAfterToday.add(Calendar.MONTH, 1);
					if (selectedDate.compareTo(today) >= 0 && selectedDate.compareTo(oneMonthAfterToday) < 0) {
						dispose();
						new BookingFrameStep2(BookingFrame.this.currentUser, selectedDate, (String)BookingFrame.this.comboList.getSelectedItem());
					} else
						JOptionPane.showMessageDialog(null,
						                              "Please select a valid date" + selectedDate.compareTo(today),
						                              "Date Error",
						                              JOptionPane.ERROR_MESSAGE);
				}
			});
			add(button);

			setModal (true);
			setAlwaysOnTop (true);
			setModalityType (ModalityType.APPLICATION_MODAL);

			setVisible(true);
		}
	}


	class BookingFrameStep2 extends JDialog  {
		JDatePickerImpl datePicker;
		User currentUser;
		String currentBookingStr = "Current Selected Count: ";
		String totalBookingStr = "Total Booking: ";
		JLabel label_1_2;
		JLabel label_1_3;
		MyTableModel myTableModel;

		private void updateBooking() {
			int countSelected = myTableModel.countSelected();
			int countBooked = BookingController.getInstance().queueCurrentBookings(currentUser).size();

			label_1_2.setText(currentBookingStr + countSelected);
			label_1_3.setText(totalBookingStr + (countSelected + countBooked));
		}
		public BookingFrameStep2(User currentUser, Calendar selectedDay, String roomTypes) {
			setTitle("Available Timeslots");
			setLayout(new GridLayout(10, 1));
			this.currentUser = currentUser;
			myTableModel = new MyTableModel(BookingController.getInstance().queueAvailableSlot(roomTypes, selectedDay));
			JLabel label_1_1 = new JLabel("Available timeslots");
			label_1_2 = new JLabel(currentBookingStr);
			label_1_3 = new JLabel(totalBookingStr);

			add(label_1_1);
			add(label_1_2);
			add(label_1_3);

			JTable table = new JTable(myTableModel);
			table.setPreferredScrollableViewportSize(new Dimension(500, 500));

			JScrollPane scrollPane = new JScrollPane(table);

			add(scrollPane);

			JButton button = new JButton("Book it!");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					int countSelected = myTableModel.countSelected();
					int countBooked = BookingController.getInstance().queueCurrentBookings(BookingFrameStep2.this.currentUser).size();
					if (countSelected + countBooked > Settings.getInstance().maxBooking) {
						JOptionPane.showMessageDialog(null,
						                              "Maximum Booking Count is 5!",
						                              "Error",
						                              JOptionPane.ERROR_MESSAGE);
						return;
					} else {
						myTableModel.commitChange(BookingFrameStep2.this.currentUser);
						dispose();
					}
				}
			});
			add(button);


			setModal (true);
			//setAlwaysOnTop (true);
			setModalityType (ModalityType.APPLICATION_MODAL);

			updateBooking();
			setSize(800, 800);
			setVisible(true);
		}
		class MyTableModel extends AbstractTableModel {
			private List<BookingSlot> slots;
			private List<Boolean> selected;
			private String[] columnNames = {"BookingID",
			                                "UserName",
			                                "RoomID",
			                                "Booking Time",
			                                "Selected"
			                               };
			public MyTableModel(List<BookingSlot> slots) {
				this.slots = slots;
				selected = new ArrayList<Boolean>(Collections.nCopies(slots.size(), false));
			}
			public int getColumnCount() {
				return columnNames.length;
			}
			public int getRowCount() {
				return slots.size();
			}
			public String getColumnName(int col) {
				return columnNames[col];
			}
			public Object getValueAt(int row, int col) {
				if (col == 0)
					return "";
				if (col == 1)
					return "";
				if (col == 2)
					return slots.get(row).room.roomID;
				if (col == 3)
					return slots.get(row).time.getTime().toLocaleString();
				if (col == 4)
					return selected.get(row);
				return null;
			}

			public boolean isCellEditable(int row, int col) {
				if (col == 4) {
					return true;
				}
				return false;
			}

			public void setValueAt(Object value, int row, int col) {
				selected.set(row, (boolean)value);
				fireTableCellUpdated(row, col);
				updateBooking();
			}
			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}
			public int countSelected() {
				int count = 0;
				for (boolean b : selected)
					if (b == true)
						count++;
				return count;
			}
			public void commitChange(User currentBookings) {

				for (int i = 0; i < selected.size(); i++)
					if (selected.get(i) == true) {
						BookingController.getInstance().addBooking(currentBookings, slots.get(i));
					}
			}
		}
	}
	public static void main(String args[]) {
		BookingController.getInstance().loadRoomData("rooms.txt");
		BookingController.getInstance().loadUserData("users.txt");
		BookingController.getInstance().loadBookingData("booking.txt");

		System.out.println("tips: password are sha-256 hashed");
		System.out.println("user: abc001");
		System.out.println("password: cba001");
		new UserLogin();
	}
}