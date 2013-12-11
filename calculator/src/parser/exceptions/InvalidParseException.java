package parser.exceptions;

/** This exception is thrown from the Parser class whenever any of the other exceptions in
 *  <code>parser.exceptions</code> are thrown.  The class that creates Parser and calls evaluate(String)
 *  must catch this exception and deal with it accordingly.
 *  <br><br>
 *  Within the Parser class, each exception changes the function String stored inside it like so:
 *  <br>
 *  <code>UnbalancedParenthesesException</code>: Adds a <code>')'</code> for each missing <code>')'</code>
 *  detected in the function String.
 *  <code>InvalidWordException</code> and <code>InvalidNumberException</code>: Both of these change
 *  the function String into <code>"-x"</code>.
 * 
 * @author Mike Facelle
 *
 */
public class InvalidParseException extends Exception
{
	static final long serialVersionUID = 0x000000;
	
	public static final String str = "Parsing failed";
	
    public InvalidParseException()
    {
        super(str + ".");
    }
    public InvalidParseException(String msg)
    {
        super(str + " : " + msg);
    }
}