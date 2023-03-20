import absyn.*;
import java.util.HashMap;
import java.util.Stack;
import java.util.Iterator;
import java.util.ArrayList;

/* TO-DO 
 1. If a block scope is recognized, but contains no variable declarations. Then an Entering and Leaving a block will be printed with empty content.
 2. Get the type of the Exp for type checking.
 3. Look over function undefined() to check if this is a necessary function
*/

public class SemanticAnalyzer implements AbsynVisitor {

    public HashMap<String, ArrayList<NodeType>> symbolTable;
    public Stack<String> stack;
    public int nest = 0;

    final static int SPACES = 4;

    public SemanticAnalyzer() {
        symbolTable = new HashMap<String, ArrayList<NodeType>>();
        stack = new Stack<String>();
        stack.add("global");
    }

    private void indent( int level ) {
        for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
    }

    public void insert(String key, NodeType node) {
        ArrayList<NodeType> nodeList = symbolTable.get(key);

        if(nodeList == null) {
            nodeList = new ArrayList<NodeType>();
            nodeList.add(node);
            symbolTable.put(stack.peek(), nodeList);
        } else { 
            if(!nodeList.contains(node)) 
                nodeList.add(node);
        }
    }

    public NodeType lookup(String name, int row, int col) {
        NodeType node = nodeExists(name);

        return node;
    }
    
    public NodeType nodeExists(String name){
        // If stack is not currently global, then don't check global. Else, check global
        Iterator<String> scope = stack.iterator();

        if (!stack.peek().equals("global"))
            scope.next();

        while( scope.hasNext() ) {
            ArrayList<NodeType> list = symbolTable.get(scope.next());
            if(list != null) {
                for(int i = 0; i < list.size(); i++) {
                    if(list.get(i).name.equals(name)) {
                        return list.get(i);
                    }
                }
            }
        }
        return null;
    }

    public boolean isDeclared(String name, String type, int row, int col){
        NodeType node = lookup(name, row, col);
        if (node == null)
            return false;
        System.err.println("Error in line " + row + ", column " + col + ": " + type + " Redeclaration");
        System.err.println(type + " " + name + " has already been declared on line " + (node.def.row + 1) + ", column " + (node.def.col + 1) + "\n");
        return true;
    }

    public boolean isDefined(String name, int row, int col){
        NodeType node = lookup(name, row, col);
        if (node != null)
            return false;
        return true;
    }

    public void delete() {
        /* Delete local scopes from table */
    }

    public void printSymbolTable( int level ) {
        ArrayList<NodeType> scope = symbolTable.get(stack.peek());
        if(scope != null) {
            for( int i = 0; i < scope.size(); i++ ) {
                indent( level );
                System.out.println(scope.get(i).name + ": " + scope.get(i).def.toString());
            }
        }
    }

    public void visit( ArrayDec dec, int level ) {
        if(isDeclared(dec.name, "Array variable", dec.row+1, dec.col+1) == false) {
            if (dec.typ.typ == 2){
                System.err.println("Error in line " + (dec.row + 1) + ", column " + (dec.col + 1) + ": Invalid Array Variable Declaration Type (VOID)");
                System.err.println("Instead expected type (BOOL, INT) got VOID. Changing VOID -> INT\n");
                dec.typ.typ = 1;
            }
            NodeType symbol = new NodeType(dec.name, dec, level);
            insert(stack.peek(), symbol);
        }
    }

    public void visit ( AssignExp exp, int level ) {
        System.err.println("AssignExp");
        exp.lhs.accept( this, level );
        exp.rhs.accept( this, level );
        
        NodeType lhs = nodeExists(((SimpleVar) exp.lhs.variable).name);
        
        //isDefined(lhs.name, "Variable", lhs.row, lhs.col);
        System.err.println("VAR " + lhs.name);
        System.err.println("Type is: " + lhs.def.getType());

    }

    public void visit ( BoolExp exp, int level ) {
        System.err.println("Bool Exp");
    }

    public void visit ( CallExp exp, int level ) {
        System.err.println("CallExp");
        if (exp.args != null)
            exp.args.accept(this, level);
    }

    public void visit ( CompoundExp exp, int level ) {
        VarDecList varDecList = exp.decs;
        while( varDecList != null ) {
          varDecList.head.accept( this, level );
          varDecList = varDecList.tail;
        }
        exp.exps.accept(this, level);
    }

    public void visit ( DecList decList, int level ) {
        while ( decList != null && decList.head != null) {
            decList.head.accept( this, level );
            decList = decList.tail;
        }
    }

    public void visit ( ExpList expList, int level ) {
        System.err.println("ExpList");
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit ( FunctionDec dec, int level ) {
        if(isDeclared(dec.func, "Function", dec.row+1, dec.col+1)) return;

        level++;
        indent( level );
        System.out.println("Entering the scope for function " + dec.func + ":");
        
        insert("global", new NodeType(dec.func, dec, level));
        
        level++;
        stack.add(dec.func);
        
        VarDecList varDecList = dec.params;
        while( varDecList != null && varDecList.head != null) {
            varDecList.head.accept( this, level );
            varDecList = varDecList.tail;
        }
        if (dec.body != null)
            dec.body.accept( this, level );
        
        printSymbolTable( level );
        stack.pop();
        level--;

        indent( level );
        System.out.println("Leaving the function scope");
    }

    public void visit ( IfExp exp, int level ) {
        System.err.println("IfExp");
        indent( level );
        System.out.println("Entering a new block:");

        level++;
        nest++;
        stack.add(String.valueOf(nest));
        
        if (exp.test != null)
            exp.test.accept( this, level );
        if ( exp.then != null)
            exp.then.accept( this, level );
        if ( exp.elsee != null )
            exp.elsee.accept( this, level );
            
        printSymbolTable( level );
        
        stack.pop();
        level--;
            
        indent( level );
        System.out.println("Leaving the block");
    }

    public void visit ( IndexVar var, int level ) {
        var.index.accept( this, level );
    }

    public void visit ( IntExp exp, int level ) {
        System.err.println("IntExp");
    }

    public void visit ( NameTy type, int level ) {
        System.err.println("Namety");
    }

    public void visit ( NilExp exp, int level ) {
        System.err.println("NilExp");
    }

    public void visit ( OpExp exp, int level ) {
        System.err.println("OpExp");
        if ( exp.left != null )
            exp.left.accept( this, level );
        exp.right.accept( this, level );
    }

    public void visit ( ReturnExp expr, int level ) {
        System.err.println("ReturnExp");
        if ( expr.exp != null )
            expr.exp.accept( this, level );
    }

    public void visit ( SimpleDec dec, int level ) {
        if (isDeclared(dec.name, "Variable", dec.row+1, dec.col+1) == false){
            if (dec.typ.typ == 2){
                System.err.println("Error in line " + (dec.row + 1) + ", column " + (dec.col + 1) + ": Invalid Variable Declaration Type (VOID)");
                System.err.println("Instead expected type (BOOL, INT) got VOID. Changing VOID -> INT\n");
                dec.typ.typ = 1;
            }
            NodeType symbol = new NodeType(dec.name, dec, level);
            insert(stack.peek(), symbol);
        }
    }

    public void visit ( SimpleVar var, int level ) {
        // Check if variable has been declared
        if (isDefined(var.name, var.row+1, var.col+1)){
            System.err.println("Error in line " + (var.row + 1) + ", column " + (var.col + 1) + ": Invalid use of undefined variable " + var.name + "\n");
        }
    }

    public void visit ( VarDecList varDecList, int level ) {
        while( varDecList != null ) {
            varDecList.head.accept( this, level );
            varDecList = varDecList.tail;
        }
    }

    public void visit ( VarExp exp, int level ) {
        exp.variable.accept(this, level);
    }

    public void visit ( WhileExp exp, int level ) {
        System.err.println("While Exp");
        indent( level );
        System.out.println("Entering a new block:");
        
        level++;
        nest++;
        stack.add(String.valueOf(nest));

        if ( exp.test != null )
            exp.test.accept( this, level );
        exp.body.accept( this, level );

        printSymbolTable( level );

        stack.pop();
        level--;
        
        indent( level );
        System.out.println("Leaving the block");
    }
}