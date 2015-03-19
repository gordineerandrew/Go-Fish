import java.util.ArrayList;
import java.util.Collections;


public class Deck{
    /*
    CONSTANTS
    */
    /* number of cards in a deck */
    final static int DECKSIZE = 52;
    /* total number of suits represented within a deck */
    final static int SUITS = 4;
    /* total number of values represented within a deck */
    final static int VALUES = 13;
    /* total number of times to shuffle the deck before it is sufficiently shuffled */
    final static int SHUFFLE_NUM = 7;

    /* underlying datastruct to store the cards in the list */
    private ArrayList<Card> deck = new ArrayList<Card>(DECKSIZE);

    /* keeps track of where the top of the deck is */
    private int deckIndex;

    public Deck(){

        /* fill the deck with cards in initial sorted order */
        deckIndex = 0;
        Card.Suit suit = Card.Suit.HEART;
        for(int i = 0; i < SUITS; i++){
            for(int j = 1; j <= VALUES; j++){
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
    }

    /* draws from the deck if there are cards to draw */
    public Card draw(){
        if(deckIndex == DECKSIZE)
            return null;

        return deck.get(deckIndex++);
    }
}
