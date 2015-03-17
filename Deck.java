import java.util.ArrayList;
import java.util.Collections;


public class Deck{
    final static int DECKSIZE = 52;
    private ArrayList<Card> deck = new ArrayList<Card>(DECKSIZE);
    /* total number of suits represented within a deck */
    final static int SUITS = 4;
    /* total number of values represented within a deck */
    final static int VALUES = 13;
    /* total number of times to shuffle the deck before it is sufficiently shuffled */
    final static int SHUFFLE_NUM = 7;

    private int deckIndex;

    public Deck(){

        /* fill the deck with cards in initial sorted order */
        deckIndex = 0;
        Card.Suit suit = Card.Suit.HEART;
        for(int i = 0; i < SUITS; i++){
            for(int j = 0; j < VALUES; j++){
                Card card = new Card(suit, intToValue(j));
                deck.set(deckIndex, card);
                deckIndex++;
            }
            if(i == 0){
                suit = Card.Suit.DIAMOND;
            }else if(i == 1){
                suit = Card.Suit.SPADE;
            }else{
                suit = Card.Suit.CLUB;
            }
        }

        /* reset the deckIndex to the top of the deck */
        deckIndex = 0;


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

    private static Card.Value intToValue(int value){
        switch(value){
            case 1:
                return Card.Value.ACE;
            case 2:
                return Card.Value.TWO;
            case 3:
                return Card.Value.THREE;
            case 4:
                return Card.Value.FOUR;
            case 5:
                return Card.Value.FIVE;
            case 6:
                return Card.Value.SIX;
            case 7:
                return Card.Value.SEVEN;
            case 8:
                return Card.Value.EIGHT;
            case 9:
                return Card.Value.NINE;
            case 10:
                return Card.Value.TEN;
            case 11:
                return Card.Value.JACK;
            case 12:
                return Card.Value.QUEEN;
            case 13:
                return Card.Value.KING;
            default:
                return Card.Value.NOTAVALUE;
        }
    }
}
