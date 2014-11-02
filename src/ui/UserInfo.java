package ui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import IRBS.*;

public class UserInfo extends JDialog implements WindowFocusListener {
	JMenuBar mbar;
	User currentUser;
	JTable table;
	MyTableModel myTableModel;
	JLabel label_1_2;
	String currentBookingStr = "Current Bookings: ";

	public void windowGainedFocus(WindowEvent e) {
		myTableModel.updateModel();
		myTableModel.fireTableDataChanged();
		int countBooked = BookingController.getInstance().queryCurrentBookings(UserInfo.this.currentUser).size();
		label_1_2.setText(currentBookingStr + countBooked);
	}
	public void windowLostFocus(WindowEvent e) {}

	public UserInfo(User currentUser) {
		this.currentUser = currentUser;
		addWindowFocusListener(this);
		setTitle("Welcome");
		setLayout(new GridLayout(10, 1));
		JLabel label_1_1 = new JLabel("Hello, " + currentUser.getFirstName() + " " + currentUser.getLastName());
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
				new BookingFrame(UserInfo.this.currentUser);
			}
		});
		add(button);

		setModal (true);
		setModalityType (ModalityType.APPLICATION_MODAL);

		setSize(800, 800);
		setVisible(true);
		//setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	class MyTableModel extends AbstractTableModel {
		private List<Booking> bookings;
		private String[] columnNames = {"BookingID",
		                                "StaffID",
		                                "Room Number",
		                                "Booking Time"
		                               };
		public MyTableModel() {
			this.bookings = BookingController.getInstance().queryCurrentBookings(currentUser);
		}
		public void updateModel() {
			this.bookings = BookingController.getInstance().queryCurrentBookings(currentUser);
		}
		public int getColumnCount() {
			return columnNames.length;
		}
		public int getRowCount() {
			return bookings.size();
		}
		public String getColumnName(int col) {
			return columnNames[col];
		}
		public Object getValueAt(int row, int col) {
			if (col == 0)
				return bookings.get(row).getBookingID();
			if (col == 1)
				return bookings.get(row).getBookingUser().getStaffID();
			if (col == 2)
				return bookings.get(row).getTimeslot().getRoom().getRoomNumber();
			if (col == 3)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy hh:mm aaa");
				return sdf.format(bookings.get(row).getTimeslot().getDatetime().getTime());
			}
			return null;
		}
	}
}