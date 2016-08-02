package org.first.team2485.scoutingserver;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;

import com.sun.glass.ui.Timer;

public class ScoutingServer extends JFrame {

	private static final byte[] IGNORE_TAG = "IGNORE".getBytes();
	
	private Process pythonServer = null;
	
	private boolean isScriptRunning = false;
	

	public static void main(String[] args) {
		new ScoutingServer();
	}

	private ScoutingServer() {

		JPanel lowerPane = new JPanel();
		
		JButton scriptButton = new JButton("Start Script");
		
		DirectoryButton scriptDirButton = new DirectoryButton(this);
		
		DirectoryButton dataDirButton = new DirectoryButton(this);
		
		UploadButton uploadButton = new UploadButton(dataDirButton);
		
		QuitButton quit = new QuitButton(this);
		
		scriptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				if (scriptDirButton.getSelectedFile() == null) {
					
					System.out.println("Script file not selected yet");
					
					return;
					
				}
				
				pythonServer = cmd("py " + scriptDirButton.getSelectedFile().getAbsolutePath(), false);

				while (pythonServer.isAlive()) {

					try {
						if (pythonServer.getInputStream().read() != -1 || pythonServer.getErrorStream().read() != -1) {
							pythonServer.getOutputStream().write(IGNORE_TAG);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
				
				
			}
		});
		
		lowerPane.add(scriptButton, BorderLayout.NORTH);
		lowerPane.add(scriptDirButton, BorderLayout.NORTH);
		lowerPane.add(dataDirButton, BorderLayout.NORTH);
		lowerPane.add(uploadButton, BorderLayout.NORTH);
		lowerPane.add(quit, BorderLayout.NORTH);
		
		
		this.add(lowerPane);
		
		
		
		
		this.pack();
		
		this.setVisible(true);
		
		
	}

	private static Process cmd(String cmd, boolean block) {
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			if (block) {
				p.waitFor();
			}
			return p;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public static String loadFormVersion(File jarLoc) throws IOException {

		FileReader fileReader = new FileReader(jarLoc/* "C:/Users/Nicholas/Desktop/More Stuff/SpamKill.jar" */);

		System.out.println("file");

		BufferedReader bufferedReader = new BufferedReader(fileReader);

		System.out.println("Buffer");

		String data = "";

		String curLine = bufferedReader.readLine();

		System.out.println("read");

		while (curLine != null) {
			data += "!@#$%^&*()" + curLine;
			curLine = bufferedReader.readLine();
		}

		bufferedReader.close();

		System.out.println("print");

		System.out.println(data);
		return data;
	}
}
