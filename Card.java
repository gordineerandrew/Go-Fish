/*
This class specifies the value and suit of a card. It also defines the human
readable string form of a card.
*/
public class Card{

    public enum Suit{
        HEART, DIAMOND, SPADE, CLUB
    }

    public enum Value{
        A,2,3,4,5,6,7,8,9,10,J,Q,K
    }

    Value value;
    Suit suit;

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
    public static String suitToString(Suit suit){
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
    public static String valueToString(Value value){
        switch (value){
            case A:
                return "A";
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case 8:
                return "8";
            case 9:
                return "9";
            case 10:
                return "10";
            case J:
                return "j";
            case Q:
                return "Q";
            case K:
                return "K";
            default:
                return "Error";
        }
    }
}
