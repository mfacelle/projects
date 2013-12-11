package parser;

import parser.exceptions.*;

/** A class to test the Parser class.  Initializes a few Parsers and Strings to be evaluated.
 * 	<br>
 *  <b>Class tested: Parser works correctly.</b>
 * 
 * @author Mike Facelle
 *
 */
public class TestParser 
{
	private static final String F1 = "x^2";
	private static final String F2 = "x + 1";
	private static final String F3 = "2 / (x+3)";
	private static final String F4 = "sin(x)";
	
	private static final double X0 = 0.0;
	private static final double X1 = 1.0;
	private static final double X2 = 2.0;
	private static final double X3 = 3.0;
	
	public static void main(String[] args)
	{
		Parser p1 = new Parser(F1, X2);	// full constructor
		Parser p2 = new Parser(F2);		// String-only constructor
		p2.setXVal(X1);
		Parser p3 = new Parser();		// no-parameter constructor
		p3.setFunction(F3);
		p3.setXVal(X3);
		
		try
		{
			System.out.print("p1(" + p1.getXVal() + "): " + p1.getFunction() + " = ");
			System.out.println(p1.evaluate());				// fully constructed
			System.out.print("p2(" + p2.getXVal() + "): " + p2.getFunction() + " = ");
			System.out.println(p2.evaluate());				// String-only constructed
			System.out.print("p3(" + p3.getXVal() + "): " + p3.getFunction() + " = ");
			System.out.println(p3.evaluate());				// no-parameter constructed
			System.out.print("Static(" + X0 + "): " + F4 + " = ");
			System.out.println(Parser.evaluate(F4, X0));	// static method test
			p1.setFunction(F2);
			p1.setXVal(X2);
			System.out.print("p1(" + p1.getXVal() + "): " + p1.getFunction() + " = ");
			System.out.println(p1.evaluate());
		}
		catch (InvalidParseException ex)
		{
			System.out.println("Invalid Parse. Error should have been displayed");
		}	
	}
}
