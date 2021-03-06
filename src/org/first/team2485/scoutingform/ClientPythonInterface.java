package org.first.team2485.scoutingform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.first.team2485.common.Message;
import org.first.team2485.common.Message.MessageType;

public class ClientPythonInterface {

	private static ClientPythonInterface instance;

	private File containingFolder;

	private Process pythonProcess; // the process of the python script

	protected ArrayList<Message> messageHistory;
	protected ArrayList<Message> unhandledMessages;

	private BufferedReader pythonOutput;
	private BufferedWriter pythonInput;

	private String os;

	protected static ClientPythonInterface getInstance() { // only allow one
															// instance
		if (instance == null) {
			instance = new ClientPythonInterface();
		}
		return instance;
	}

	private ClientPythonInterface() {
		messageHistory = new ArrayList<Message>();
		unhandledMessages = new ArrayList<Message>();

		os = System.getProperty("os.name");

		if (os.contains("Windows")) {
			os = "windows";
		} else {
			os = "mac";
		}

		String path = this.getClass().getResource("").getPath();

		System.out.println("Starting path: " + path);

		path = path.replaceAll(Pattern.quote("%20"), " ");
		path = path.replaceAll(Pattern.quote(".jar!"), ".jar");

		System.out.println("Finalzied Path: " + path);

		File f = new File(path);

		System.out.println("Path Object:" + f);

		while (!f.getName().equals("org")) {
			f = f.getParentFile();
		}

		containingFolder = f.getParentFile().getParentFile();
	}

	protected Process getProcess() {
		return pythonProcess;
	}

	protected void startScript() {

		if (pythonProcess != null) {
			pythonProcess.destroyForcibly();
		}

		File scriptPath = new File(containingFolder, os + "Client.py");

		System.out.println("Script path: " + scriptPath);

		pythonProcess = cmd("python -u " + scriptPath.getPath(), false);

		System.out.println("started cmd. Is alive? " + pythonProcess.isAlive());

		pythonInput = new BufferedWriter(new OutputStreamWriter(pythonProcess.getOutputStream()));
		pythonOutput = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()));

		new Thread(() -> {
			try {
				readInputFromPython();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void readInputFromPython() throws IOException {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (os.equals("mac")) {
			String address = JOptionPane.showInputDialog("Server Address:");
			pythonInput.write(address);
			pythonInput.newLine();
			pythonInput.flush();
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		pythonInput.write(ScoutingForm.name);
		pythonInput.newLine();
		pythonInput.flush();

		while (pythonProcess.isAlive() || pythonOutput.ready()) {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (pythonOutput.ready()) {

				String curMessage = null;
				try {
					curMessage = pythonOutput.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (curMessage != null) {

					System.out.println("read message: " + curMessage);

					if (curMessage.startsWith("MESSAGE")) {

						Message message = new Message(curMessage.substring("MESSAGE".length()));

						if (message.getMessageType() == MessageType.FORM_UPDATE) {
							saveFormVersion(message.getMessage());
						}

						System.out.println("added message");
						messageHistory.add(message);
						unhandledMessages.add(message);
					} else {
						Message debugMessage = new Message(curMessage, ScoutingForm.name, "DEBUG", MessageType.CHAT);

						messageHistory.add(debugMessage);
						unhandledMessages.add(debugMessage);
					}
				}
			} else {
				System.out.println("Input was null");
			}

			sendStringToPython("READ_ONLY");
			pythonInput.newLine();
			pythonInput.flush();
		}
	}

	protected void sendStringToPython(String s) {
		try {
			pythonInput.write(s + "^");
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	protected String saveData(String data, boolean forceWrite) { // method
																	// messy,
																	// fix
		
		File unsentDataFolder = new File(containingFolder, "unsentData");
		
		if (!unsentDataFolder.isDirectory()) {
			unsentDataFolder.mkdir();
		}

		File file = new File(containingFolder, "scoutingData.csv");

		if (file.exists() && !forceWrite) {
			return "Unsent data already exists";
		}

		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);

			fileWriter.write(data);

			fileWriter.flush();

			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "Failed to write scouting data to file";
		}
		return "Successfully saved the scouting data";

	}

	protected String sendData() {

		sendStringToPython("SEND_SCOUTING_DATA");

		for (int i = 0; i < 10; i++) {
			if (!new File(containingFolder, "unsentData/scoutingData.csv").exists()) {
				return "Sucessfully sent scouting data";
			}
		}
		return "An error occurred sending the data";
	}

	private void saveFormVersion(String newFormVersion) throws IOException {

		byte[] byteArray = new byte[newFormVersion.length()];

		for (int i = 0; i < byteArray.length; i++) {
			byteArray[i] = (byte) newFormVersion.charAt(i);
		}

		FileOutputStream outputStream = new FileOutputStream(
				new File(containingFolder, "Bluetooth Scouting Client " + System.currentTimeMillis()));

		outputStream.write(byteArray);

		outputStream.close();
	}

	protected boolean checkForUpdate() {

		File curFile = new File(getClass().getResource("").getPath());

		while (!curFile.getName().equals("org")) {
			curFile = curFile.getParentFile();
		}

		curFile = curFile.getParentFile();

		curFile = new File(curFile.getName().substring(0, curFile.getName().length() - 1).replace("%20", " "));

		System.out.println("I am: " + curFile.getName());

		for (File f : containingFolder.listFiles()) {
			if (!f.getName().equals(curFile.getName())) {
				if (f.lastModified() > curFile.lastModified()) {
					return true;
				}
			}
		}
		return false;
	}

	protected void switchToUpdatedVersion() {

		System.out.println("Updating version");

		File curFile = new File(getClass().getResource("").getPath());

		while (!curFile.getName().equals("org")) {
			curFile = curFile.getParentFile();
		}

		curFile = curFile.getParentFile();

		curFile = new File(curFile.getName().substring(0, curFile.getName().length() - 1).replace("%20", " "));

		System.out.println("I am: " + curFile.getName());

		File mostRecentFile = curFile;

		for (File f : containingFolder.listFiles()) {
			if (!f.getName().equals(curFile.getName())) {
				if (f.getName().contains("Bluetooth Scouting Client")) {

					System.out.println("Checking: " + f.getName());

					System.out.println("Best: " + mostRecentFile.lastModified());
					System.out.println("Cur: " + f.lastModified());

					if (mostRecentFile == null || f.lastModified() > mostRecentFile.lastModified()) {
						mostRecentFile = f;
					}
				}
			}
		}

		System.out.println("moved update to regular");

		String command = "java -jar \"" + containingFolder + "/" + mostRecentFile.getName() + "\"";

		System.out.println("Running command:" + command);

		cmd(command, false);

		System.out.println("ran new form");

		System.exit(0);
	}
}
