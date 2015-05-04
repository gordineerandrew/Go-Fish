/*
File:         HumanPlayer.java
Created:      2015/03/21
Last Changed: 2015/04/30
Author:      Scott Munro <scottnmunro@gmail.com>
             Andrew Gordineer <gordineerandrew@gmail.com>

Extra auxillary functions need for human players 
*/

public class HumanPlayer extends Player{
    public HumanPlayer(String name, Deck deckRef){
        super(name, deckRef);
    }
    public Card drawCard(){
        Card c = super.drawCard();
        if(c != null){
           if(!GameConstants.HEADLESS) System.out.println(this.name + " drew: " + c);
        }
        return c;
    }

    public void beginTurn(){
        super.beginTurn();
       if(!GameConstants.HEADLESS) System.out.printf("%-22s","Current hand:");
        displayHand();
        GoFish.getProbabilities();
    }
    public void endTurn(){
        super.endTurn();
       if(!GameConstants.HEADLESS) System.out.print("Current hand: ");
        displayHand();
    }

    public Card.Value[] cardsInHand(){
        Card.Value[] cards = new Card.Value[hand.size()];
        for(int i = 0; i < hand.size(); i++){
            cards[i] = hand.get(i).getValue();
        }
        return cards;
    }
}
