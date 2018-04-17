package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static int endIndex = 0;
	public static boolean isArray = false;
	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	expr = noSpaces(expr);
    	String temp = "";
    	for(int x = 0; x<expr.length();x++)
    	{
    		if(x == expr.length()-1)
    		{
    			if(expr.charAt(x)!='+'&&expr.charAt(x)!='-'&&expr.charAt(x)!='/'&&expr.charAt(x)!='*'&&
        				expr.charAt(x)!=')'&&expr.charAt(x)!='('&&expr.charAt(x)!=']'&&expr.charAt(x)!='0'
        				&&expr.charAt(x)!='1'&&expr.charAt(x)!='2'&&expr.charAt(x)!='3'&&expr.charAt(x)!='4'
        				&&expr.charAt(x)!='5'&&expr.charAt(x)!='6'&&expr.charAt(x)!='7'&&expr.charAt(x)!='8'
        				&&expr.charAt(x)!='9')
    			{
    				temp+=expr.charAt(x);
    				if(doesVarExist(temp,vars)==false)
    				{
    				Variable first = new Variable(temp);
        			vars.add(first);
    				}
    				
    			}
    			else if(expr.charAt(x)=='+'||expr.charAt(x)=='-'||expr.charAt(x)=='/'||expr.charAt(x)=='*'||
        				expr.charAt(x)==')'||expr.charAt(x)=='('||expr.charAt(x)==']'||expr.charAt(x)=='0'
        				||expr.charAt(x)=='1'||expr.charAt(x)=='2'||expr.charAt(x)=='3'||expr.charAt(x)=='4'
        				||expr.charAt(x)=='5'||expr.charAt(x)=='6'||expr.charAt(x)=='7'||expr.charAt(x)=='8'
        				||expr.charAt(x)=='9')
        		{
        			if(temp!=""&&temp!=null&&doesVarExist(temp,vars)==false)
        			{
        			Variable first = new Variable(temp);
        			vars.add(first);
        			temp = "";
        			}
        			else
        			{
        				temp = "";
        			}
        		}	
    		}
    		else if(expr.charAt(x)=='+'||expr.charAt(x)=='-'||expr.charAt(x)=='/'||expr.charAt(x)=='*'||
    				expr.charAt(x)==')'||expr.charAt(x)=='('||expr.charAt(x)==']'||expr.charAt(x)=='0'
    				||expr.charAt(x)=='1'||expr.charAt(x)=='2'||expr.charAt(x)=='3'||expr.charAt(x)=='4'
    				||expr.charAt(x)=='5'||expr.charAt(x)=='6'||expr.charAt(x)=='7'||expr.charAt(x)=='8'
    				||expr.charAt(x)=='9')
    		{
    			if(temp!=""&&temp!=null&&doesVarExist(temp,vars)==false)
    			{
    			Variable first = new Variable(temp);
    			vars.add(first);
    			temp = "";
    			}
    			else
    			{
    				temp = "";
    			}
    		}
    		else if (expr.charAt(x)=='[')
    		{
    			if(temp!=""&&temp!=null&&doesArrayExist(temp,arrays)==false)
    			{
    			Array first = new Array(temp);
    			arrays.add(first);
    			temp="";
    			}
    			else
    			{
    				temp = "";
    			}
    		}
    		else
    		{
    			temp+=expr.charAt(x);
    		}
    	}
    }
    
    private static String noSpaces(String str)
    {
    	if(str.length()==1)
    	{
    		if(str.charAt(0)==' ')
    		{
    			return "";
    		}
    		else
    		{
    			return str;
    		}
    	}
    	if(str.charAt(0)==' ')
    	{
    		return noSpaces(str.substring(1,str.length()));
    	}
    	else
    	{
    		return str.charAt(0)+noSpaces(str.substring(1,str.length()));
    	}
   
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	expr = noSpaces(expr);
    	Stack operator = new Stack();
    	Stack operand = new Stack();
    	for(int x= 0;x<expr.length();x++)
    	{
    		if(expr.charAt(x)=='(')
    		{
    			x++;
    			int y = findParanthesisIndex(expr.substring(x))+x;
    			System.out.println("ParIndexes: "+x+","+y);
    			float z = evaluate(expr.substring(x,y),vars,arrays);
    			System.out.println("Parenthesis value: "+z);
    			operand.push(Float.toString(z));
    			x = y;
    		}
    		if(expr.charAt(x)>='0'&&expr.charAt(x)<='9')
    		{
    			String number = getNumber(expr.substring(x));
    			x+=endIndex-1;
    			System.out.println("endIndex: "+endIndex);
    			System.out.println(number+"*");
    			operand.push(number);
    		}
    		else if((expr.charAt(x)>='a'&&expr.charAt(x)<='z')||(expr.charAt(x)>='A'&&expr.charAt(x)<='Z'))
    		{
    			String variable = getVariable(expr.substring(x));
    			if(isArray==false)
    			{
    			x+=endIndex-1;
    			System.out.println("endIndex: "+endIndex);
    			System.out.println(variable);
    			operand.push(variable);
    			}
    			else
    			{
    				System.out.println(endIndex+ " End Index");
    				x+=endIndex+1;
    				int y = findArrayIndex(expr.substring(x))+x;
    				int arrayIndex = findArray(variable,arrays);
    				System.out.println("array Index: "+arrayIndex+ " ----> Indexes: "+x+","+y);
    				isArray=false;
    				int z = (arrays.get(arrayIndex).values[(int)(evaluate(expr.substring(x,y),vars,arrays))]);
    				System.out.println("ArrayValue Pushed: "+ z);
    				operand.push(Integer.toString(z));
    				x=y;
    			}
    		}
    		else if(expr.charAt(x)=='-'||expr.charAt(x)=='+'||expr.charAt(x)=='*'||expr.charAt(x)=='/')
    		{
    			if(operator.isEmpty())
    			{
    				operator.push(expr.charAt(x));
    			}
    			else
    			{
    				char current = expr.charAt(x);
    				char previous = (char)operator.pop();
    				if(isHigher(previous,current)==true)
    				{
    					String firstVariable = (String)operand.pop();
    					String secondVariable = (String)operand.pop();
    					float firstValue = 0;
    					float secondValue = 0;
    					
    					if(firstVariable.charAt(0)>='0'&&firstVariable.charAt(0)<='9')
    						firstValue = Float.parseFloat(firstVariable);
    					else
    						firstValue = findValue(firstVariable,vars);
    					if(secondVariable.charAt(0)>='0'&&secondVariable.charAt(0)<='9')
    						secondValue = Float.parseFloat(secondVariable);
    					else
    						secondValue = findValue(secondVariable,vars);
    					float result = calculate(firstValue,secondValue,previous);
    					System.out.println(result);
    					operand.push(Float.toString(result));
    					operator.push(current);
    				}
    				else
    				{
    					
    					operator.push(previous);
    					operator.push(current);
    				}
    			}
    		}
    	}
    	while(true)
    	{
    		if(operator.size()==0)
    		{
    			break;
    		}
    		char operation = (char) operator.pop();
    		String firstVariable = (String)operand.pop();
    		String secondVariable = (String) operand.pop();
    		float firstValue=0;
    		float secondValue=0;
    		String result="";
    		if(firstVariable.charAt(0)>='0'&&firstVariable.charAt(0)<='9')
				firstValue = Float.parseFloat(firstVariable);
			else
				firstValue = findValue(firstVariable,vars);
			if(secondVariable.charAt(0)>='0'&&secondVariable.charAt(0)<='9')
				secondValue = Float.parseFloat(secondVariable);
			else
				secondValue = findValue(secondVariable,vars);
			float value = calculate(firstValue,secondValue,operation);
    		result = Float.toString(value);
    		System.out.println(result);
    		operand.push(result);
    	}
    	String finalAnswer=(String)operand.pop();
    	if((finalAnswer.charAt(0)>='a'&&finalAnswer.charAt(0)<='z')||(finalAnswer.charAt(0)>='A'&&finalAnswer.charAt(0)<='Z'))
    	{
    		return findValue(finalAnswer,vars);
    	}
    	return Float.parseFloat(finalAnswer);
    }
    
    public static Stack printStackChar(Stack stack)
    {
    	char data;
    	Stack temp = new Stack();
    	while(stack.isEmpty()!=true)
    	{
    		data = (char)stack.pop();
    		System.out.print(data+" --> ");
    		temp.push(data);
    	}
    	System.out.println();
    	while(temp.isEmpty()!=true)
    	{
    		stack.push(temp.pop());
    	}
    	return stack;
    }
    
    public static float calculate(float first, float second, char operation)
    {
    	float finalValue= 0;
    	if(operation=='+')
    	{
    		finalValue = second+first;
    	}
    	if(operation=='-')
    	{
    		finalValue = second-first;
    	}
    	if(operation=='*')
    	{
    		finalValue = second*first;
    	}
    	if(operation=='/')
    	{
    		finalValue = second/first;
    	}
    	return finalValue;
    }
    
    public static boolean doesArrayExist(String variable, ArrayList<Array>arrays)
    {
    	boolean doesExist = false;
    	for(int x= 0;x<arrays.size();x++)
    	{
    		if(arrays.get(x).name.equals(variable))
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    public static boolean doesVarExist(String variable,ArrayList <Variable>vars)
    {
    	boolean doesExist = false;
    	for(int x= 0;x<vars.size();x++)
    	{
    		if(vars.get(x).name.equals(variable))
    		{
    			return true;
    		}
    	}
    	return false;
    }
    

    public static float findValue(String variable,ArrayList<Variable>vars)
    {
    	int temp = 0;
    	for(int x = 0;x<vars.size();x++)
    	{
    		if(vars.get(x).name.equals(variable))
    		{
    			temp = vars.get(x).value;
    		}
    	}
    	return temp;
    }

    public static int findArray(String array, ArrayList<Array>arrays)
    {
    	
    	for(int x =0;x<arrays.size();x++)
    	{
    		if(array.equals(arrays.get(x).name))
    		{
    			return x;
    		}
    	}
    	return 0;
    }
    
    public static String getNumber(String expr)
    {
    	String temp = "";
    	endIndex=0;
    	for(int x =0;x<expr.length();x++)
    	{
    		
    		if(expr.charAt(x)=='+'||expr.charAt(x)=='-'||expr.charAt(x)=='/'||expr.charAt(x)=='*'||
    			expr.charAt(x)==')'||expr.charAt(x)=='('||(expr.charAt(x)>='a'&&expr.charAt(x)<='z')||
    			(expr.charAt(x)>='A'&&expr.charAt(x)<='Z'||expr.charAt(x)==']'))
    		{
    			endIndex = x;
    			break;
    		}
    		endIndex++;
    		temp+=expr.charAt(x);
    	}
    	return temp;
    }
    
    public static String getVariable(String expr) {
    	String temp = "";
    	for(int x =0;x<expr.length();x++)
    	{
    		if(expr.charAt(x)=='+'||expr.charAt(x)=='-'||expr.charAt(x)=='/'||expr.charAt(x)=='*'||
    				expr.charAt(x)==')'||expr.charAt(x)=='('||expr.charAt(x)==']'||expr.charAt(x)=='0'
    				||expr.charAt(x)=='1'||expr.charAt(x)=='2'||expr.charAt(x)=='3'||expr.charAt(x)=='4'
    				||expr.charAt(x)=='5'||expr.charAt(x)=='6'||expr.charAt(x)=='7'||expr.charAt(x)=='8'
    				||expr.charAt(x)=='9')
    		{
    			endIndex = x;
    			isArray=false;
    			break;
    		}
    		if(expr.charAt(x)=='[')
    		{
    			endIndex = x;
    			isArray = true;
    			break;
    		}
    		endIndex++;
    		temp+=expr.charAt(x);
    	}
    	return temp;
    }
    
    public static int findArrayIndex(String expr)
    {
    	int count = 0;
    	int nestedArray = 0;
    	boolean hasNestedArray=false;
    	for(int x = 0;x<expr.length();x++)
    	{
    		if(nestedArray<=0)
    		{
    			hasNestedArray = false;
    		}
    		if(nestedArray>0)
    		{
    			hasNestedArray = true;
    		}
    		if(expr.charAt(x)=='[')
    		{
    			nestedArray++;
    		}
    		if(expr.charAt(x)==']')
    		{
    			nestedArray--;
    		}
    		if(expr.charAt(x)==']'&&hasNestedArray == false)
    		{
    			count = x;
    			break;
    		}
    	}
    	return count;
    }
    
    public static int findParanthesisIndex(String expr)
    {
    	int count = 0;
    	int nestedPar =0;
    	boolean hasNestedPar = false;
    	for(int x = 0;x<expr.length();x++)
    	{
    		if(nestedPar<=0)
    		{
    			hasNestedPar = false;
    		}
    		if(nestedPar>0)
    		{
    			hasNestedPar = true;
    		}
    		if(expr.charAt(x)=='(')
    		{
    			nestedPar++;
    		}
    		if(expr.charAt(x)==')')
    		{
    			nestedPar--;
    		}
    		if(expr.charAt(x)==')'&&hasNestedPar == false)
    		{
    			count = x;
    			break;
    		}
    	}
    	return count;
    }
    
    public static boolean isHigher(char first, char second)
    {
    	int firstValue=0;
    	int secondValue=0;
    	if(first=='*')
    	{
    		firstValue=4;
    	}
    	if(first=='/')
    	{
    		firstValue = 4;
    	}
    	if(first=='+')
    	{
    		firstValue = 2;
    	}
    	if(first=='-')
    	{
    		firstValue = 3;
    	}
    	if(second=='*')
    	{
    		secondValue=4;
    	}
    	if(second=='/')
    	{
    		secondValue = 4;
    	}
    	if(second=='+')
    	{
    		secondValue = 2;
    	}
    	if(second=='-')
    	{
    		secondValue = 3;
    	}
    	if(firstValue>secondValue||firstValue==secondValue)
    	{
    		return true;
    	}
    	return false;
    }
    
}
