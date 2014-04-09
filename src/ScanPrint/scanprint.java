package ScanPrint;

/*
 * This simple application uses the Morena framework to get an image from a printer
 * and then print it. 
 * 
 * Author:David Corbin
 */

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import javax.print.*;
import java.io.*;

import eu.gnome.morena.*;
import java.util.List;
import ScanPrint.SynchronousHelper;


import com.apple.eawt.Application;

public class scanprint implements ActionListener{
	JPanel titlepanel;
    JFrame mainwindow;
    JLabel spacer, desc;
    Button start;
    Image image;
    Application application;
    PrintService[] printServices;
    JRadioButton printlist;
    ButtonGroup printerlistb;
    Manager manager;
    List<Device> devices;
    Device device;

	public static void main(String[] args) {
		scanprint asdf = new scanprint();
		asdf.basewindow();
	}


	private void basewindow(){
	    mainwindow = new JFrame();
	    mainwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    mainwindow.setSize(400, 400);
	    mainwindow.getContentPane();
	
	    startpanel();
	    mainwindow.setResizable(false);
	    mainwindow.setVisible(true); 
	    
	    application = Application.getApplication();
	    image = Toolkit.getDefaultToolkit().getImage("icon.png");
	    application.setDockIconImage(image);
	}

	private void startpanel(){
	    titlepanel = new JPanel();
	    titlepanel.setPreferredSize(new Dimension(300, 300));
	    start = new Button("Start");
	    start.setFont(new Font("Verano", Font.BOLD, 60));
	    //start.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
	    start.addActionListener(this);
	    //Border empty = new EmptyBorder(top, left, bottom right);
	    desc = new JLabel("<html><center>Scanning and printing<br> program by <br>David Corbin</center></html>");
	    desc.setBorder(BorderFactory.createEmptyBorder(30, 60, 100, 60));
	    desc.setFont(new Font("Verano", Font.BOLD, 20));
	    mainwindow.add(titlepanel);
	    titlepanel.add(desc);
	    titlepanel.add(start); 
	}

	public void alert(String alertdata){
	    JOptionPane.showMessageDialog(null, alertdata);
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			scanning();
		}
		catch (Exception f){
			System.out.println("Fail");
		}
	}
	
    static int timerval = 3;
	private void scanning() throws Exception {
	    start.setVisible(false);
	    desc.setVisible(false);
	    
	    manager = Manager.getInstance();
	    devices = manager.listDevices();
	    
	    //If there are no scanners connected
	    if (devices.isEmpty()){
	    	JLabel scanning = new JLabel("No scanners connected.");
		    scanning.setFont(new Font("Verano", Font.BOLD, 20));
		    scanning.setBorder(BorderFactory.createEmptyBorder(30, 60, 100, 60));
		    titlepanel.add(scanning);
	    }
	    
	    //If there are devices connected
	    else {
	    	device = (Device) devices.get(0);
	    	if (device instanceof Scanner)  {
	    		JLabel scanning = new JLabel("<html>Scanning from<br>" + device + "</html>");
			    scanning.setFont(new Font("Verano", Font.BOLD, 20));
			    scanning.setBorder(BorderFactory.createEmptyBorder(30, 60, 100, 60));
			    titlepanel.add(scanning);
			    
				final JLabel time = new JLabel("<html><center>" + timerval + "</center></html>");
			    time.setFont(new Font("Verano", Font.BOLD, 20));
			    time.setBorder(BorderFactory.createEmptyBorder(30, 60, 100, 60));
			    titlepanel.add(time);
			    
			    final Timer timer = new Timer(1000, null);
			    timer.addActionListener(new ActionListener() {
			    	public void actionPerformed(ActionEvent evt) {
			        	if (timerval > 0){
			        		timerval--;
			        		System.out.println(timerval);
			        	}
			        	else {
			        		timer.stop();
			        		System.out.println(timerval);
			        	}
			        }
		        });
		        timer.start();
		        
			    File scandir = new File("Scanned Images");
			    if (!scandir.exists()) {
			    	scandir.mkdir();  
			    }

			    //BufferedImage bimage = SynchronousHelper.scanImage(device);
	    	}
	    	
	    	//If the device is a camera
	    	else if (device instanceof Camera) {
	    		JLabel scanning = new JLabel("Please unplug the camera from the computer.");
			    scanning.setFont(new Font("Verano", Font.BOLD, 20));
			    scanning.setBorder(BorderFactory.createEmptyBorder(30, 60, 100, 60));
			    titlepanel.add(scanning);
	    	}
	    	
	    	//Error catching
	    	else {
	    		JLabel scanning = new JLabel("An error has occured. Please reopen the application.");
			    scanning.setFont(new Font("Verano", Font.BOLD, 20));
			    scanning.setBorder(BorderFactory.createEmptyBorder(30, 60, 100, 60));
			    titlepanel.add(scanning);
	    	}
	    }
	}
	
	private void inbetween(){
		
	}
	
	private void print() {
	    printerlistb = new ButtonGroup();
	    printServices = PrintServiceLookup.lookupPrintServices(null, null);
	    for (PrintService printer : printServices) {
	        String printera = printer.getName();
	        printlist = new JRadioButton(printera);
	        printerlistb.add(printlist);	        
	        titlepanel.add(printlist);
	        titlepanel.add(spacer = new JLabel(" "),"span, grow");
	    }
	}
}
