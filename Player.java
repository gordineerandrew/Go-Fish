import java.util.ArrayList;

public abstract class Player{
    private static final int MAX_TOTAL_BOOKS = 13;

    /* state of the player */
    protected String name;
    protected Deck deckRef;
    protected ArrayList<Card> hand;
    protected int score;
    protected int[] books = new int[MAX_TOTAL_BOOKS];


    /* constructor that all player subclasses must call */
    protected Player(String name, Deck deckRef){
        this.name = name;
        this.deckRef = deckRef;
        this.hand = new ArrayList<Card>();
        this.score = 0;

    }

    /* access functions */
    public String getName(){
        return name;
    }

    public int getScore(){
        return score;
    }

    private void collectBook(Card.Value value){
        books[value.ordinal()]++;
        score++;
    }
    public Card drawCard(){
        Card newCard = deckRef.draw();
        /* if there are cards left in the deck... */
        if(newCard != null){
            /* get the value of the new card */
            Card.Value newValue = newCard.getValue();

            /* if the list already contains this card create a book
            after removing the old card */
            Card cardInHand = this.getCard(newValue);
            if(cardInHand != null){
                collectBook(newValue);
                hand.remove(cardInHand);
            }

            /* else add the new card to your hand */
            else{
                hand.add(newCard);
            }
        }
        /* else don't draw */

        /* return a reference to the drawn card */
        return newCard;
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
        if(val == Card.Value.NOTAVALUE || otherPlayer == null){
            throw new IllegalArgumentException("Card's value and player must exist");
        }

        /* check if the other player has the card */
        Card requestedCard = otherPlayer.getCard(val);
        if(requestedCard != null){
            /* remove the card from the other player's hand */
            otherPlayer.hand.remove(requestedCard);
            /* remove the card from this player's hand */
            Card playerCard = this.getCard(val);
            this.hand.remove(playerCard);
            /* add book to this player's set of books */
            collectBook(requestedCard.getValue());
            /* return that the requested card was found */
            return true;
        }

        /* else Go-Fish */
        return false;
    }

    public void displayHand(){
        String s = hand.toString();
        System.out.println(s.substring(1,s.length()-1));
    }

    public int getHandSize(){
        return hand.size();
    }

    public boolean handEmpty(){
        return hand.size() == 0;
    }

    public String toString(){
        return getName();
    }

    public void displayState(){
        String s = hand.toString();
        s = s.substring(1,s.length()-1);
        System.out.printf("%s:\tScore: %d\tHand: %s\n", name, score, s);
    }

    public void beginTurn(){
        System.out.println(name + "'s turn.");
        System.out.println("Score: " + score);
    }
    public void endTurn(){
        System.out.println(name + "'s score: " + score);
    }

}
