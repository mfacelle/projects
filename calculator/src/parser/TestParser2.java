package parser;

import java.util.Scanner;
import parser.exceptions.*;

public class TestParser2 
{
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		Parser p = new Parser();
		double result = 0;
		
		while (true) {
			System.out.println("Enter function to parse ('exit' to close)");
			String func = scan.nextLine();
			if (func.equalsIgnoreCase("exit"))
				break;
			System.out.println("Enter x-val");
			double xval = Double.parseDouble(scan.nextLine());
			p.setFunction(func);
			p.setXVal(xval);
			
			try {
				result = p.evaluate();
			} catch (InvalidParseException ex) {
				System.out.println(ex.getMessage());
				System.out.println("Parsing failed.");
				continue;
			}
			
			System.out.println(func + " [at " + xval + "] = " + result);	
		}
		
		System.out.println("done");
	}
}
