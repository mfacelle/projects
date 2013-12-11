package parser.exceptions;

/** This exception is thrown when the String to be parsed uses a word that is not defined
 *  inside the parser.
 *  <br>
 *  <i>For example:</i> using <code>sim</code> instead of <code>sin</code> for the sine function.
 * 
 * @author Mike Facelle
 *
 */
public class InvalidWordException extends Exception
{
	static final long serialVersionUID = 0x000011;
	
	public static final String str = "Invalid word entered.";
	
    public InvalidWordException()
    {
        super(str);
    }
    public InvalidWordException(String msg)
    {
        super(msg);
    }
}