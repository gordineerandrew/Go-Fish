/*
File:         Card.java
Created:      2015/03/21
Last Changed: 2015/04/30
Author:      Scott Munro <scottnmunro@gmail.com>
             Andrew Gordineer <gordineerandrew@gmail.com>

Provides the idea of what a Card is to the game
*/
public class Card{

    /* all suits values that a card can take on */
    public enum Suit{
        HEART,DIAMOND,SPADE,CLUB,NOTASUIT
    }

    /* all numeric values that a card can take on */
    public enum Value{
        ACE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,TEN,JACK,QUEEN,KING,
        NOTAVALUE
    }


    private Value value;
    private Suit suit;

    public Card(Suit suit, Value value){
        this.value = value;
        this.suit = suit;
    }

    /* getters for the card state */
    public Value getValue(){
        return this.value;
    }

    public Suit getSuit(){
        return this.suit;
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
        switch (suit){
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

    // maps ints to enum values
    public static Card.Value intToValue(int value){
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

    // maps a human-readable character to a value
    public static Card.Value charToValue(char value){
        switch(value){
            case 'a':
            case 'A':
                return Card.Value.ACE;
            case '2':
                return Card.Value.TWO;
            case '3':
                return Card.Value.THREE;
            case '4':
                return Card.Value.FOUR;
            case '5':
                return Card.Value.FIVE;
            case '6':
                return Card.Value.SIX;
            case '7':
                return Card.Value.SEVEN;
            case '8':
                return Card.Value.EIGHT;
            case '9':
                return Card.Value.NINE;
            case 't':
            case 'T':
                return Card.Value.TEN;
            case 'j':
            case 'J':
                return Card.Value.JACK;
            case 'q':
            case 'Q':
                return Card.Value.QUEEN;
            case 'k':
            case 'K':
                return Card.Value.KING;
            default:
                return Card.Value.NOTAVALUE;
        }
    }

    //Returns the string version of the value of the card.
    public static String valueToString(Value value){
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
                return "T";
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
