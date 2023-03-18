package absyn;

public class ArrayDec extends VarDec {
    public NameTy typ;
    public String name;
    public int size;

    public ArrayDec ( int row, int col, NameTy typ, String name, int size ) {
        this.row = row;
        this.col = col;
        this.typ = typ;
        this.name = name;
        this.size = size;
    }

    public String toString(){
        if(typ.typ == 0) return "bool";
        if(typ.typ == 1) return "int";
        if(typ.typ == 2) return "void";
        return null;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
