package whiteboard;
//Zelin Mao 1112821 COMP90015 Ass2 


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.TitledBorder;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;






public class whiteboard {
	private JList userList;
	private JFrame frame;
	public  Container board;
	private JButton lineBtn, rectBtn, circleBtn, ovalBtn, txtBtn, eraserBtn, colorBtn, kickBtn ;
	private JComboBox fileList;
	public DataInputStream input ;
    public DataOutputStream output ;
    public String mode; 
	private Circle Circle;
	private Oval Oval;
	private Line Line;
	private Eraser eraser;
	private Text Text;
	private Rectangle Rect;
	
	public String[] currentUsers;
	private static JTextArea chattextArea;
	private JScrollPane scrollPane;
	private JTextField inputtextField;
	
	

	
	public static JTextArea getTextArea() {
		return chattextArea;
	}
	/**
	 * @wbp.parser.entryPoint
	 */
	public void show() {
		frame = new JFrame("WhiteBoard");
		board = frame.getContentPane();
		board.setLayout(null);
	
		JPanel panel = new JPanel();
		panel.setLocation(0, 0);
		panel.setSize(770, 30);

		lineBtn = new JButton("Line");
		lineBtn.setFont(new Font("Arial", Font.PLAIN, 12));
		lineBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (board.getComponentCount() > 5) {
					Draw currentArea = (Draw) board.getComponent(5);
					Line = new Line(currentArea.img, currentArea.g2);
					board.remove(board.getComponentCount() - 1);
				} else {
					Line = new Line();
				}
				Line.setSize(770, 535);
			    Line.setLocation(0, 30);
				Line.setOutput(output);
				Line.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				

				board.add(Line);
				frame.revalidate();
				frame.repaint();
			}		
		});

		
		
		
		rectBtn = new JButton("Rect");
		rectBtn.setFont(new Font("Arial", Font.PLAIN, 12));
		rectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (board.getComponentCount() > 5) {
					Draw currentArea = (Draw) board.getComponent(5);
					Rect = new Rectangle(currentArea.img, currentArea.g2);
					board.remove(board.getComponentCount() - 1);
				} else {
					Rect = new Rectangle();
				}
				Rect.setSize(770, 535);
				Rect.setLocation(0, 30);
				Rect.setOutput(output);
				Rect.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				
				board.add(Rect);
				frame.revalidate();
				frame.repaint();
			}
		});

		circleBtn = new JButton("Circle");
		circleBtn.setFont(new Font("Arial", Font.PLAIN, 12));
		circleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (board.getComponentCount() > 5) {
					Draw currentArea = (Draw) board.getComponent(5);
					Circle = new Circle(currentArea.img, currentArea.g2);
					board.remove(board.getComponentCount() - 1);
				} else {
					Circle = new Circle();
				}
				Circle.setSize(770, 535);
				Circle.setLocation(0, 30);
				Circle.setOutput(output);
				Circle.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				
				board.add(Circle);
				frame.revalidate();
				frame.repaint();
			}
		});

		ovalBtn = new JButton("Oval");
		ovalBtn.setFont(new Font("Arial", Font.PLAIN, 12));
		ovalBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (board.getComponentCount() > 5) {
					Draw currentArea = (Draw) board.getComponent(5);
					Oval = new Oval(currentArea.img, currentArea.g2);
					board.remove(board.getComponentCount() - 1);
				} else {
					Oval = new Oval();
				}
				Oval.setSize(770, 535);
				Oval.setLocation(0, 30);
				Oval.setOutput(output);
				Oval.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				
				board.add(Oval);
				frame.revalidate();
				frame.repaint();
			}
		});

		JTextField wordBox = new JTextField(10);
		wordBox.setFont(new Font("Arial", Font.PLAIN, 12));

		colorBtn = new JButton("Color");
		colorBtn.setFont(new Font("Arial", Font.PLAIN, 12));
		colorBtn.setBackground(new Color(0, 0, 0));
		colorBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color c =JColorChooser.showDialog(frame, "Color Chooser", colorBtn.getBackground());
				colorBtn.setBackground(c);
				
				if (board.getComponentCount() > 5) {
					Draw currentArea = (Draw) board.getComponent(5);
					
					currentArea.g2.setColor(c);
				
				} 
			}
		});


		if(mode == "Manager") {
			String[] method = {"New", "Open", "Save",  "Save As", "Close"};
			
			
			
			
			fileList = new JComboBox(method);
			fileList.setFont(new Font("Arial", Font.PLAIN, 12));
			fileList.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				   {
						
						String chosen = (String) fileList.getSelectedItem();
						if(chosen.equals("New")) {
							try {
								output.writeUTF("NEW_FILE");
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (board.getComponentCount() > 5) {
								Draw currentArea = (Draw) board.getComponent(5);
								Color originColor = currentArea.g2.getColor();
								currentArea.loadImg(currentArea.createImage(currentArea.getSize().width, currentArea.getSize().height));
								currentArea.loadG2((Graphics2D) currentArea.img.getGraphics());
								currentArea.g2.setColor(originColor);
								currentArea.clearScreen();
							}
						}else if(chosen.equals("Close")) {
							try {
								output.writeUTF("QUIT");
								System.exit(0);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
							
						}else if(chosen.equals("Save")) {
							
							 
							 JFileChooser fc = new JFileChooser();
				             FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
				             fc.setFileFilter(filter);
				             int result = fc.showSaveDialog(frame);
				             
				             
				             if (result == JFileChooser.APPROVE_OPTION) {
				            	 File file = fc.getSelectedFile();
				                 String fileName = file.getPath();
				                 
				                 File imgFile = new File(fileName);
				                 
				                 if (board.getComponentCount() > 5) {
				                	 	Draw currentArea = (Draw) board.getComponent(5);
										Image drawnImg = currentArea.img;
										BufferedImage bimage = new BufferedImage(drawnImg.getWidth(null), drawnImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
										Graphics2D g = bimage.createGraphics(); 
										g.drawImage(drawnImg, 0, 0, null);
										g.dispose();
										try {
											ImageIO.write(bimage, "png", imgFile);
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											JOptionPane.showMessageDialog(frame, "Can not save the file.");										}
									}
				             }
							 
							 
							 
							 
							 
							 
						}else if(chosen.equals("Save As")) {
							 
							 JFileChooser fc = new JFileChooser();
				             FileNameExtensionFilter filter1 = new FileNameExtensionFilter("JPG", "jpg");
				             FileNameExtensionFilter filter2 = new FileNameExtensionFilter("JPEG", "jpeg");
				             FileNameExtensionFilter filter3 = new FileNameExtensionFilter("BMP", "bmp");
				             FileNameExtensionFilter filter4 = new FileNameExtensionFilter("PNG", "png");
				             
				             fc.addChoosableFileFilter(filter1);
				             fc.addChoosableFileFilter(filter2);
				             fc.addChoosableFileFilter(filter3);
				             fc.addChoosableFileFilter(filter4);
				             fc.setFileFilter(fc.getChoosableFileFilters()[1]);
				             
				             int result = fc.showSaveDialog(frame);
				             
				             if (result == JFileChooser.APPROVE_OPTION) {
				            	 File file = fc.getSelectedFile();
				                 String fileName = file.getPath();
				                 
				                 File imgFile = new File(fileName);
				                 
				                 if (board.getComponentCount() > 5) {
				                	 	Draw currentArea = (Draw) board.getComponent(5);
										Image drawnImg = currentArea.img;
										BufferedImage bimage = new BufferedImage(drawnImg.getWidth(null), drawnImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
										Graphics2D g = bimage.createGraphics(); 
										g.drawImage(drawnImg, 0, 0, null);
										g.dispose();
										if(fileName.endsWith(".bmp")){
											try {
												ImageIO.write(bimage, "bmp", imgFile);
											} catch (IOException e1) {
												// TODO Auto-generated catch block
												JOptionPane.showMessageDialog(frame, "Can not save the file.");
											}
										}
										else if(fileName.endsWith(".png")) {
											try {
												ImageIO.write(bimage, "png", imgFile);
											} catch (IOException e1) {
												// TODO Auto-generated catch block
												JOptionPane.showMessageDialog(frame, "Can not save the file.");
											}
										}
										else {
											try {
												ImageIO.write(bimage, "jpg", imgFile);
												
											} catch (IOException e1) {
												// TODO Auto-generated catch block
												JOptionPane.showMessageDialog(frame, "Can not save the file.");											}
										}
									}
				             }
							 
							 
							 
						}else if(chosen.equals("Open")) {
							JFileChooser fc = new JFileChooser();
							FileNameExtensionFilter filter = new FileNameExtensionFilter("IMAGE FILES", "jpg", "jpeg", "bmp", "png");
				            fc.setFileFilter(filter);
				            int result = fc.showOpenDialog(frame);
				            if (result == JFileChooser.APPROVE_OPTION) {
				            	File file = fc.getSelectedFile();
				            	try {
									BufferedImage imgBuffer = ImageIO.read(file);
									BufferedImage jpgBuffer = new BufferedImage(imgBuffer.getWidth(), imgBuffer.getHeight(), BufferedImage.TYPE_INT_RGB);
									jpgBuffer.createGraphics().drawImage(imgBuffer, 0, 0, null);
									
									 
									 File temp = new File("temp.jpg");
									 ImageIO.write(jpgBuffer, "jpg", temp);
									 long fileSize = temp.length();
									 byte[] sendingBuffer = new byte[1024*1024];
									 RandomAccessFile byteFile = new RandomAccessFile(temp,"r");
									 
									// Send to server.
						     		output.writeUTF("OPEN_IMG/" + Long.toString(fileSize));

						     		int num;
									
									while((num = byteFile.read(sendingBuffer)) > 0){
										output.write(Arrays.copyOf(sendingBuffer, num));
									}
									byteFile.close();
									temp.delete();
					
						     		//Open locally.
						     		if (board.getComponentCount() > 5) {
										Draw currentArea = (Draw) board.getComponent(5);
										Color orginColor = currentArea.getG2().getColor();
										Graphics2D g2 = (Graphics2D) jpgBuffer.getGraphics();
										g2.setColor(orginColor);
										currentArea.loadImg(jpgBuffer);
										currentArea.loadG2(g2);
										currentArea.repaint();
										
									}
								} catch (IOException e1) {
									
									JOptionPane.showMessageDialog(frame, "Can not open the file.");	
								}
				            	
				            }
				            
							
							
						}
						
				   }
				
			});
			panel.add(fileList);
		}
		
	

		panel.add(lineBtn);
		panel.add(rectBtn);
		panel.add(circleBtn);
		panel.add(ovalBtn);
		
				eraserBtn = new JButton("Eraser");
				eraserBtn.setFont(new Font("Arial", Font.PLAIN, 12));
				eraserBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						if (board.getComponentCount() > 5) {
							Draw currentArea = (Draw) board.getComponent(5);
							eraser = new Eraser(currentArea.img, currentArea.g2);
							board.remove(board.getComponentCount() - 1);
							
							eraser.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
							eraser.setSize(770, 535);
							eraser.setLocation(0, 30);
							eraser.setOutput(output);
							board.add(eraser);
							frame.revalidate();
							frame.repaint();
						}
					}
				});
				panel.add(eraserBtn);
		panel.add(wordBox);
		
				txtBtn = new JButton("Text");
				txtBtn.setFont(new Font("Arial", Font.PLAIN, 12));
				txtBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (board.getComponentCount() > 5) {
							Draw currentArea = (Draw) board.getComponent(5);
							Text = new Text(currentArea.img, currentArea.g2);
							board.remove(board.getComponentCount() - 1);
						} else {
							Text = new Text();
						}
						Text.setSize(770, 535);
						Text.setLocation(0, 30);
						Text.setOutput(output);
						Text.inputStr = wordBox.getText();
						board.add(Text);
						frame.revalidate();
						frame.repaint();
					}
				});
				panel.add(txtBtn);
		panel.add(colorBtn);
		
		if(mode == "Manager") {
			
			kickBtn = new JButton("Kick");
			kickBtn.setFont(new Font("Arial", Font.PLAIN, 12));
			kickBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(currentUsers != null) {
						if(currentUsers.length ==1) {
							JOptionPane.showMessageDialog(frame, "No user to kick out.");
							return;
						}else {
							String[] ul = new String[currentUsers.length -1];
							for (int i = 1; i < currentUsers.length; i++) {
									ul[i-1] = currentUsers[i];
							}
							Kicking kick = new Kicking(output, ul);
							kick.setVisible(true);
						}	
					}
				}
			});
			panel.add(kickBtn);
		}

		board.add(panel);
		
	    userList = new JList<String>();
	    userList.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Current User", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
	    userList.setSize(215, 200);
	    userList.setLocation(770, 30);
	    userList.setPreferredSize(new Dimension(150, 100));
	    userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    board.add(userList);
	    
	    
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(771, 230, 215, 300);
		board.add(scrollPane);
		
		chattextArea = new JTextArea();
		chattextArea.setFont(new Font("Arial", Font.PLAIN, 12));
		chattextArea.setEditable(false);
		scrollPane.setViewportView(chattextArea);
		
		inputtextField = new JTextField();
		inputtextField.setBounds(771, 534, 156, 25);
		frame.getContentPane().add(inputtextField);
		inputtextField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.setFont(new Font("Arial", Font.PLAIN, 9));
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String msg = inputtextField.getText();
					
					String request = "CHAT/"+ msg; 
					
					output.writeUTF(request);
					
					
					
					
					
					
					
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		btnSend.setBounds(929, 535, 56, 23);
		board.add(btnSend);
		
		Line = new Line();
	    Line.setOutput(output);
		Line.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	    Line.setSize(770, 535);
	    Line.setLocation(0, 30);
		board.add(Line);
		
		
		

		frame.setSize(1000, 600);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	try {
					output.writeUTF("QUIT");
					System.exit(0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		    }
		});

		frame.setVisible(true);
	}
}


