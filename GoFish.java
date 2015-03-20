/*
## GameDriver
DONE * shuffle deck at start of game (shuffle 7 times for true shuffle)
DONE * deal 7 cards to each player
DONE * auto collect any books.
DONE * select player to take the first turn at random
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
import java.util.Random;

public class GoFish{

    /* GLOBAL CONSTANTS */
    private static final int MAX_OPPONENTS = 3;
    private static final int STARTING_HAND = 7;
    private static final int TIME_DELAY = 1000;
    private static final boolean DEBUG = true;

    public static void main(String[] args) throws InterruptedException{
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

        /* list of all players
        room for all of the opponents and the human player*/
        ArrayList<Player> all_players = new ArrayList<Player>(numOpponents+1);

        /* generate human player */
        HumanPlayer user = new HumanPlayer("Player 1", deck);
        /* add to all player list to keep track of user */
        all_players.add(user);

        /* generate opponents */
        ArrayList<AIPlayer> opponents = new ArrayList<AIPlayer>(numOpponents);
        for(int i = 0; i < numOpponents; i++){
            AIPlayer opp = new AIPlayer("Computer "+(i+1), deck);
            /* add to both opplist and alllist so
            that the new opponent can be kept track of */
            opponents.add(opp);
            all_players.add(opp);
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

        /* choose player to begin game */
        /* current_player stores the index of the player in the alllist */
        int current_player = chooseStartingPlayer(all_players);
        System.out.printf("%s will begin the game\n", all_players.get(current_player));
        System.out.println("Press any key and then ENTER to let the game begin");
        userIn.next();

        /* begin game loop */

    }

    /* static routine that deals cards out to each player
    at the beginning of each game */
    public static void dealCards(Player p){
        for(int i = 0; i < STARTING_HAND; i++){
            Card c = p.drawCard();
            if(DEBUG && c!=null)
                System.out.print(c+" ");
        }

        if(DEBUG)
            System.out.println();
    }

    /* chooses the starting player by having each player
    roll a random die. The player with the highest roll
    is the player who starts */
    public static int chooseStartingPlayer(ArrayList<Player> playerlist) throws InterruptedException{
        /* keeps track of the largest roll */
        int max_roll = 0;
        /* keeps track of the player with the largest roll */
        int starting_player = -1;
        /* a random number generate to generate the dice rolls */
        Random r = new Random();

        System.out.println("Rolling 20 sided dice to decide starting player...");
        /* each player will roll a dice
        the player with the highest dice roll will go first */
        for(int i = 0; i < playerlist.size(); i++){
            /* wait for half a second to give player time to read info */
            Thread.sleep(TIME_DELAY);

            /* roll a number 1 - 20 */
            int players_roll = r.nextInt(20)+1;
            if(players_roll > max_roll){
                max_roll = players_roll;
                starting_player = i;
            }

            /* display the player's roll */
            System.out.printf("%s rolled a... \t%d\n", playerlist.get(i), players_roll);
        }

        Thread.sleep(TIME_DELAY);

        assert starting_player != -1;
        return starting_player;
    }
}
