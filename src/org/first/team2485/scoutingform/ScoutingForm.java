package org.first.team2485.scoutingform;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.first.team2485.scoutingform.questions.CheckboxQuestion;
import org.first.team2485.scoutingform.questions.FreeResponseQuestion;
import org.first.team2485.scoutingform.questions.MultipleChoiceQuestion;
import org.first.team2485.scoutingform.questions.QuestionGroup;
import org.first.team2485.scoutingform.questions.SpinnerQuestion;

/**
 * 
 * @author Jeremy McCulloch
 *
 */
@SuppressWarnings("serial")
public class ScoutingForm extends JPanel {
  
	private JFrame frame;
	private ScoutingFormTab[] tabs;
	private JTabbedPane tabbedPane;

	public ScoutingForm(ScoutingFormTab... tabs) {
		
		frame = new JFrame();
		frame.add(this);
		frame.setSize(1000, 600);
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		this.tabbedPane = new JTabbedPane();
		
		this.tabs = tabs;
		for (ScoutingFormTab tab : tabs) {
			tabbedPane.add(tab.getName(), new JScrollPane(tab));
		}
		
		this.add(tabbedPane);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout());
		buttonPane.add(new SubmitButton(this));
		buttonPane.add(new QuitButton(this.frame));//this handles all quitting logic
		this.add(buttonPane);


		frame.pack(); 
		frame.setVisible(true);
		this.repaint(); 
		
	}

	public String submit() {

		String output = "";

		for (ScoutingFormTab tab : tabs) {
			output += tab.getData();
		}

		return output;

	} 
	

	public static void main(String[] args) {

		ScoutingFormTab prematch = new ScoutingFormTab("Prematch",
			new SpinnerQuestion("Team number"),
			new SpinnerQuestion("Match Number:")
		);
		
		ScoutingFormTab autonomous = new ScoutingFormTab("Autonomous", 
			new CheckboxQuestion("Did they...", "Approach a Defense", "Cross a Defense"), 
			new SpinnerQuestion("How many high goals did they make?"),
			new SpinnerQuestion("How many low goals did they make?")
		);
		
		ScoutingFormTab teleop = new ScoutingFormTab("Teleop", 
			new QuestionGroup("Defense Category A", 
				new MultipleChoiceQuestion("Which defense is up?", "Cheval de Frise", "Portcullis"),
				new MultipleChoiceQuestion("How long did it take to cross on avg (seconds)?", "0 - 5", "5 - 10", "10 - 15", "15+", "Failed"),
				new SpinnerQuestion("How many times did they cross it?")
			),
			new QuestionGroup("Defense Category B", 
				new MultipleChoiceQuestion("Which defense is up?", "Moat", "Ramparts"),
				new MultipleChoiceQuestion("How long did it take to cross on avg (seconds)?", "0 - 5", "5 - 10", "10 - 15", "15+", "Failed"),
				new SpinnerQuestion("How many times did they cross it?")
			),
			new QuestionGroup("Defense Category C", 
				new MultipleChoiceQuestion("Which defense is up?", "Drawbridge", "Sally Port"),
				new MultipleChoiceQuestion("How long did it take to cross on avg (seconds)?", "0 - 5", "5 - 10", "10 - 15", "15+", "Failed"),
				new SpinnerQuestion("How many times did they cross it?")
			),
			new QuestionGroup("Defense Category D", 
				new MultipleChoiceQuestion("Which defense is up?", "Rough Terrain", "Rock Wall"),
				new MultipleChoiceQuestion("How long did it take to cross on avg (seconds)?", "0 - 5", "5 - 10", "10 - 15", "15+", "Failed"),
				new SpinnerQuestion("How many times did they cross it?")
			),
			new QuestionGroup("Low Bar", 
				new MultipleChoiceQuestion("How long did it take to cross on avg (seconds)?", "0 - 5", "5 - 10", "10 - 15", "15+", "Failed"),
				new SpinnerQuestion("How many times did they cross it?")
			),
			new SpinnerQuestion("How many high goals did they make?"),
			new SpinnerQuestion("How many high goals did they miss?"),
			new SpinnerQuestion("How many low goals did they make?"),
			new SpinnerQuestion("How many low goals did they miss?"),
			new MultipleChoiceQuestion("Did they...", "Challenge", "Scale", "Neither")
		);//<--- sad winky face 
		
		ScoutingFormTab ratings = new ScoutingFormTab("Ratings", 
			new MultipleChoiceQuestion("Speed", "Snail", "Slow", "Average", "Speedy", "Lightning"),
			new MultipleChoiceQuestion("Manueverability", "Sluggish", "Unresponsive", "Average" , "Responsive", "Nimble"),
			new MultipleChoiceQuestion("Shooter Repeatability", "Unreliable", "Sketchy", "Average", "Consistent", "Reliable", "N/A"), 
			new MultipleChoiceQuestion("Shooter Speed", "Snail", "Slow", "Average", "Speedy", "Lightning", "N/A"),
			new MultipleChoiceQuestion("Defense", "Shitty", "Decent", "Average", "Good", "Badass", "N/A"), 
			new MultipleChoiceQuestion("Defense Evasion", "Shitty", "Decent", "Average", "Good", "Badass", "N/A"), 
			new MultipleChoiceQuestion("Overall Impression", "Shitty", "Decent", "Average", "Good", "Badass")
		);
		
		ScoutingFormTab misc = new ScoutingFormTab("Miscellaneous", 
			new SpinnerQuestion("How many tech fouls did they get?"),
			new SpinnerQuestion("How many other fouls did they get?"),
			new CheckboxQuestion("What role(s) did they play?", "Shooter", "Breacher", "Defender", "Support"),
			new MultipleChoiceQuestion("Did they break down?", "Yes", "No"),
			new FreeResponseQuestion("Comments:")
		);
		
		new ScoutingForm(prematch, autonomous, teleop, ratings, misc);
		
	}
}
