import java.util.ArrayList;


public class Deck{
    ArrayList<Card> deck = new ArrayList<Card>(52);
    private int values = 13;
    private int suits = 4;

    public Deck(){
        int deckIndex = 0;
        Card.Suit suit = Card.Suit.HEART;
        for(int i = 0; i < this.suits; i++){
            for(int j = 0; j < this.values; j++){
                Card card = new Card(suit, intToValue(j));
                deck.set(deckIndex, card);
            }
            if(i == 0){
                suit = Card.Suit.DIAMOND;
            }else if(i == 1){
                suit = Card.Suit.SPADE;
            }else{
                suit = Card.Suit.CLUB;
            }
        }
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
