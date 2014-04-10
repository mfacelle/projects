package calculator;

import javax.swing.*;
import javax.swing.border.*;

import parser.Parser;
import parser.exceptions.InvalidParseException;

import java.awt.*;
import java.awt.event.*;

/**	A GUI for the Calculator class, to parse functions input as Strings by the user at an optionally
 * 	specified x-value.
 *  <br><br>
 *  <b>Hierarchy of Components:</b><br>
 *  <pre><code>
 *  contentPane         :</code> the main pane, from getContentPane()<code><br>
 *    info_panel        :</code> contains info_label[0:3]<code><br>
 *      info_label[0:3] :</code> 4 labels; containts information, such as using constants or math functions<code><br>
 *    input_panel       :</code> contains all input labels and textfields<code><br>
 *      f_label         :</code> label for function input: FX_STR variable<code><br>
 *      f_panel         :</code> contains <code>f_input</code> textfield<code><br>
 *        f_input       :</code> textfield for inputting the function to be parsed<code><br>
 *      x_label         :</code> label for x-value input: X_STR variable<code><br>
 *      x_panel         :</code> contains <code>x_input</code> textfield<code><br>
 *        x_input       :</code> textfield for inputting the x-value to be parsed at<code><br>
 *    button_panel      :</code> contains <code>eval_button</code><code><br>
 *      eval_button     :</code> button to begin parsing the function String at an x-value<code><br>
 *    result_panel      :</code> contains <code>result_label</code><code><br>
 *      result_label    :</code> displays the result of parsing<code><br>
 *  </code></pre>
 *  
 * 
 * @author Mike Facelle
 *
 */
public class GUI extends JFrame implements ActionListener
{
	public static final long serialVersionUID = 0x10000;
	
	// sizes of panels
	/** The width of the overall window */
	public static final int WIDTH = 650;
	/** The height of <code>info_panel</code> */
	public static final int IFPANEL_HEIGHT = 75;
	/** The height of <code>input_panel</code> */
	public static final int IPANEL_HEIGHT = 75;
	/** The height of <code>button_panel</code> */
	public static final int BPANEL_HEIGHT = 125;
	/** The height of <code>result_panel</code> */
	public static final int RPANEL_HEIGHT = 60;
	/** The width of <code>eval_button</code> */
	public static final int BUTTON_WIDTH = 200;
	/** The height of <code>eval_button</code> */
	public static final int BUTTON_HEIGHT = 50;
	/** The number of characters for instantiating the <code>f_input</code> JTextfield */
	public static final int FINPUT_CHARS = 64;
	/** The number of characters for instantiating the <code>x_input</code>, <code>y_input</code>, and
	 * <code>z_input</code> JTextfields */
	public static final int XINPUT_CHARS = 11;
	
	// initial strings and labels:
	/** Initial function String to display in <code>f_input</code>: <code>"x^2"</code> */
	public static final String INIT_FUNC = "x^2";
	/** String to display in <code>f_label</code>: <code>" f(x) = "</code> */
	public static final String FX_STR = " f(x,y,z) = ";
	/** Initial x-value String to display in <code>x_input</code>: <code>"0"</code> */
	public static final String INIT_X = "0";
	/** String to display in <code>x_label</code>: <code>" x : "</code> */
	public static final String X_STR = " x : ";
	/** String to display in <code>y_label</code>: <code>" y : "</code> */
	public static final String Y_STR = " y : ";
	/** String to display in <code>z_label</code>: <code>" z : "</code> */
	public static final String Z_STR = " z : ";
	/** String to display in <code>eval_button</code>: <code>"Evaluate"</code> */
	public static final String BTN_TXT = "Evaluate";
	/** Initial result String to display in <code>result_label</code>: <code>"0.0"</code> */
	public static final String INIT_RESULT = "0.0";
	/** First line of info for <code>info_panel</code>: Constants label */
	public static final String INFO_0 = "Constants:";
	/** Second line of info for <code>info_panel</code>: Constants references */
	public static final String INFO_1 = "p : pi,    e : euler's";
	/** Third line of info for <code>info_panel</code>: Functions label */
	public static final String INFO_2 = "Mathematical functions:";
	/** Fourth line of info for <code>info_panel</code>: Functions references */
	public static final String INFO_3 = "sin, cos, tan, ln, abs, sqrt";
	/** Fifth line of info for <code>info_panel</code>: How to use scientific notation (1.0E3) */
	public static final String INFO_4 = "Scientific Notation:";
	/** LAST line of info for <code>info_panel</code>: How to use scientific notation (1.0E3) */
	public static final String INFO_5 = "use E like so: 1.0E3";
	
	// fonts:
	/** Font for JLabels <code>info_label_1</code>, <code>info_label_3</code> */
	public static final Font INF_FONT_0 = new Font(Font.MONOSPACED, 0, 12);
	/** Font for JLabels <code>info_label_0</code>, <code>info_label_2</code> (Bold) */
	public static final Font INF_FONT_B = new Font(Font.MONOSPACED, Font.BOLD, 12);
	/** Font for JLabel <code>f_label</code> and <code>x_label</code> */
	public static final Font LBL_FONT = new Font(Font.MONOSPACED, 0, 12);
	/** Font for JTextFields <code>f_input</code> and <code>x_input</code> */
	public static final Font TXT_FONT = new Font(Font.MONOSPACED, 0, 14);
	/** Font for JButton <code>eval_button</code> */
	public static final Font BTN_FONT = new Font(Font.MONOSPACED, 0, 14);
	/** Font for JLabel <code>result_label</code> */
	public static final Font RES_FONT = new Font(Font.MONOSPACED, Font.BOLD, 32);
	/** Font for JLabel <code>result_label</code> if parsing error is thrown */
	public static final Font ERR_FONT = new Font(Font.MONOSPACED, 0, 12);
	/** Font for JLabel <code>result_label</code>'s TitledBorder */
	public static final Font RTITLE_FONT = new Font(Font.MONOSPACED, Font.BOLD, 14);
	
	// borders:
	/** Border template for titled borders <code>IF_BORDER</code>, <code>F_BORDER</code>, <code>X_BORDER</code> */
	public static final Border I_BORDER_0 = BorderFactory.createLineBorder(Color.BLACK, 1);
	/** TitledBorder for <code>info_panel</code> */
	public static final Border IF_BORDER = BorderFactory.createTitledBorder(I_BORDER_0,"", TitledBorder.LEFT, TitledBorder.TOP, INF_FONT_0);
	/** TitledBorder for <code>f_panel</code> */
	public static final Border F_BORDER = BorderFactory.createTitledBorder(I_BORDER_0,"Function", TitledBorder.LEFT, TitledBorder.TOP, ERR_FONT);
	/** TitledBorder for <code>x_panel</code> */
	public static final Border X_BORDER = BorderFactory.createTitledBorder(I_BORDER_0,"x-value", TitledBorder.LEFT, TitledBorder.TOP, ERR_FONT);
	/** TitledBorder for <code>y_panel</code> */
	public static final Border Y_BORDER = BorderFactory.createTitledBorder(I_BORDER_0,"y-value", TitledBorder.LEFT, TitledBorder.TOP, ERR_FONT);
	/** TitledBorder for <code>z_panel</code> */
	public static final Border Z_BORDER = BorderFactory.createTitledBorder(I_BORDER_0,"z-value", TitledBorder.LEFT, TitledBorder.TOP, ERR_FONT);
	/** Template for titled border for <code>result_panel</code> */
	public static final Border R_BORDER_0 = BorderFactory.createLineBorder(Color.BLACK, 2);
	/** TitledBorder for <code>result_panel</code> */
	public static final Border R_BORDER = BorderFactory.createTitledBorder(R_BORDER_0,"Result", TitledBorder.LEFT, TitledBorder.TOP, RTITLE_FONT);

	
	// variables (for parsing)
	//private String function;	// function string from f_input textfield
	//private String x_val;		// x value string from x_input textfield
	/** Result String displayed on <code>result_label</code> */
	private String result;		
	
	// hierarchy of containers:
	/** contentPane retrieved from <code>this.getContentPane()</code> */
	private Container contentPane;
	
	/** Contains info_block (to set size of GridLayout) */
	private JPanel info_panel;
		/** Contains the JLabel <code>info_label[0:3]</code> */
		private JPanel info_block;
			/** Label to hold constants info */
			private JLabel info_01;
				/** Label for constants that can be used */
				private JLabel info_label_0;
				/** How to display <code>pi</code> and <code>e</code> constant */
				private JLabel info_label_1;
			/** Label to hold functions info */
			private JLabel info_23;
				/** Label for mathematical funcations that can be used */
				private JLabel info_label_2;
				/** How to use <code>sin</code>, <code>cos</code>, <code>tan</code>, 
				 * <code>ln</code>, <code>abs</code>, <code>sqrt</code>. */
				private JLabel info_label_3;
			/** Label to hold scientific notation info */
			private JLabel info_45;
				/** Scientific Notation label */
				private JLabel info_label_4;
				/** Displays how to use E for scientific notation*/
				private JLabel info_label_5;
	/** JPanel containing function input fields:<br> 
	 * 	function label and textfield (<code>f_label</code>, <code>f_input</code>), 
	 */
	private JPanel input_panel_1;
		/** Displays <code>f(x) =</code> in front of the function input JTextfield, <code>f_input</code>*/
		private JLabel f_label;
		/** Contains the JTextField <code>f_input</code> */
		private JPanel f_panel;
			/** Input field for the function String to be parsed */
			private JTextField f_input; 
	/**	JPanel containing all input fields for variables
	 * 	x-value label and textfield (<code>x_label</code>, <code>x_input</code>). 
	 * */
	private JPanel input_panel_2;	
		/** Displays <code>x :</code> in front of the input JTextfield, <code>x_input</code>*/
		private JLabel x_label;
		/** Contains the JTextField <code>x_input</code> */
		private JPanel x_panel;
			/** Input field for the x variable */
			private JTextField x_input;
		/** Displays <code>y :</code> in front of the input JTextfield, <code>y_input</code>*/
		private JLabel y_label;
		/** Contains the JTextField <code>y_input</code> */
		private JPanel y_panel;
			/** Input field for the y variable */
			private JTextField y_input;
		/** Displays <code>z :</code> in front of the input JTextfield, <code>z_input</code>*/
		private JLabel z_label;
		/** Contains the JTextField <code>z_input</code> */
		private JPanel z_panel;
			/** Input field for the z-variable */
			private JTextField z_input;
	/** Contians the JButton <code>eval_button</code> */
	private JPanel button_panel;
		/** JButton to begin parsing the function String from <code>f_input</code> 
		 * 	at the specified x-value in <code>x_input</code> */
		private JButton eval_button;
	/** Contains the JLabel <code>result_label</code> */
	private JPanel result_panel;
		/** Displays the result of parsing the function String from <code>f_input</code> 
		 * at the specified x-value in <code>x_input</code> */
		private JLabel result_label;
	
// ====================================================
		
	public GUI()
	{
		super();
		setTitle("Mike's Calculator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(WIDTH, IFPANEL_HEIGHT + IPANEL_HEIGHT*2 + BPANEL_HEIGHT + RPANEL_HEIGHT));
		setResizable(false);
		
		contentPane = getContentPane();
		
	// info panel:
		info_panel = new JPanel();
		info_panel.setLayout(new FlowLayout());
			info_block = new JPanel();
			// GridLayout seems to fill the full frame, no matter what I try to do...
			info_block.setLayout(new GridLayout(3,1));
			info_01 = new JLabel();
			info_01.setLayout(new FlowLayout());
				info_label_0 = new JLabel(INFO_0);
				info_label_0.setFont(INF_FONT_B);
				info_label_0.setHorizontalAlignment(SwingConstants.CENTER);
				info_label_1 = new JLabel(INFO_1);
				info_label_1.setFont(INF_FONT_0);
				info_label_1.setHorizontalAlignment(SwingConstants.CENTER);
			info_01.add(info_label_0);
			info_01.add(info_label_1);
			info_23 = new JLabel();
			info_23.setLayout(new FlowLayout());
				info_label_2 = new JLabel(INFO_2);
				info_label_2.setFont(INF_FONT_B);
				info_label_2.setHorizontalAlignment(SwingConstants.CENTER);
				info_label_3 = new JLabel(INFO_3);
				info_label_3.setFont(INF_FONT_0);
				info_label_3.setHorizontalAlignment(SwingConstants.CENTER);
			info_23.add(info_label_2);
			info_23.add(info_label_3);
			info_45 = new JLabel();
			info_45.setLayout(new FlowLayout());
				info_label_4 = new JLabel(INFO_4);
				info_label_4.setFont(INF_FONT_B);
				info_label_4.setHorizontalAlignment(SwingConstants.CENTER);
				info_label_5 = new JLabel(INFO_5);
				info_label_5.setFont(INF_FONT_0);
				info_label_5.setHorizontalAlignment(SwingConstants.CENTER);
			info_45.add(info_label_4);
			info_45.add(info_label_5);
			info_block.add(info_01);
			info_block.add(info_23);
			info_block.add(info_45);
			info_block.setPreferredSize(new Dimension((int)(WIDTH/1.5), (int)(IFPANEL_HEIGHT/1.5)));
		info_panel.add(info_block);
		info_panel.setBorder(IF_BORDER);
		info_panel.setPreferredSize(new Dimension((int)(WIDTH/1.25), IFPANEL_HEIGHT));
		
	// input panel:
		// function input
		f_panel = new JPanel();
		f_panel.setLayout(new FlowLayout());
			f_input = new JTextField("x^2", FINPUT_CHARS);
			f_input.setFont(TXT_FONT);
			f_label = new JLabel(FX_STR);
			f_label.setFont(LBL_FONT);
		f_panel.add(f_label);
		f_panel.add(f_input);
		f_panel.setBorder(F_BORDER);
		// xvalue input
		x_panel = new JPanel();
		x_panel.setLayout(new FlowLayout());
			x_input = new JTextField("0", XINPUT_CHARS);
			x_label = new JLabel(X_STR);
			x_label.setFont(LBL_FONT);
		x_panel.add(x_label);
		x_panel.add(x_input);
		x_panel.setBorder(X_BORDER);
		// yvalue input
		y_panel = new JPanel();
		y_panel.setLayout(new FlowLayout());
			y_input = new JTextField("0", XINPUT_CHARS);
			y_label = new JLabel(Y_STR);
			y_label.setFont(LBL_FONT);
		y_panel.add(y_label);
		y_panel.add(y_input);
		y_panel.setBorder(Y_BORDER);
		// zvalue input
		z_panel = new JPanel();
		z_panel.setLayout(new FlowLayout());
			z_input = new JTextField("0", XINPUT_CHARS);
			z_label = new JLabel(Z_STR);
			z_label.setFont(LBL_FONT);
		z_panel.add(z_label);
		z_panel.add(z_input);
		z_panel.setBorder(Z_BORDER);
		// input panel 1: function input
		input_panel_1 = new JPanel();
		input_panel_1.setLayout(new FlowLayout());
		input_panel_1.add(f_panel);
		input_panel_1.setPreferredSize(new Dimension(WIDTH, IPANEL_HEIGHT));
		// input panel 2: variable value input
		input_panel_2 = new JPanel();
		input_panel_2.setLayout(new FlowLayout());
		input_panel_2.add(x_panel);
		input_panel_2.add(y_panel);
		input_panel_2.add(z_panel);
		input_panel_2.setPreferredSize(new Dimension(WIDTH, IPANEL_HEIGHT));
		
	// button panel:
		button_panel = new JPanel();
		button_panel.setLayout(new FlowLayout());
			eval_button = new JButton(BTN_TXT);
			eval_button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
			eval_button.setFont(BTN_FONT);
			eval_button.addActionListener(this);
		button_panel.add(eval_button);
		button_panel.setPreferredSize(new Dimension(WIDTH, BPANEL_HEIGHT));
		
	// result panel:
		result_panel = new JPanel();
		result_panel.setLayout(new FlowLayout());
			result_label = new JLabel(INIT_RESULT);
			result_label.setFont(RES_FONT);
			result_label.setBorder(R_BORDER);
			result_label.setPreferredSize(new Dimension((int)(WIDTH/1.25), RPANEL_HEIGHT));
			result_label.setHorizontalAlignment(SwingConstants.CENTER);
		result_panel.add(result_label);
		result_panel.setPreferredSize(new Dimension(WIDTH, RPANEL_HEIGHT));
		
		
	// contentPane:
		contentPane.setLayout(new GridLayout(5,1));
		contentPane.add(info_panel);
		contentPane.add(input_panel_1);
		contentPane.add(input_panel_2);
		contentPane.add(button_panel);
		contentPane.add(result_panel);
		
		pack();
		setVisible(true);
		
	}
	
// ====================================================

	/**	If ActionEvent source is <code>eval_button</code> (the only button in this GUI), then this will
	 * 	create a new <code>Parser</code> object, supplying it with the String from <code>f_input</code> 
	 * 	and the double-value of the String from <code>x_input</code>.  Then it will perform the 
	 * 	<code>evaluate()</code> method from the <code>Parser</code> class.<br>
	 * 	This method will then set the text String in <code>result_label</code> to the String equivalent of
	 * 	the result from <code>evaluate()</code>
	 * 	If an <code>InvalidParseException</code> is thrown by <code>evaluate()</code>, then it will set
	 * 	the text of <code>result_label</code> to the error message generated by <code>evaluate()</code>.
	 * 
	 */
	public void actionPerformed(ActionEvent ev)
	{
		if (ev.getSource() == eval_button) {
			Parser p = new Parser(f_input.getText(), 	Double.parseDouble(x_input.getText()),
														Double.parseDouble(y_input.getText()),
														Double.parseDouble(z_input.getText()));
			try {
				result_label.setText("Parsing...");
				result = setResultStr(p.evaluate());
				result_label.setFont(RES_FONT);
			} catch (InvalidParseException ex) {
				result = setResultStr(ex.getMessage());
				result_label.setFont(ERR_FONT);
			}
			result_label.setText(result);
		}
	}
	
// ====================================================
// set result string
	
	/**	Returns the String that can be passed to <code>result_label.setText()</code> 
	 * @return <code>Double.toString(r)</code> 
	 */
	private String setResultStr(double r)
	{
		return Double.toString(r);
	}
	/**	Returns the String that can be passed to <code>result_label.setText()</code>, if
	 * 	and error was thrown. 
	 * @return <code>" error: " + err_msg</code> 
	 */
	private String setResultStr(String err_msg)
	{
		return " error: " + err_msg;
	}
}
