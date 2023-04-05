import absyn.*;
import java.util.HashMap;
import java.util.function.Function;

public class CodeGenerator implements AbsynVisitor {

    public HashMap<String, Integer> framePtr = new HashMap<String, Integer>();

    /* Offsets */
    public int mainEntry = 0;
    public int globalOffset = 0;
    public int currentOffset = 0;
    public int funLoc = 0;
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

    public String[] OPERATIONS = {"+", "-", "-", "*", "/", "==", "!=", "<", "<=", ">", ">=", "~", "&&", "||"};
    public String[] OP = {"ADD", "SUB", "UM", "MUL", "DIV", "EQ", "NE", "LT", "LE", "GT", "GE", "NOT", "AND", "OR"};

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
        emitRM( "ST", FP, 0, FP, "push ofp" );
        emitRM( "LDA", FP, 0, FP, "push frame" );
        emitRM( "LDA", AC, 1, PC, "load ac with ret ptr" );
        emitRM_Abs( "LDA", PC, mainEntry+1, "jump to main loc" );
        emitRM( "LD", FP,  0, FP, "pop frame" );
        emitComment("End of execution.");
        emitRO( "HALT", 0, 0, 0, "");
    }

    public void visit( ArrayDec dec, int offset, boolean isGlobal ) {
        if(isGlobal) {
            emitComment("allocating global var: " + dec.name);
            emitComment("<- vardecl");
        } else {
            emitComment("processing local var: " + dec.name);
            framePtr.put(dec.name, offset);
        }
    }

    public void visit ( AssignExp exp, int offset, boolean isAddr ) {
        emitComment("-> op");

        exp.lhs.accept(this, offset-1, true);

        offset = framePtr.get("op");
        emitRM("ST", AC, offset, FP, "op: push left");

        exp.rhs.accept(this, offset-1, false);


        emitRM("LD", AC1, offset, FP, "op: load left");
        emitRM("ST", AC, 0, 1, "assign: store value");

        emitComment("<- op");
    }

    public void visit ( BoolExp exp, int offset, boolean isAddr ) {
        emitComment("-> constant");
        emitRM("LDC", AC, 1, 0, "load const");
        emitComment("<- constant");
    }

    public void visit ( CallExp exp, int offset, boolean isAddr ) {
        emitComment("-> call of function: " + exp.func);
        int functionLoc = -emitLoc;
        if (exp.args != null){
            exp.args.accept( this, offset, false );
            emitRM("ST", AC, offset + initialOffset, FP, "store arg val");
        }
        emitRM("ST", FP, offset, FP, "push ofp");
        emitRM("LDA", FP, offset, FP, "push frame");
        emitRM("LDA", AC, 1, PC, "load ac with ret ptr");
        emitRM("LDA", PC, functionLoc, FP, "jump to fun loc");
        emitRM("LD", FP, 0, FP, "pop frame");

        emitComment("<- call");
    }

    public void visit ( CompoundExp exp, int offset, boolean isAddr ) {
        // Comes from Boolean expressions or FunctionDec
        emitComment("-> compound statement");
        visit(exp.decs, offset, false);

        visit(exp.exps, currentOffset, false);
        emitComment("<- compound statement");
    }

    public void visit ( DecList decList, int offset, boolean isAddr ) {
        while ( decList != null && decList.head != null) {
            // VarDecList or Function Dec
            globalOffset--;
            decList.head.accept( this, offset, true );
            decList = decList.tail;
        }
    }

    public void visit ( ExpList expList, int offset, boolean isAddr ) {
        if(!framePtr.containsKey("op")){
            framePtr.put("op", offset);
            currentOffset--;
        } else {
            offset = framePtr.get("op");
        }

        while( expList != null ) {
            if (expList.head instanceof OpExp) offset = framePtr.get("op");
            expList.head.accept( this, offset, false );
            currentOffset--;
            expList = expList.tail;
        }  
    }

    public void visit ( FunctionDec dec, int offset, boolean isAddr ) {

        // Comes from Global DecList
        emitComment("processing function: " + dec.func);
        emitComment("jump around function body here");
        
        if (dec.func.equals("main")) {
            mainEntry = emitLoc;
        }
        int savedLoc = emitSkip(1);


        emitRM("ST", AC, retOffset, FP, "store return");

        // offset here is -2 typically
        offset = initialOffset;
        
        VarDecList params = dec.params;

        // Function Params read into offset
        while(params != null) {
            if (params.head instanceof SimpleDec){
                framePtr.put(((SimpleDec)params.head).name, offset);
            }
            else if (params.head instanceof ArrayDec){
                framePtr.put(((ArrayDec)params.head).name, offset);
            }
            offset--;
            params = params.tail;
        }

        currentOffset = offset;

        // Goes to CompundExp typically
        dec.body.accept(this, offset, false);

        int savedLoc2 = emitSkip(0);
        emitRM("LD", PC, globalOffset, FP, "return to caller");
        emitBackup(savedLoc);
        emitRM("LDA", PC, savedLoc2 - savedLoc, PC, "jump around fn body");
        emitComment("<- fundecl");
        emitRestore();
    }
    
    public void visit ( IfExp exp, int offset, boolean isAddr ) {
        emitComment("-> if");
        emitComment("<- if");
    }

    public void visit ( IndexVar var, int offset, boolean isAddr ) {
        offset = framePtr.get(var.name);
        emitRM("LDA", AC, offset, FP, "load id address");
        emitRM("ST", AC, globalOffset, FP, "store array addr");
        globalOffset--;
        var.index.accept(this, globalOffset, false);
    }
    
    public void visit ( IntExp exp, int offset, boolean isAddr ) {
        emitComment("-> constant");
        emitRM("LDC", AC, 1, 0, "load const");
        emitComment("<- constant");
    }

    public void visit ( NameTy type, int offset, boolean isAddr ) {
    }
    
    public void visit ( NilExp exp, int offset, boolean isAddr ) {
    }

    public void visit ( OpExp exp, int offset, boolean isAddr ) {
        emitComment("-> op");

        exp.left.accept(this, offset-1, false);
        emitRM("ST", AC, offset, FP, "op: push left");

        exp.right.accept(this, offset-1, false);

        // perform load left
        emitRM("LD", AC1, offset, FP, "op: load left");

        // For next operation, do operation
        
        if (exp.op > 4) {
            emitRO("SUB", AC, 1, 0, "op " + OPERATIONS[exp.op]);
            emitRM("JGT", AC, 2, PC, "br if true");
            emitRM("LDC", AC, 0, AC, "false case");
            emitRM("LDA", PC, 1, PC, "unconditional jmp");
            emitRM("LDC", AC, 1, AC, "true case");
        } else
            emitRO(OP[exp.op], AC, 1, 0, "op " + OPERATIONS[exp.op]);

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
        } else {
            emitComment("processing local var: " + dec.name);
            framePtr.put(dec.name, offset);
        }
    }

    public void visit ( SimpleVar var, int offset, boolean isAddr ) {
        offset = framePtr.get(var.name);
        emitRM("LDA", AC, offset, FP, "load id address");
    }

    public void visit ( VarDecList varDecList, int offset, boolean isAddr ) {
        while( varDecList != null ) {
            varDecList.head.accept( this, offset, false );
            offset--;
            currentOffset--;
            varDecList = varDecList.tail;
        }
    }

    public void visit ( VarExp exp, int offset, boolean isAddr ) {
        emitComment("-> id");
        emitComment("looking up id: " + exp.toString());
        
        exp.variable.accept(this, offset, false);

        /* Need to check if the variable is defined locally or globally. If local use emitRM with GP otherwise use FP. Use LDA if the variable is defined in the body, otherwise use LD if the variable is defined as an argument in the params */

        emitComment("<- id");
    }

    public void visit ( WhileExp exp, int offset, boolean isAddr ) {
        emitComment("-> while");
        emitComment("while: jump after body comes back here");
        offset = framePtr.get("op");
        exp.test.accept(this, offset, false);
        emitComment("while: jump to end belongs here");

        int savedLoc = emitSkip(1);
        exp.body.accept(this, offset, false);
        int savedLoc2 = emitLoc;

        emitRM("LDA", PC, -savedLoc - (currentOffset - initialOffset), PC, "while: absolute jmp to test");
        emitBackup(savedLoc);
        emitRM("JEQ", AC, savedLoc2 - savedLoc, PC, "while: jmp to end");
        emitRestore();
        emitComment("<- while");
    }
}
