import java.util.LinkedList;

public abstract class Player{
    private static final int MAX_TOTAL_BOOKS = 13;

    /* state of the player */
    protected Deck deckRef;
    protected LinkedList<Card> hand;
    protected int score;
    protected int[] books = new int[MAX_TOTAL_BOOKS];


    /* constructor that all player subclasses must call */
    protected Player(Deck deckRef){
        this.deckRef = deckRef;
        hand = new LinkedList<Card>();
        score = 0;
    }

    public void drawCard(){
        Card newCard = deckRef.draw();
        if(newCard != null)
            hand.add(newCard);
    }

    /* check to ensure that a player has the
    specified card */
    public Card getCard(Card.Value val){
        for(Card c: hand){
            if(c.getValue() == val)
                return c;
        }

        return null;
    }

    /*
    PRE: card value exists in this player's hand
        card value must be an actual value
        the player must be an actual player

    returns TRUE if this player recieved the requested card from other player
    returns FALSE if the player mus Go-Fish
    */
    public boolean cardRequest(Card.Value val, Player otherPlayer){
        /* prereq assertion */
        ASSERT(val != Card.NOTAVALUE && otherPlayer != null);

        /* check if the other player has the card */
        Card requestedCard = otherPlayer.getCard(val);
        if(requestedCard != null){
            /* remove the card from the other player's hand */
            otherPlayer.hand.remove(requestedCard);
            /* remove the card from this player's hand */
            Card playerCard = this.getCard(val);
            this.hand.remove(playerCard);
            /* add book to this player's set of books */
            /* return that the requested card was found */
        }

        /* else Go-Fish */
        return false;
    }

}
