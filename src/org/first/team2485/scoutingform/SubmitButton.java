package org.first.team2485.scoutingform;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.first.team2485.scoutingform.formIO.FormIO;

/**
 * 
 * @author Jeremy McCulloch
 * @author Nicholas Contreras
 */
@SuppressWarnings("serial")
public class SubmitButton extends JButton implements ActionListener {

	private ScoutingForm form;

	public SubmitButton(ScoutingForm form) {

		super("Submit");

		this.form = form;
		this.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String dataToSend = form.submit();
		String result = FormIO.getInstance().saveData(dataToSend, false);

		boolean resetForm = false;

		if (result.equals("Successfully saved the scouting data")) {

			result = FormIO.getInstance().sendData();

			JOptionPane.showMessageDialog(null, result);

			if (result.equals("Sucessfully sent scouting data")) {
				resetForm = true;
			}
		} else if (result.equals("Unsent data already exists")) {

			int input = JOptionPane.showConfirmDialog(null, "Unsent data already exists\nDo you want to send it first?",
					"Warning", JOptionPane.YES_NO_OPTION);

			if (input == JOptionPane.YES_OPTION) {

				result = FormIO.getInstance().sendData();

				JOptionPane.showMessageDialog(null, "When sending old data...\n" + result);

				if (!result.equals("Sucessfully sent scouting data")) {

					input = JOptionPane.showConfirmDialog(null,
							"The old data did not send sucessfully\nDo want to overwrite it with (and send) the new data?",
							"Warning", JOptionPane.YES_NO_OPTION);

					if (input == JOptionPane.YES_OPTION) {

						result = FormIO.getInstance().saveData(dataToSend, true);

						if (!result.equals("Successfully saved the scouting data")) {
							JOptionPane.showMessageDialog(null, result);
						} else {
							result = FormIO.getInstance().sendData();

							JOptionPane.showMessageDialog(null, result);

							if (result.equals("Sucessfully sent scouting data")) {
								resetForm = true;
							}
						}
					}
				} else {
					result = FormIO.getInstance().saveData(dataToSend, false);

					if (!result.equals("Successfully saved the scouting data")) {
						JOptionPane.showMessageDialog(null, result);
					} else {
						result = FormIO.getInstance().sendData();

						JOptionPane.showMessageDialog(null, result);

						if (result.equals("Sucessfully sent scouting data")) {
							resetForm = true;
						}
					}
				}
			} else {
				result = FormIO.getInstance().saveData(dataToSend, true);

				if (!result.equals("Successfully saved the scouting data")) {
					JOptionPane.showMessageDialog(null, result);
				} else {
					result = FormIO.getInstance().sendData();

					JOptionPane.showMessageDialog(null, result);
				}

			}
		} else {
			JOptionPane.showMessageDialog(null, result);
		}

		if (resetForm) {
			form.reset();
		}
	}
}
