package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.List;
import java.util.Date;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import IRBS.*;

public class BookingFrame extends JDialog {
	JDatePickerImpl datePicker;
	User currentUser;
	JComboBox<String> comboList;

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

		comboList = new JComboBox<String>();
		for (String s : BookingController.getInstance().queryRoomTypes()) {
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
					                              "Please select a valid date",
					                              "Date Error",
					                              JOptionPane.ERROR_MESSAGE);
			}
		});
		add(button);

		setModal (true);
		setModalityType (ModalityType.APPLICATION_MODAL);

		setVisible(true);
	}
}