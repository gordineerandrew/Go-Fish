/*
## GameDriver
* shuffle deck at start of game (shuffle 7 times for true shuffle)
* deal 7 cards to each player
* auto collect any books.
* select player to take the first turn at random
### Game Loop
* specify the current players turn
* choose card to look for
* choose player to look for card from
* confirm that match is appropriate
* if not redo
* if selected correctly then book is created
    * gets to go another turn
* else go fish
    * draw card from deck if there are any
    * END TURN

### MISC GAME LOOP INFO
* during player turns go through each of these steps waiting for inputs
* during AI turns, have minor delay so that player isn't overrun with information
*/

/* imported utilities */
import java.util.Scanner;
import java.util.ArrayList;

public class GoFish{

    /* GLOBAL CONSTANTS */
    private static final int MAX_OPPONENTS = 3;
    private static final int STARTING_HAND = 7;
    private static final boolean DEBUG = true;

    public static void main(String[] args){
        /*
        GAME SETUP
        */

        /* initialize a scanner to get user input throughout the game */
        Scanner userIn = new Scanner(System.in);

        /* PROMPT USER FOR NUMBER OF AI PLAYERS TO PLAY AGAINST */
        System.out.print("How many opponents (1-3)? ");
        int numOpponents;
        /* while the player inputs a number not between 0 and the max prompt for another number */
        while(!((numOpponents = userIn.nextInt()) > 0 && numOpponents <= MAX_OPPONENTS)){
            System.out.println("Number of Opponents must be between 0 and " + MAX_OPPONENTS);
            System.out.print("How many opponents (1-3)? ");
        }

        /* create the deck */
        Deck deck = new Deck();

        /* generate human player */
        HumanPlayer user = new HumanPlayer("Player 1", deck);

        /* generate opponents */
        ArrayList<AIPlayer> opponents = new ArrayList<AIPlayer>(numOpponents);
        for(int i = 0; i < numOpponents; i++){
            opponents.add(new AIPlayer("Opponent "+(i+1), deck));
        }

        /* deal 7 cards to each players */
        /* deal cards to human player */
        dealCards(user);
        user.displayState();
        /* deal starting hand to each opponent */
        for(int i = 0; i < numOpponents; i++){
            AIPlayer opp = opponents.get(i);
            dealCards(opp);
            opp.displayState();
        }



    }

    public static void dealCards(Player p){
        for(int i = 0; i < STARTING_HAND; i++){
            Card c = p.drawCard();
            if(DEBUG && c!=null)
                System.out.print(c+" ");
        }

        if(DEBUG)
            System.out.println();
    }
}
