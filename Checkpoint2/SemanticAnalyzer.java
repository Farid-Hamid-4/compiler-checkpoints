import absyn.*;
import java.util.HashMap;
import java.util.Stack;
import java.util.Iterator;
import java.util.ArrayList;

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

    public void lookup() {
    }

    public void delete() {
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
        NodeType symbol = new NodeType(dec.name, dec, level);

        if(symbolTable.containsKey(dec.name) == false) {
            insert(stack.peek(), symbol);
        } else {
            System.err.println("Error: array variable " + dec.name + " already declared on line " + (dec.row + 1) + ", column " + (dec.col + 1));
            System.err.println("                     ^");
        }
    }

    public void visit ( AssignExp exp, int level ) {
        exp.lhs.accept( this, level );
        exp.rhs.accept( this, level );
    }

    public void visit ( BoolExp exp, int level ) {
    }

    public void visit ( CallExp exp, int level ) {
        ExpList argsList = exp.args;
        while( argsList != null ) {
            argsList.head.accept( this, level );
            argsList = argsList.tail;
        }
    }

    public void visit ( CompoundExp exp, int level ) {
        VarDecList varDecList = exp.decs;
        while( varDecList != null ) {
          varDecList.head.accept( this, level );
          varDecList = varDecList.tail;
        }
        ExpList expList = exp.exps;
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit ( DecList decList, int level ) {
        while ( decList != null && decList.head != null) {
            decList.head.accept( this, level );
            decList = decList.tail;
        }
    }

    public void visit ( ExpList expList, int level ) {
        while( expList != null ) {
            expList.head.accept( this, level );
            expList = expList.tail;
        }
    }

    public void visit ( FunctionDec dec, int level ) {
        level++;
        indent( level );
        System.out.println("Entering the scope for function " + dec.func + ":");
        
        level++;
        stack.add(dec.func);

        VarDecList varDecList = dec.params;
        while( varDecList != null && varDecList.head != null) {
            varDecList.head.accept( this, level );
            varDecList = varDecList.tail;
        }
        if( dec.body != null ) 
            dec.body.accept( this, level );
        
        printSymbolTable( level );
        stack.pop();
        level--;

        indent( level );
        System.out.println("Leaving the function scope");
    }

    public void visit ( IfExp exp, int level ) {
        System.out.println("Entering a new block:");

        if (exp.test != null)
            exp.test.accept( this, level );
        if ( exp.then != null)
            exp.then.accept( this, level );
        if ( exp.elsee != null )
            exp.elsee.accept( this, level );

        System.out.println("Leaving the block");
    }

    public void visit ( IndexVar var, int level ) {
        var.index.accept( this, level );
    }

    public void visit ( IntExp exp, int level ) {
    }

    public void visit ( NameTy type, int level ) {
    }

    public void visit ( NilExp exp, int level ) {
    }

    public void visit ( OpExp exp, int level ) {
        if ( exp.left != null )
            exp.left.accept( this, level );
        exp.right.accept( this, level );
    }

    public void visit ( ReturnExp expr, int level ) {
        if ( expr.exp != null )
            expr.exp.accept( this, level );
    }

    /* Still prints and recognizes the variable even though redeclared */
    public void visit ( SimpleDec dec, int level ) {
        NodeType symbol = new NodeType(dec.name, dec, level);
        
        Iterator<String> scope = stack.iterator();
        while( scope.hasNext() ) {
            ArrayList<NodeType> list = symbolTable.get(scope.next());
            if(list != null) {
                for(int i = 0; i < list.size(); i++) {
                    if(list.get(i).name.equals(dec.name)) {
                        System.err.println("Error: variable " + dec.name + " already declared on line " + (dec.row + 1) + ", column " + (dec.col + 1));
                        System.err.println("                ^"); 
                        return;      
                    }
                }
            }
        }
        insert(stack.peek(), symbol);
    }

    public void visit ( SimpleVar var, int level ) {
    }

    public void visit ( VarDecList varDecList, int level ) {
        while( varDecList != null ) {
            varDecList.head.accept( this, level );
            varDecList = varDecList.tail;
        }
    }

    public void visit ( VarExp exp, int level ) {
    }

    public void visit ( WhileExp exp, int level ) {
        if ( exp.test != null )
            exp.test.accept( this, level );
        exp.body.accept( this, level );
    }
}