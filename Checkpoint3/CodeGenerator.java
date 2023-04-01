import absyn.*;

public class CodeGenerator implements AbsynVisitor {

    public int mainEntry, globalOffset;

    public CodeGenerator(){
    }

    public void visit ( Absyn trees ) {
        // generate the prelude
        

        // generate the i/o routines

        // make a request to the visit method for DecList

        trees.accept( this, 0, false );

        // generate finale
    }
    
    public void visit( ArrayDec dec, int offset, boolean isAddress ) {
    }

    public void visit ( AssignExp exp, int offset, boolean isAddress ) {
    }

    public void visit ( BoolExp exp, int offset, boolean isAddress ) {
    }

    public void visit ( CallExp exp, int offset, boolean isAddress ) {
    }

    public void visit ( CompoundExp exp, int offset, boolean isAddress ) {
    }

    public void visit ( DecList decList, int offset, boolean isAddress ) {
    }

    public void visit ( ExpList expList, int offset, boolean isAddress ) {
    }

    public void visit ( FunctionDec dec, int offset, boolean isAddress ) {   
    }

    public void visit ( IfExp exp, int offset, boolean isAddress ) {
    }

    public void visit ( IndexVar var, int offset, boolean isAddress ) {
    }
    
    public void visit ( IntExp exp, int offset, boolean isAddress ) {
    }

    public void visit ( NameTy type, int offset, boolean isAddress ) {
    }
    
    public void visit ( NilExp exp, int offset, boolean isAddress ) {
    }

    public void visit ( OpExp exp, int offset, boolean isAddress ) {
    }

    public void visit ( ReturnExp expr, int offset, boolean isAddress ) {
    }

    public void visit ( SimpleDec dec, int offset, boolean isAddress ) {
    }

    public void visit ( SimpleVar var, int offset, boolean isAddress ) {
    }

    public void visit ( VarDecList varDecList, int offset, boolean isAddress ) {
    }

    public void visit ( VarExp exp, int offset, boolean isAddress ) {
    }

    public void visit ( WhileExp exp, int offset, boolean isAddress ) {
    }
}
