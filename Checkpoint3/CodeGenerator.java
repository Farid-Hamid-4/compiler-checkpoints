import absyn.*;

public class CodeGenerator implements AbsynVisitor {

    final static int SPACES = 4;

    private void indent( int level ) {
        for( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
    }
    
    public void visit( ArrayDec dec, int level, boolean flag ) {
    }

    public void visit ( AssignExp exp, int level, boolean flag ) {
    }

    public void visit ( BoolExp exp, int level, boolean flag ) {
    }

    public void visit ( CallExp exp, int level, boolean flag ) {
    }

    public void visit ( CompoundExp exp, int level, boolean flag ) {
    }

    public void visit ( DecList decList, int level, boolean flag ) {
    }

    public void visit ( ExpList expList, int level, boolean flag ) {
    }

    public void visit ( FunctionDec dec, int level, boolean flag ) {
    }

    public void visit ( IfExp exp, int level, boolean flag ) {
    }

    public void visit ( IndexVar var, int level, boolean flag ) {
    }
    
    public void visit ( IntExp exp, int level, boolean flag ) {
    }

    public void visit ( NameTy type, int level, boolean flag ) {
    }
    
    public void visit ( NilExp exp, int level, boolean flag ) {
    }

    public void visit ( OpExp exp, int level, boolean flag ) {
    }

    public void visit ( ReturnExp expr, int level, boolean flag ) {
    }

    public void visit ( SimpleDec dec, int level, boolean flag ) {
    }

    public void visit ( SimpleVar var, int level, boolean flag ) {
    }

    public void visit ( VarDecList varDecList, int level, boolean flag ) {
    }

    public void visit ( VarExp exp, int level, boolean flag ) {
    }

    public void visit ( WhileExp exp, int level, boolean flag ) {
    }

}
