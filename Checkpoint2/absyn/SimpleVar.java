package absyn;

public class SimpleVar extends Var {
    public String name;

    public SimpleVar ( int row, int col, String name ) {
        this.row = row;
        this.col = col;
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
