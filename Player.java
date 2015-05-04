/*
File:         Player.java
Created:      2015/03/21
Last Changed: 2015/04/30
Author:      Scott Munro <scottnmunro@gmail.com>
             Andrew Gordineer <gordineerandrew@gmail.com>

Base state and behavior of every player in the game
*/

import java.util.ArrayList;

public abstract class Player{
    private static final int MAX_TOTAL_BOOKS = 13;

    /* state of the player */
    protected String name;
    /* reference to the deck */
    protected Deck deckRef;
    /* player's hand of cards */
    protected ArrayList<Card> hand;
    /* current score */
    protected int score;
    /* list of books owned by this player */
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

    /* 
    function used to track the player's score and update other players
    when the player creates a book 
    */
    private void collectBook(Card.Value value, boolean drewCard){
        books[value.ordinal()]++;
        score++;
        if(GoFish.initState)
            GoFish.initialAlertBook(this, value, drewCard);
        else
            GoFish.alertBook(this, value, drewCard);
    }

    /* 
    draws a card from the deck if any are left
    updates the probabilty map and alerts other players of the 
    new unknown card 
    */
    public Card drawCard(){
        Card newCard = deckRef.draw();
        /* if there are cards left in the deck... */
        if(newCard != null){
            /* incrememnt probabilities across board by 1 */
            GoFish.incrementProbabilities(this);
            /* get the value of the new card */
            Card.Value newValue = newCard.getValue();

            /* if the list already contains this card create a book
            after removing the old card */
            Card cardInHand = this.getCard(newValue);
            if(cardInHand != null){
                collectBook(newValue, true);
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
            collectBook(requestedCard.getValue(), false);
            /* return that the requested card was found */
            return true;
        }

        /* else Go-Fish */
        return false;
    }

    /* 
    display the player's hand in a human readable form
    */
    public void displayHand(){
        StringBuilder s = new StringBuilder();
        for(Card c : hand){
            s.append("\t");
            s.append(c.toString());
        }
        System.out.println(s);
    }

    /* how many cards are in the player's hand? */
    public int getHandSize(){
        return hand.size();
    }

    /* is the player's hand empty ? */
    public boolean handEmpty(){
        return hand.size() == 0;
    }

    /* returns a human readable ID of the player (their name) */
    public String toString(){
        return getName();
    }

    /* 
    display the current state of the player 
    i.e.: HAND AND SCORE
    */
    public void displayState(){
        String s = hand.toString();
        s = s.substring(1,s.length()-1);
        System.out.printf("%s:\tScore: %d\tHand: %s\n", name, score, s);
    }

    /* some simple information displayed the user about each player's turn */
    public void beginTurn(){
        System.out.println(name + "'s turn.");
        GoFish.printScores();
    }
    public void endTurn(){
        System.out.println(name + "'s score: " + score);
    }

}
