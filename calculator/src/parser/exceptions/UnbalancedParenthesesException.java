package parser.exceptions;

/** This exception is thrown when the parentheses in the String to be parsed are unbalanced.
 * 	<br>
 * 	This occurs when the number of <code>'('</code> aren't equal to the number of <code>')'<code>.
 * 
 * @author Mike Facelle
 *
 */
public class UnbalancedParenthesesException extends Exception
{
	static final long serialVersionUID = 0x000020;
	
	public static final String str = "Unbalanced Parentheses.";
	
    public UnbalancedParenthesesException()
    {
        super(str);
    }
    public UnbalancedParenthesesException(String msg)
    {
        super(msg);
    }
}