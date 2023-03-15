import absyn.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Iterator;

public class SemanticAnalyzer implements AbsynVisitor {

    final static int SPACES = 4;

    private void indent( int level ) {
        for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
    }

    public SemanticAnalyzer() {
    }

    public void searchHashMap() {
    }
    
    public void visit( ArrayDec exp, int level ) {
    }

    public void visit ( AssignExp exp, int level ) {
    }

    public void visit ( BoolExp exp, int level ) {
    }

    public void visit ( CallExp exp, int level ) {
    }

    public void visit ( CompoundExp exp, int level ) {
    }

    public void visit ( DecList decList, int level ) {
    }

    public void visit ( ExpList expList, int level ) {
    }

    public void visit ( FunctionDec exp, int level ) {
        
    }

    public void visit ( IfExp exp, int level ) {
    }

    public void visit ( IndexVar exp, int level ) {
    }
    
    public void visit ( IntExp exp, int level ) {
    }

    public void visit ( NameTy exp, int level ) {
    }
    
    public void visit ( NilExp exp, int level ) {
    }

    public void visit ( OpExp exp, int level ) {
    }

    public void visit ( ReturnExp expr, int level ) {
    }

    public void visit ( SimpleDec exp, int level ) {
    }

    public void visit ( SimpleVar exp, int level ) {
    }

    public void visit ( VarDecList varDecList, int level ) {
    }

    public void visit ( VarExp exp, int level ) {
    }

    public void visit ( WhileExp exp, int level ) {
    }
}