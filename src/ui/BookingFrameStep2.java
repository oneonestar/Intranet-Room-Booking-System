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

class BookingFrameStep2 extends JDialog {
	User currentUser;
	String currentBookingStr = "Current Selected Count: ";
	String totalBookingStr = "Total Booking: ";
	JLabel label_1_2;
	JLabel label_1_3;
	MyTableModel myTableModel;

	private void updateBooking() {
		int countSelected = myTableModel.countSelected();
		int countBooked = BookingController.getInstance().queryCurrentBookings(currentUser).size();

		label_1_2.setText(currentBookingStr + countSelected);
		label_1_3.setText(totalBookingStr + (countSelected + countBooked));
	}
	public BookingFrameStep2(User currentUser, Calendar selectedDay, String roomTypes) {
		setTitle("Available Timeslots");
		setLayout(new GridLayout(10, 1));
		this.currentUser = currentUser;
		myTableModel = new MyTableModel(BookingController.getInstance().queryAvailableTimeslot(roomTypes, selectedDay));
		JLabel label_1_1 = new JLabel("Available timeslots");
		label_1_2 = new JLabel(currentBookingStr);
		label_1_3 = new JLabel(totalBookingStr);

		add(label_1_1);
		add(label_1_2);
		add(label_1_3);

		JTable table = new JTable(myTableModel);
		table.setPreferredScrollableViewportSize(new Dimension(500, 1000));

		JScrollPane scrollPane = new JScrollPane(table);

		add(scrollPane);

		JButton button = new JButton("Book");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int countSelected = myTableModel.countSelected();
				int countBooked = BookingController.getInstance().queryCurrentBookings(BookingFrameStep2.this.currentUser).size();
				if (countSelected + countBooked > 5) {
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
		private List<Timeslot> slots;
		private List<Boolean> selected;
		private String[] columnNames = {"Room Number",
		                                "Booking Time",
		                                "Select"};
		public MyTableModel(List<Timeslot> slots) {
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
				return slots.get(row).getRoom().getRoomNumber();
			if (col == 1)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy hh:mm aaa");
				return sdf.format(slots.get(row).getDatetime().getTime());
			}
			if (col == 2)
				return selected.get(row);
			return null;
		}

		public boolean isCellEditable(int row, int col) {
			if (col == 2) {
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