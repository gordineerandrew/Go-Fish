public class HumanPlayer extends Player{
    public HumanPlayer(String name, Deck deckRef){
        super(name, deckRef);
    }
    public Card drawCard(){
        Card c = super.drawCard();
        if(c != null){
            System.out.println(this.name + " drew: " + c);
        }
        return c;
    }
}
