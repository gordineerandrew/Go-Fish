import java.util.ArrayList;
import java.util.Collections;


public class Deck{
    /*
    CONSTANTS
    */
    /* number of cards in a deck */
    final static int DECKSIZE = 52;
    /* total number of suits represented within a deck */
    final static int NUM_SUITS = 4;
    /* total number of values represented within a deck */
    final static int NUM_VALUES = 13;
    /* total number of times to shuffle the deck before it is sufficiently shuffled */
    final static int SHUFFLE_NUM = 7;

    /* underlying datastruct to store the cards in the list */
    private ArrayList<Card> deck = new ArrayList<Card>(DECKSIZE);

    /* keeps track of where the top of the deck is */
    private int deckIndex;

    private int[] cardsRemaining;

    public Deck(){

        /* fill the deck with cards in initial sorted order */
        deckIndex = 0;
        Card.Suit suit = Card.Suit.HEART;
        for(int i = 0; i < NUM_SUITS; i++){
            for(int j = 1; j <= NUM_VALUES; j++){
                deck.add(new Card(suit, Card.intToValue(j)));
            }

            if(i == 0)
                suit = Card.Suit.DIAMOND;
            else if(i == 1)
                suit = Card.Suit.SPADE;
            else
                suit = Card.Suit.CLUB;

        }

        /* shuffle the deck */
        for(int i = 0; i < SHUFFLE_NUM; i++)
            Collections.shuffle(deck);
        /* store number of suits for each value into an array */
        cardsRemaining = new int[NUM_VALUES];
        for(int i = 0; i < NUM_VALUES; i++){
            cardsRemaining[i] = NUM_SUITS;
        }

    }


    /* draws from the deck if there are cards to draw */
    public Card draw(){
        if(deckIndex == DECKSIZE)
            return null;
        Card c = deck.get(deckIndex++);
        cardsRemaining[c.getValue().ordinal()]--;
        return c;
    }

    /* Used to check the remaining cards in the deck */
    public String toString(){
        StringBuilder s = new StringBuilder(DECKSIZE - deckIndex);
        for(int i = 0; i < cardsRemaining.length; i++){
            Card.Value val = Card.intToValue(i+1);
            String valString = Card.valueToString(val);
            s.append("\nThere are " + cardsRemaining[i] + " " + valString + "'s remaining.\n");
        }
        for(int i = deckIndex; i < DECKSIZE; i++){
            s.append(deck.get(i) + " ");
        }
        s.append("\n");
        return s.toString();
    }
}
