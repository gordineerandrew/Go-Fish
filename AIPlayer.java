public class AIPlayer extends Player{
    public AIPlayer(String name, Deck deckRef){
        super(name, deckRef);
    }
    public Card drawCard(){
        Card c = super.drawCard();
        if(GameConstants.DEBUG && c != null){
            System.out.println(this.name + " drew: " + c);
        }
        return c;
    }
}
