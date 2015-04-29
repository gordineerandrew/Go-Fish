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

    public void beginTurn(){
        super.beginTurn();
        System.out.printf("%-22s","Current hand:");
        displayHand();
        GoFish.getProbabilities();
    }
    public void endTurn(){
        super.endTurn();
        System.out.print("Current hand: ");
        displayHand();
    }

    public Card.Value[] cardsInHand(){
        Card.Value[] cards = new Card.Value[hand.size()];
        for(int i = 0; i < hand.size(); i++){
            cards[i] = hand.get(i).getValue();
        }
        return cards;
    }
}
