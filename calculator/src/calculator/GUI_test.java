package calculator;

import javax.swing.*;
import javax.swing.border.*;

import parser.Parser;
import parser.exceptions.InvalidParseException;

import java.awt.*;
import java.awt.event.*;

public class GUI_test extends JFrame implements ActionListener
{
	public static final long serialVersionUID = 0x10000;
	
	// sizes of panels
	public static final int WIDTH = 700;
	public static final int IPANEL_HEIGHT = 75;
	public static final int BPANEL_HEIGHT = 125;
	public static final int RPANEL_HEIGHT = 50;
	public static final int BUTTON_WIDTH = 200;
	public static final int BUTTON_HEIGHT = 50;
	
	// initial strings and labels:
	public static final String INIT_FUNC = "x^2";
	public static final String FX_STR = " f(x) = ";
	public static final String INIT_X = "0";
	public static final String X_STR = " x : ";
	public static final String BTN_TXT = "Evaluate";
	public static final String INIT_RESULT = "0.0";
	
	// fonts:
	public static final Font LBL_FONT = new Font(Font.MONOSPACED, 0, 12);
	public static final Font TXT_FONT = new Font(Font.MONOSPACED, 0, 14);
	public static final Font BTN_FONT = new Font(Font.MONOSPACED, 0, 14);
	public static final Font RES_FONT = new Font(Font.MONOSPACED, Font.BOLD, 32);
	public static final Font ERR_FONT = new Font(Font.MONOSPACED, 0, 12);
	
	// borders:
	public static final Border I_BORDER = BorderFactory.createLineBorder(Color.BLACK, 1);
	public static final Border R_BORDER = BorderFactory.createLineBorder(Color.BLACK, 2);

	
	// variables (for parsing)
	private String function;	// function string from f_input textfield
	private String x_val;		// x value string from x_input textfield
	private String result;		// result string displayed on result_label
	
	// hierarchy of containers:
	private Container contentPane;
	
	private JPanel input_panel;
		private JLabel f_label, x_label;
		private JPanel f_panel, x_panel;
			private JTextField f_input, x_input;
	private JPanel button_panel;
		private JButton eval_button;
	private JPanel result_panel;
		private JLabel result_label;
	
	// ---------------------
	
	public GUI_test()
	{
		super();
		setTitle("Test GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(WIDTH, IPANEL_HEIGHT + BPANEL_HEIGHT + RPANEL_HEIGHT));
		
		contentPane = getContentPane();
		
	// input panel:
		// function input
		f_panel = new JPanel();
		f_panel.setLayout(new FlowLayout());
			f_input = new JTextField("x^2", 40);
			f_input.setFont(TXT_FONT);
			f_label = new JLabel(FX_STR);
			f_label.setFont(LBL_FONT);
		f_panel.add(f_label);
		f_panel.add(f_input);
		f_panel.setBorder(I_BORDER);
		// xvalue input
		x_panel = new JPanel();
		x_panel.setLayout(new FlowLayout());
			x_input = new JTextField("0", 10);
			x_label = new JLabel(X_STR);
			x_label.setFont(LBL_FONT);
		x_panel.add(x_label);
		x_panel.add(x_input);
		x_panel.setBorder(I_BORDER);
		// input panel
		input_panel = new JPanel();
		input_panel.setLayout(new FlowLayout());
		input_panel.add(f_panel);
		input_panel.add(x_panel);
		input_panel.setPreferredSize(new Dimension(WIDTH, IPANEL_HEIGHT));
		
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
			result_label.setPreferredSize(new Dimension(WIDTH/2, RPANEL_HEIGHT));
			result_label.setHorizontalAlignment(SwingConstants.CENTER);
		result_panel.add(result_label);
		result_panel.setPreferredSize(new Dimension(WIDTH, RPANEL_HEIGHT));
		
		
	// contentPane:
		contentPane.setLayout(new GridLayout(3,1));
		contentPane.add(input_panel);
		contentPane.add(button_panel);
		contentPane.add(result_panel);
		
		pack();
		setVisible(true);
		
	}
	
	// ---------------------

	public void actionPerformed(ActionEvent ev)
	{
		if (ev.getSource() == eval_button) {
			Parser p = new Parser(f_input.getText(), Double.parseDouble(x_input.getText()), 0, 0);
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
	
	// ---------------------
	// set result string
	
	private String setResultStr(double r)
	{
		return Double.toString(r);
	}
	private String setResultStr(String err_msg)
	{
		return " error: " + err_msg;
	}
}
