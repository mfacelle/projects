package parser;

import parser.exceptions.*;

/** Parser will evaluate a String (as a char[]) as a math expression.
 *  <br>Uses recursive descent parsing.
 *  <br><br><b>Grammar:</b>
 *  <code><pre>
 *  <br> expr   ::= term     | expr + term     | expr - term
 *  <br> term1  ::= factor   | term * factor   | term / factor 
 *  <br> term2  ::= factor   | term ^ factor   | term E factor
 *  <br> factor ::= ( expr ) | number|variable | -factor
 *  </pre></code>
 *  <br><br>
 *  A term1 and term2 are used so that order-of-operations is done correctly
 *  according to PEMDAS. (term2 has a higher priority than term1)<br>
 *  <br>
 *  Version 3.0 fixes issues with parentheses, expressions, and functions:<br>
 *  - (fixed)When using a function, the rest of the string was evaluated as though it were all inside the parentheses.<br>
 *  - (ongoing)End parentheses by themselves are not read.  A user can input (1+2))))) and it will read as (1+2)
 *  
 *  @author Mike Facelle
 *  @date 4/11/14
 *  @version 3.0
 *  
 */
public class Parser
{
	/** The String to be parsed */
    private String function;	
    /** the current index of the String being evaluated */
    private int index;			 
    /** the current token being parsed or evaluated */
    private char token;			 
    /** the x-value at which to parse the String */
    private double x;	
    /** the y-value at which to parse the String */
    private double y;
    /** the z-value at which to parse the String */
    private double z;
    /** the amount of parentheses. if state = 0, then number of <code>'('</code> = <code>')'</code> */
    private int state;			 
    /** To fix bugs, added to determine if "inside" a function or not (like sin, cos) */
    private int inFunction;
    
    /** Initializes a parser with no parameters.
     *  <br>Sets <code>function</code> to <code>"x"</code>.
     *  <br>Takes the value of x to be 0;
     */
    public Parser()
    {
    	this("x");
    }
    /** Initializes a parser with the specified function and no specified x value.
     *  Sets the value of x to 0.<br>
     *  The x value can be set using setXVal().
     *  
     * @param initFunction
     * 			The String to be evaluated as a mathematical expression.
     */
    public Parser(String initFunction)
    {
    	this(initFunction, 0, 0, 0);
    }
    /** Initializes a parser with the specified function and x value
     * 
     * @param initFunction
     * 			The function String to be evaluated as a mathematical expression.
     * @param xVal
     * 			The x value to evaluate the function String at.
     */
    public Parser(String initFunction, double xVal, double yVal, double zVal)
    {
    	function = initFunction;
    	x = xVal;
    	y = yVal;
    	z = zVal;
    	index = 0;
    	state = 0;
    	inFunction = 0;
    }   

    
// ====================================================
// evaluate    
    
    /** Begins the recursive loop of parsing and evaluating the function String, at the
     * 	x value specified in this class.
     *  
     *  @throws InvalidParseException
     *  		When any of the other exceptions in <code>parser.exceptions</code> are thrown.<br>
     *  		When the other exceptions are thrown, this method performs these operations:
     *  		<code>UnbalancedParenthesesException</code>: Adds a <code>')'</code> for each missing
     *  		<code>')'</code> detected in the function String.
     *  		<code>InvalidWordException</code> and <code>InvalidNumberException</code>: 
     *  		Both of these change the function String into <code>"-x"</code>.
     *  
     *  @return the value of the expression for the specified x value
     */
    public double evaluate() throws InvalidParseException
    {
        index = 0;
        state = 0;
        
        Value result = new Value(); // empty constructor initializes to 0.0
        while (true) // for re-doing evaluation after catching an exception
        {
            nextToken(); // check first element
            try {                
                expression(result);
                if (state != 0)  // if parentheses unbalanced
                    throw new UnbalancedParenthesesException();
                break;
            }
            catch (UnbalancedParenthesesException ex) {
                //System.out.println(ex.getMessage());
                //ex.printStackTrace();
            	if (state > 0)
	                for (int i = state; i > 0; i--)
	                    setFunction(function + ")");
                // returns to caller. catching this exception should involve a new call to evaluate() from caller
                throw new InvalidParseException(ex.getMessage());               
            }
            catch (InvalidNumberException ex) {
                //System.out.println(ex.getMessage());
                //ex.printStackTrace();
                setFunction("-x");                
                throw new InvalidParseException(ex.getMessage());
            }
            catch (InvalidWordException ex) {
                //System.out.println(ex.getMessage());
                //ex.printStackTrace();
                setFunction("-x");               
                throw new InvalidParseException(ex.getMessage());
            }
        }
        return result.getValue();
    }
    
    // --------------------------------

    /** Begins the recursive loop of parsing and evaluating the function String, at the
     * 	x value passed to this method.  <br>
     * 	<b>Overwrites the instance variable, x</b>
     * 
     * @param	xx
     * 			The x-value to parse the instance variable <code>function</code> at.
     *  
     *  @throws InvalidParseException
     *  		When any of the other exceptions in <code>parser.exceptions</code> are thrown.<br>
     *  		When the other exceptions are thrown, this method performs these operations:
     *  		<code>UnbalancedParenthesesException</code>: Adds a <code>')'</code> for each missing
     *  		<code>')'</code> detected in the function String.
     *  		<code>InvalidWordException</code> and <code>InvalidNumberException</code>: 
     *  		Both of these change the function String into <code>"-x"</code>.
     *  
     *  @return the value of the expression for the specified x value
     */
    public double evaluate(double xx) throws InvalidParseException
    {
    	setXVal(xx);
    	return evaluate();
    }
    
    // --------------------------------

    /** <b>Static method:</b> can be used without altering any of the class variables.  Creates a 
     *  new instance of the Parser class to do this.
     *  <br>
     * 	Begins the recursive loop of parsing and evaluating the function String passed, at the
     * 	x value passed to it.
     *  
     *  @param f
     *  		The String to be evaluated
     *  @param xx
     *  		The x-value at which to evaluate the String
     *  
     *  @throws InvalidParseException
     *  		When any of the other exceptions in <code>parser.exceptions</code> are thrown.<br>
     *  		When the other exceptions are thrown, this method performs these operations:
     *  		<code>UnbalancedParenthesesException</code>: Adds a <code>')'</code> for each missing
     *  		<code>')'</code> detected in the function String.
     *  		<code>InvalidWordException</code> and <code>InvalidNumberException</code>: 
     *  		Both of these change the function String into <code>"-x"</code>.
     *  
     *  @return the value of the expression for the specified x value
     */
    public static double evaluate(String f, double xx, double yy, double zz) throws InvalidParseException
    {
    	Parser p = new Parser(f, xx, yy, zz);
    	return p.evaluate();
    }
    
 // ====================================================
 // for moving through function string  
    
    /** Sets <code>index</code> back to 0.  Will probably never need to be used, since evaluate()
     * 	resets the index anyway.
     */
    public void resetIndex()
    {
    	index = 0;
    }
    
    // --------------------------------
    
    /** Moves to the next char in the function String.
     *  <br>Modifies <code>index</code> and <code>token</code>, found in this class.
     */
    private void nextToken()
    {
        while (index < function.length() && Character.isWhitespace(function.charAt(index)))
            ++index;
        
        if (index < function.length())
            token = function.charAt(index++);
        else
            token = '\0';
    }
    
 // ====================================================
 // for actual parsing
    
    private void expression(Value result) throws InvalidParseException, InvalidNumberException, InvalidWordException
    {
        term1(result);
        while (token == '+' || token == '-') {
            char op = token;
            Value subresult = new Value();
            nextToken();
            term1(subresult);
            result.setValue(arith(result.getValue(), op, subresult.getValue()));
        }
    }
    
    // --------------------------------

    private void term1(Value result) throws InvalidParseException, InvalidNumberException, InvalidWordException
    {
        term2(result);
        while (token == '*' || token == '/' || token == '%') {
            char op = token;
            Value subresult = new Value();
            nextToken();
            term2(subresult);
            result.setValue(arith(result.getValue(), op, subresult.getValue()));
        }
    }
    
    private void term2(Value result) throws InvalidParseException, InvalidNumberException, InvalidWordException
    {
        factor(result);
        while (token == '^' || token == 'E') {
            char op = token;
            Value subresult = new Value();
            nextToken();
            factor(subresult);
            result.setValue(arith(result.getValue(), op, subresult.getValue()));
        }
    }
    
    // --------------------------------

    private void factor(Value result) throws InvalidParseException, InvalidNumberException, InvalidWordException
    {
        if (token == '(') {
            ++state;
            nextToken();
            expression(result);
        	if (token == ')')
	        	--state;
        }
        else if (token == ')')	// never actually gets called...
        	--state;
        else if (token == '-') {
            Value subresult = new Value();
            nextToken();
            factor(subresult);
            --index; // so call of nextToken() returns the current token (to fix not-reading-end-parentheses glitch)
            result.setValue(subresult.getValue() * -1);
        }
        else if ((token >= '0' && token <= '9') || isSpecialChar(token))
            number(result);
        else if (isLetter(token))
            read(result);
        else
            throw new InvalidNumberException("Invalid character entered");
        nextToken();
    }
    
 // ====================================================
 // for reading numbers and words  
    
    /** Gets the value of the number currently being parsed in the function String.
     * 
     * @param result
     * 			The current value of the function at the x specified in this class.
     * 
     * @throws InvalidNumberException
     * 			When the number being read contains a character that is not a number or valid
     * 			parsing character, such as P (pi) or E (e).
     * 
     * @return Void, because it uses setValue() to set the value in result rather than passing back
     * 			a returned value.
     */
    private void number(Value result) throws InvalidNumberException, InvalidWordException
    {    
        int integer = 0;
        double decimal = 0;
        int power = 1;  			// for calculating decimal powers to multiply by (1/10, 1/100, etc)
        boolean integers = true;	// whether or not the number is calculating integer or decimal places
        int hasX = 0, hasY = 0, hasZ = 0;
        int hasPi = 0;
        int hasE = 0;
        
        while ((token >= '0' && token <= '9') || isSpecialChar(token))
        {   
            if (token == '.') {
            	if (integers == false) // if decimal already found
            		throw new InvalidNumberException("Multiple decimal places");
                integers = false;
            }
            else if (integers && !isSpecialChar(token)) {
            	// numbers cannot follow a variable/constant:
            	if (hasX > 0 || hasPi > 0 || hasE > 0)
            		throw new InvalidNumberException();
                // calculates integer portion
                integer = integer*10 + (token - '0');
            }
            else if (!integers && !isSpecialChar(token)) {
            	// numbers cannot follow a variable/constant:
            	if (hasX > 0 || hasY > 0 || hasZ > 0 || hasPi > 0 || hasE > 0)
            		throw new InvalidNumberException();
                // calculates decimal portion
                decimal = decimal + ((token - '0') * (1 / Math.pow(10, power++)));
            }
            else if (token == 'x')
                hasX++;
            else if (token == 'y')
                hasY++;
            else if (token == 'z')
                hasZ++;
            else if (token == 'p')
                hasPi++;
            else if (token == 'e')
                hasE++;
            else
                throw new InvalidNumberException();
                        	
            nextToken();
        }
        
        result.setValue(integer + decimal);
        
        // include x, pi, and e
        for (int i = hasX; i > 0; i--) {
            if (result.getValue() == 0)
                result.setValue(1); // so a number with no 0-9 digits wont always be 0
            result.setValue(result.getValue() * x);
        }
        for (int i = hasY; i > 0; i--) {
            if (result.getValue() == 0)
                result.setValue(1); // so a number with no 0-9 digits wont always be 0
            result.setValue(result.getValue() * y);
        }
        for (int i = hasZ; i > 0; i--) {
            if (result.getValue() == 0)
                result.setValue(1); // so a number with no 0-9 digits wont always be 0
            result.setValue(result.getValue() * z);
        }
        for (int j = hasPi; j > 0; j--) {
            if (result.getValue() == 0)
                result.setValue(1); // so a number with no 0-9 digits wont always be 0
            result.setValue(result.getValue() * Math.PI);
        }
        for (int k = hasE; k > 0; k--) {
            if (result.getValue() == 0)
                result.setValue(1); // so a number with no 0-9 digits wont always be 0
            result.setValue(result.getValue() * Math.E);
        }
        
        --index; //so call of nextToken() in factor() will return the current token
        //nextToken();
    }
    
    // --------------------------------
    
    /** "Reads" the function String at the token being parsed for a word that corresponds
     * 	to a mathematical function, such as sin or cos.<br><br>
     * 	Supported functions:<br><code><pre>
     * 		sin	 : sine<br>
     * 		cos  : cosine<br>
     * 		tan  : tangent<br>
     * 		ln	 : natural log<br>
     * 		abs	 : absolute value<br>
     * 		sqrt : square root<br>
     * </pre></code>
     * <br><br>
     * <i>There's probably a more efficient way than handling these as all char[]...</i>
     * <br><br>
     * 	This method reads the word entered, and if it's valid it takes the substring of
     * 	chars between the parentheses and evaluates them.  This prevents the bug where
     *  everything after this function became evaluated as the argument (due to the nature of
     *  expression()).<br>
     * 
     * @param result
     * 			The current value of the function at the x specified in this class.
     * 
     * @throws InvalidWordException
     * 			When the word being parsed is not a valid parsing word.<br>
     * 			For example: <code>"sim"</code> instead of <code>"sin"</code>.
     * @throws InvalidNumberException
     * 			Can be thrown after parsing the argument of a word, if the argument contains a number
     * 			with an invalid digit or character.
     * @throws InvalidParseException
     * 			Thrown if the argument to the function is an invalid function string
     * 
     * @return Void, because it uses setValue() to set the value in result rather than passing back
     * 			a returned value.
     */
    private void read(Value result) throws InvalidWordException, InvalidNumberException, InvalidParseException //invalid number from call of expression()
    {
    	// get the word
        StringBuffer func = new StringBuffer("");  // to hold the word      
        for (int i = 0; isLetter(token) && i < function.length(); i++, nextToken())
            func.append(token);
        
        // evaluate the argument substring, regardless of if it's a valid function yet
        // don't have to rewrite it under each if statement that way
        // it'll take more time if it's invalid, but it throws an error if invalid anyway,
        // so the extra time won't mean much since it fails.
        int argStartIndex = index;
        int argEndIndex = 0;		
        // find when the argument string ends (the ')' char)
        for (; token != ')'; nextToken());	// go to next ')'
        argEndIndex = index;
        Value subresult = new Value();
        try {
        	subresult.setValue(evaluate(function.substring(argStartIndex, argEndIndex), x, y, z));
        }
        catch (InvalidParseException ex) {
        	throw new InvalidParseException("Argument to function " + func + " could not be parsed");
        }
        
        // check array to see if it matches known function:
        // sin
        if (func.indexOf("sin") != -1)
            result.setValue(Math.sin(subresult.getValue()));
        // cos
        else if (func.indexOf("cos") != -1)
            result.setValue(Math.cos(subresult.getValue()));
        // tan
        else if (func.indexOf("tan") != -1)
            result.setValue(Math.tan(subresult.getValue()));
        // ln
        else if (func.indexOf("ln") != -1)
            result.setValue(Math.log(subresult.getValue()));
        // abs value
        else if (func.indexOf("abs") != -1)
            result.setValue(Math.abs(subresult.getValue()));
        // square root
        else if (func.indexOf("sqrt") != -1)
            // if value under radical is negative, it returns "NaN", so no exception needed
            result.setValue(Math.sqrt(subresult.getValue()));
        else
            throw new InvalidWordException();        
    }
    
    
    
    // --------------------------------
     
    /** Performs math operation on two operands with an operator: +, -, *, /, %, ^
     * 
     * @param lhs
     * 			The (double-value) left-hand-side operand of the operation to be performed
     * @param op
     * 			A char corresponding to the mathematical operation to be performed.
     * @param rhs
     * 			The (double-value) right-hand-side operand of the operation to be performed
     * 
     * @return the double value of the result of the two operands and operation token
     */
    private double arith(double lhs, char op, double rhs)
    {
        switch (op) {
            case '+': return lhs + rhs;
            case '-': return lhs - rhs;
            case '*': return lhs * rhs;
            case '/': return lhs / rhs;
            case '%': return lhs % rhs;
            case '^': return Math.pow(lhs, rhs);
            case 'E': return lhs * Math.pow(10, rhs);
            default: return 0;
        }
    }

// ====================================================
    
    /** Determines if the token is a letter, ONLY lower case allowed
     * @return whether of not the token passed is a valid lower-case letter.
     */
    public static boolean isLetter(char token)
    {
        return token >= 'a' && token <= 'z' ? true : false;
    }
    
    /** Determines if the token is a special character reserved for constants,
     * 	variables, and the scientific notation exponential operator, <code>E</code>.
     * 	They are not used in any mathematical functions.
     * <br>
     * 	These are: <code>'x'</code>, <code>'y'</code>, <code>'z'</code>, 
     * 	<code>'p'</code>, and <code>'e'</code>
     * @return whether of not the token passed is a special character.
     */
    public static boolean isSpecialChar(char c)
    {
    	return c == 'x' || c == 'y' || c == 'z' || c == 'p' || c == 'e' || c == '.';
    }

// ====================================================
// Get and Sets
    
    public String getFunction()	{ return function; }
    public double getXVal()		{ return x;}
    public double getYVal()		{ return y;}
    public double getZVal()		{ return z;}

    public void setFunction(String f) 	{ function = f; }
    public void setXVal(double xx)		{ x = xx; }
    public void setYVal(double yy)		{ y = yy; }
    public void setZVal(double zz)		{ z = zz; }
}
        