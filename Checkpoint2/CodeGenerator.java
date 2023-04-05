import absyn.*;

public class CodeGenerator implements AbsynVisitor {

    /* Offsets */
    public int mainEntry = 0;
    public int globalOffset = 0;
    public static int emitLoc = 0;
    public static int highEmitLoc = 0;
    public static final int retOffset = -1;
    public static final int initialOffset = -2;

    /* Special Registers */
    public static final int AC = 0;
    public static final int AC1 = 1;
    public static final int FP = 5;
    public static final int GP = 6;
    public static final int PC = 7;

    /* The following emit functions were provided by Professor Fei in his Lecture 11 slides */
    public void emitComment(String comment) {
        System.out.println("* " + comment);
    }

    public void emitRO( String op, int r, int s, int t, String c ) {
        System.out.printf("%3d: %5s %d,%d,%d \t%s\n", emitLoc, op, r, s, t, c);
        ++emitLoc;
        if( highEmitLoc < emitLoc )
            highEmitLoc = emitLoc;
    }

    public void emitRM( String op, int r, int d, int s, String c ) {
        System.out.printf("%3d: %5s %d,%d(%d) \t%s\n", emitLoc, op, r, d, s, c);
        ++emitLoc;
        if( highEmitLoc < emitLoc )
            highEmitLoc = emitLoc;
    }

    public void emitRM_Abs( String op, int r, int a, String c ) {
        System.out.printf("%3d: %5s %d,%d(%d) \t%s\n", emitLoc, op, r, a - (emitLoc + 1), PC, c);
        ++emitLoc;
        if( highEmitLoc < emitLoc )
            highEmitLoc = emitLoc;
    }

    public int emitSkip( int distance ) {
        int i = emitLoc;
        emitLoc += distance;
        if( highEmitLoc < emitLoc )
            highEmitLoc = emitLoc;
        return i;
    }

    public void emitBackup( int loc ) {
        if( loc > highEmitLoc )
            emitComment("BUG in emitBackup");
        emitLoc = loc;
    }

    public void emitRestore() {
        emitLoc = highEmitLoc;
    }

    /* Custom emit function to handle OP for IN, OUT, ADD, SUB, TIMES, OVER */
    public void emitOP(String op, int r, int s, int t, String c) {
        System.out.printf("%3d: %5s %d,%d,%d \t%s\n", emitLoc, op, r, s, t, c);
        ++emitLoc;
    }

    public void visit ( Absyn trees ) {
        // generate the prelude
        emitComment("Standard prelude:");
        emitRM("LD", GP, 0, AC, "load gp with maxaddress");
        emitRM("LDA", FP, 0, GP, "copy to gp to fp");
        emitRM("ST", AC, 0 , AC, "clear location 0");        
        int savedLoc = emitSkip(1);

        // input
        emitComment("Jump around i/o routines here");
        emitComment("code for input routine");
        emitRM("ST", AC, retOffset, FP, "store return");
        emitOP("IN", 0, 0, 0, "input");
        emitRM("LD", PC, retOffset, FP, "return to caller");

        // output
        emitComment("code for output routine");
        emitRM("ST", AC, retOffset, FP, "store return");
        emitRM("LD", AC, initialOffset, FP, "load output value");
        emitOP("OUT", 0, 0, 0, "output");
        emitRM("LD", PC, retOffset, FP, "return to caller");
        
        int savedLoc2 = emitSkip(0);
        emitBackup(savedLoc);
        emitRM_Abs("LDA", PC, savedLoc2, "jump around i/o code");
        emitRestore();

        emitComment("End of standard prelude.");

        // make a request to the visit method for DecList
        trees.accept( this, 0, false );

        // generate finale
        emitRM( "ST", FP, globalOffset, FP, "push ofp" );
        emitRM( "LDA", FP, globalOffset, FP, "push frame" );
        emitRM( "LDA", AC, 1, PC, "load ac with ret ptr" );
        emitRM_Abs( "LDA", PC, mainEntry, "jump to main loc" );
        emitRM( "LD", FP,  0, FP, "pop frame" );
        emitComment("End of execution.");
        emitRO( "HALT", 0, 0, 0, "");
    }

    public void visit( ArrayDec dec, int offset, boolean isGlobal ) {
        if(isGlobal) {
            emitComment("allocating global var: " + dec.name);
            emitComment("<- vardecl");
            emitLoc++;
        } else {
            emitComment("processing local var: " + dec.name);
        }
    }

    public void visit ( AssignExp exp, int offset, boolean isAddr ) {
        emitComment("-> op");

        exp.lhs.accept(this, offset, true);
        emitRM("ST", AC, offset, FP, "op: push left");
        
        exp.rhs.accept(this, offset, false);

        emitComment("<- op");
    }

    public void visit ( BoolExp exp, int offset, boolean isAddr ) {
    }

    public void visit ( CallExp exp, int offset, boolean isAddr ) {
        emitComment("-> call");
        emitComment("<- call");
    }

    public void visit ( CompoundExp exp, int offset, boolean isAddr ) {
        emitComment("-> compound statement");
        visit(exp.decs, offset, false);
        visit(exp.exps, offset, false);
        emitComment("<- compound statement");
    }

    public void visit ( DecList decList, int offset, boolean isAddr ) {
        while ( decList != null && decList.head != null) {
            decList.head.accept( this, offset, true );
            decList = decList.tail;
        }
    }

    public void visit ( ExpList expList, int offset, boolean isAddr ) {
        while( expList != null ) {
            expList.head.accept( this, offset, false );
            expList = expList.tail;
        }
    }

    public void visit ( FunctionDec dec, int offset, boolean isAddr ) {
        emitComment("processing function: " + dec.func);
        emitComment("jump around function body here");

        emitRM("ST", AC, retOffset, FP, "store return");

        /* Go to CompoundExp */
        dec.body.accept(this, offset, false);
    }
    
    public void visit ( IfExp exp, int offset, boolean isAddr ) {
        emitComment("-> if");
        emitComment("<- if");
    }

    public void visit ( IndexVar var, int offset, boolean isAddr ) {
    }
    
    public void visit ( IntExp exp, int offset, boolean isAddr ) {
    }

    public void visit ( NameTy type, int offset, boolean isAddr ) {
    }
    
    public void visit ( NilExp exp, int offset, boolean isAddr ) {
    }

    public void visit ( OpExp exp, int offset, boolean isAddr ) {
        emitComment("-> op");
        exp.left.accept(this, offset, false);
        emitComment("<- op");
    }

    public void visit ( ReturnExp expr, int offset, boolean isAddr ) {
        emitComment("-> return");
        emitComment("<- return");
    }

    public void visit ( SimpleDec dec, int offset, boolean isGlobal ) {
        if(isGlobal) {
            emitComment("allocating global var: " + dec.name);
            emitComment("<- vardecl");
            emitLoc++;
        } else {
            emitComment("processing local var: " + dec.name);
        }
    }

    public void visit ( SimpleVar var, int offset, boolean isAddr ) {
    }

    public void visit ( VarDecList varDecList, int offset, boolean isAddr ) {
        while( varDecList != null ) {
            varDecList.head.accept( this, offset, false );
            varDecList = varDecList.tail;
        }
    }

    public void visit ( VarExp exp, int offset, boolean isAddr ) {
        emitComment("-> id");
        emitComment("looking up id: " + exp.toString());

        /* Need to check if the variable is defined locally or globally. If local use emitRM with GP otherwise use FP. Use LDA if the variable is defined in the body, otherwise use LD if the variable is defined as an argument in the params */

        emitComment("<- id");
    }

    public void visit ( WhileExp exp, int offset, boolean isAddr ) {
        emitComment("-> while");
        emitComment("while: jump after body comes back here");
        exp.test.accept(this, offset, false);
        emitComment("<- while");
    }
}
