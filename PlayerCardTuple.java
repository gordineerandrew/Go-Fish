public class PlayerCardTuple{
    public Player p;
    public Card.Value c;
    public double prb; // probability

    public PlayerCardTuple(){
        this.p = null;
        this.c = Card.Value.NOTAVALUE;
        this.prb = 0;
    }

    public PlayerCardTuple(Player p, Card.Value c, double prb){
        this.p = p;
        this.c = c;
        this.prb = prb;
    }
}
