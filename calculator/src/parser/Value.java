package parser;

/** Value is a class used to carry a value through a String being evaluated.
 *
 * @author http://www.programmersheaven.com/mb/java/83603/83603/evaluating-a-string-as-a-math-function/
 */
class Value
{
    private double value;   

    public Value(double value)
    {
        setValue(value);
    }
    public Value()
    {
        this(0.0);
    }
    
    public void setValue(double value)
    {
        this.value = value;
    }
    
    public double getValue()
    {
        return value;
    }
}