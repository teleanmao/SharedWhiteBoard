package whiteboard;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;

//Zelin Mao 1112821 COMP90015 Ass2 


public class Kicking extends JFrame {

	private JPanel contentPane;
	DataOutputStream output ;
	String chosenUser;

	public Kicking(DataOutputStream output, String[] currentUserList) {
		setTitle("Kick Out");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.output = output;
		chosenUser  = currentUserList[0];
		
		JComboBox userList = new JComboBox(currentUserList);
		userList.setFont(new Font("Arial", Font.PLAIN, 12));
		userList.setBounds(96, 102, 229, 23);
		contentPane.add(userList);
		
		userList.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			   {
						chosenUser = (String) userList.getSelectedItem();					
			   }
			
		});
		
		JLabel lblNewLabel = new JLabel("Please choose one of the user to kick out:");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		lblNewLabel.setBounds(96, 58, 239, 34);
		contentPane.add(lblNewLabel);
		
		JButton kickBtn = new JButton("Kick");
		kickBtn.setFont(new Font("Arial", Font.PLAIN, 12));
		kickBtn.setBounds(96, 175, 93, 23);
		contentPane.add(kickBtn);
		kickBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chosenUser!=null)
					try {
						output.writeUTF("KICK/" + chosenUser);
						System.out.println("Kick " + chosenUser);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				setVisible(false);
			}		
		});

		
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setFont(new Font("Arial", Font.PLAIN, 12));
		cancelBtn.setBounds(232, 175, 93, 23);
		contentPane.add(cancelBtn);
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}		
		});
	}
}
