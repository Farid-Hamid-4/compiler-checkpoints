package absyn;

public class IfExp extends Exp {
    public Exp test;
    public Exp then;
    public Exp elsee;

    public IfExp ( int pos, Exp test, Exp then, Exp elsee ) {
        this.pos = pos;
        this.test = test;
        this.then = then;
        this.elsee = elsee;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
