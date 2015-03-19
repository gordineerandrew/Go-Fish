import java.util.LinkedList;
import java.util.ListIterator;

public abstract class Player{
    private static final int MAX_TOTAL_BOOKS = 13;

    /* state of the player */
    protected String name;
    protected Deck deckRef;
    protected LinkedList<Card> hand;
    protected int score;
    protected int[] books = new int[MAX_TOTAL_BOOKS];


    /* constructor that all player subclasses must call */
    protected Player(String name, Deck deckRef){
        this.name = name;
        this.deckRef = deckRef;
        this.hand = new LinkedList<Card>();
        this.score = 0;

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
        if(val != Card.Value.NOTAVALUE && otherPlayer != null){
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
            books[requestedCard.getValue().ordinal()]++;
            /* update the score */
            score++;
            /* return that the requested card was found */
            return true;
        }

        /* else Go-Fish */
        return false;
    }

    /* find and remove books */
    public void collectBooks(){
        /* check each element in list for duplicates:
        iterate through each element in the list... */
        for(int i = 0; i < hand.size(); i++){
            /* check if the current card in the hand is valid */
            Card current_card = hand.get(i);
            if(current_card != null){
                /* if the card is valid, get it's value... */
                Card.Value current_val = current_card.getValue();
                /* if there are other cards in your hand with the same value
                null them out */
                if(nullDuplicates(i+1, current_val)){
                    /* if there were duplicates, null out the current value
                    and mark the new book in the books record */
                    hand.set(i, null);
                    books[current_val.ordinal()]++;
                    score++;
                }
                /* else do nothing */
            }
        }

        /* remove any nulled out values */
        for(ListIterator<Card> iterator = hand.listIterator(); iterator.hasNext(); ){
            Card nextCard = iterator.next();
            if(nextCard == null)
                iterator.remove();
        }
    }

    /* used by collectBooks to help remove duplicates from the hand
    nulls out the first duplicate (only the first because books are size 2) in
    the hand to be removed later does not modify the size of the hand by itself*/
    private boolean nullDuplicates(int startIndex, Card.Value val){
        /* search for a duplicate */
        for(int i = startIndex; i < hand.size(); i++){
            Card currentCard = hand.get(i);
            if(currentCard != null && currentCard.getValue() == val){
                /* if a duplicate is found, null out the duplicate and return */
                hand.set(i, null);
                return true;
            }
        }
        /* if no duplicates found return that false */
        return false;
    }

    public void displayHand(){
        System.out.println(hand);
    }

    public String toString(){
        return name;
    }

    public void displayState(){
        System.out.printf("%s:\tScore: %d\tHand: %s\n", name, score, hand.toString());
    }

}
