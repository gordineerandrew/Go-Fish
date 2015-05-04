/*
File:         Deck.java
Created:      2015/03/21
Last Changed: 2015/04/30
Author:      Scott Munro <scottnmunro@gmail.com>
             Andrew Gordineer <gordineerandrew@gmail.com>

Builds and manages a 52-card deck of cards
*/

import java.util.ArrayList;
import java.util.Collections;

public class Deck{

    /* number of cards in a deck */
    final static int DECK_SIZE = 52;
    /* total number of suits represented within a deck */
    final static int NUM_SUITS = 4;
    /* total number of values represented within a deck */
    final static int NUM_VALUES = 13;
    /* total number of times to shuffle the deck before it is sufficiently shuffled */
    final static int SHUFFLE_NUM = 7;

    /* underlying datastruct to store the cards in the list */
    private ArrayList<Card> deck = new ArrayList<Card>(DECK_SIZE);

    /* keeps track of where the top of the deck is */
    private int deckIndex;

    /* the number of cards remaining in the deck; essentially just used for debugging information */
    private int[] cardsRemaining;

    public Deck(){

        /* fill the deck with cards in initial sorted order */
        deckIndex = 0;
        Card.Suit suit = Card.Suit.HEART;
        for(int i = 0; i < NUM_SUITS; i++){
            
            /* add all of each suit to the deck */
            for(int j = 1; j <= NUM_VALUES; j++)
                deck.add(new Card(suit, Card.intToValue(j)));
            
            /* update the suit */
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

        /* initialize cardsRemaining to have every card */
        cardsRemaining = new int[NUM_VALUES];
        for(int i = 0; i < NUM_VALUES; i++)
            cardsRemaining[i] = NUM_SUITS;
        

    }


    /* draws from the deck if there are cards to draw */
    public Card draw(){
        if(deckEmpty())
            return null;
        /* if the deck isn't empty get the next card */
        Card c = deck.get(deckIndex++);
        /* update the remaining counter */
        cardsRemaining[c.getValue().ordinal()]--;
        return c;
    }

    /* is the deck empty or not? */
    public boolean deckEmpty(){
        return deckIndex == DECK_SIZE;
    }

    /* Used to check the remaining cards in the deck */
    public String toString(){
        StringBuilder s = new StringBuilder(DECK_SIZE - deckIndex);

        /* displays how much of each card is left in the deck */
        for(int i = 0; i < cardsRemaining.length; i++){
            Card.Value val = Card.intToValue(i+1);
            String valString = Card.valueToString(val);
            s.append("There are " + cardsRemaining[i] + " " + valString + "'s remaining.\n");
        }

        /* display each individual card left */
        for(int i = deckIndex; i < DECK_SIZE; i++)
            s.append(deck.get(i) + " ");
        
        s.append("\n");
        return s.toString();
    }

    /* how much of the deck is left? */
    public int size(){
        return DECK_SIZE - deckIndex;
    }
}
