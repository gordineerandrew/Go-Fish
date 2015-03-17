/*
This class specifies the value and suit of a card. It also defines the human
readable string form of a card.
*/
public class Card{

    public enum Suit{
        HEART,DIAMOND,SPADE,CLUB
    }

    public enum Value{
        ACE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,TEN,JACK,QUEEN,KING
    }

    private Value value;
    private Suit suit;

    public Card(Suit suit, Value value){
        this.value = value;
        this.suit = suit;
    }

    //Overrides the toString method to create a human readable version.
    public String toString(){
        StringBuilder s = new StringBuilder("[");
        s.append(valueToString(this.value));
        s.append(suitToString(this.suit));
        s.append("]");
        return s.toString();
    }

    //Switches the suit enum to the unicode value for that enum and returns it.
    private static String suitToString(Suit suit){
        switch (this.suit){
            case HEART:
                return "\u2665";
            case DIAMOND:
                return "\u2666";
            case SPADE:
                return "\u2660";
            case CLUB:
                return "\u2663";
            default:
                return "Error";
        }
    }

    //Returns the string version of the value of the card.
    private static String valueToString(Value value){
        switch (value){
            case ACE:
                return "A";
            case TWO:
                return "2";
            case THREE:
                return "3";
            case FOUR:
                return "4";
            case FIVE:
                return "5";
            case SIX:
                return "6";
            case SEVEN:
                return "7";
            case EIGHT:
                return "8";
            case NINE:
                return "9";
            case TEN:
                return "10";
            case JACK:
                return "j";
            case QUEEN:
                return "Q";
            case KING:
                return "K";
            default:
                return "Error";
        }
    }
}
