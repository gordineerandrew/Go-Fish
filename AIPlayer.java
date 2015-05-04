/*
File:         AIPlayer.java
Created:      2015/03/21
Last Changed: 2015/04/30
Author:      Scott Munro <scottnmunro@gmail.com>
             Andrew Gordineer <gordineerandrew@gmail.com>

This file defines the behavior of all the non-human players
in every GoFish game.
*/


public class AIPlayer extends Player{

    /* CONSTRUCTR */
    public AIPlayer(String name, Deck deckRef){
        /* call the Player constructor */
        super(name, deckRef);
    }

    /* 
    draws a card for the AI Player,
    if debuging is turned on then extra information is display 
    */
    public Card drawCard(){
        Card c = super.drawCard();
        if(GameConstants.DEBUG && c != null){
            System.out.println(this.name + " drew: " + c);
        }
        return c;
    }

}
