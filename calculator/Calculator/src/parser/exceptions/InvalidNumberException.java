package parser.exceptions;

/** This exception is thrown when the String to be parsed contains an invalid character
 *  intended to be used as a number.  If the character is lower-case, InvalidWordException will be thrown
 *  because the parser will attempt to read it as a word, not a number.
 *  <br>
 *  <i>For example:</i> using a character other than <code>x</code>, <code>P</code>, or <code>E</code>
 *  because these are the only valid non-numeric characters accepted (other than words).
 * 
 * @author Mike Facelle
 *
 */
public class InvalidNumberException extends Exception
{
	static final long serialVersionUID = 0x000010;
	
	public static final String str = "Invalid number entered.";
	
    public InvalidNumberException()
    {
        super(str);
    }
    public InvalidNumberException(String msg)
    {
        super(msg);
    }
}