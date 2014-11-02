package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import IRBS.*;

public class Login extends JFrame {
	JButton login;
	JTextField username;
	JPasswordField pass;
	JLabel u, p;
	public Login() {
		setTitle("Login");
		setLayout(new GridLayout(3, 2));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		u = new JLabel("StaffID");
		p = new JLabel("Password");
		username = new JTextField(20);
		pass = new JPasswordField(20);
		login = new JButton("Login");

		add(u);
		add(username);
		add(p);
		add(pass);
		add(login);

		username.requestFocus();

		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String un = username.getText();
				String pa = new String(pass.getPassword());

				User u = BookingController.getInstance().authenticate(un, pa);
				if (u != null) {
					new UserInfo(u);
					//reset login page for next user
					username.setText("");
					pass.setText("");
					u = null;
				} else {
					JOptionPane.showMessageDialog(null,
					                              "StaffID or Password is incorrect",
					                              "Login Failed",
					                              JOptionPane.ERROR_MESSAGE);
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
		username.addKeyListener(k);

		pack();
		setLocationRelativeTo(null);
	}
	public static void main(String args[]) {
		System.out.println("**Please refer to README file for testing**");
		new Login();
	}
}