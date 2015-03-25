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
import java.io.IOException;

public class GoFish{


    public static void main(String[] args) throws IOException, InterruptedException{
        /*
        GAME SETUP
        */

        /* initialize a scanner to get user input throughout the game */
        Scanner userIn = new Scanner(System.in);

        /* PROMPT USER FOR NUMBER OF AI PLAYERS TO PLAY AGAINST */
        System.out.print("How many opponents (1-3)? ");
        int numOpponents;
        /* while the player inputs a number not between 0 and the max prompt for another number */
        while(!((numOpponents = userIn.nextInt()) > 0 && numOpponents <= GameConstants.MAX_OPPONENTS)){
            System.out.println("Number of Opponents must be between 0 and " + GameConstants.MAX_OPPONENTS);
            System.out.print("How many opponents (1-" + GameConstants.MAX_OPPONENTS + ")? ");
        }
        System.out.println();
        /* create the deck */
        Deck deck = new Deck();

        /* list of all players
        room for all of the opponents and the human player*/
        ArrayList<Player> all_players = new ArrayList<Player>(numOpponents+1);

        /* generate human player */
        HumanPlayer user = new HumanPlayer(GameConstants.PLAYER_NAME, deck);
        /* update the longest name width when another player is created */
        GameConstants.LONGEST_NAME_WIDTH = Math.max(GameConstants.LONGEST_NAME_WIDTH, user.getName().length());
        /* add to all player list to keep track of user */
        all_players.add(user);

        /* generate opponents */
        ArrayList<AIPlayer> opponents = new ArrayList<AIPlayer>(numOpponents);
        for(int i = 0; i < numOpponents; i++){
            AIPlayer opp = new AIPlayer("COM Player "+(i+1), deck);
            GameConstants.LONGEST_NAME_WIDTH = Math.max(GameConstants.LONGEST_NAME_WIDTH, opp.getName().length());

            /* add to both opplist and alllist so
            that the new opponent can be kept track of */
            opponents.add(opp);
            all_players.add(opp);
        }

        /* deal 7 cards to each players */
        dealCards(all_players);

        /* Prints the deck after cards have been dealt. */
        if(GameConstants.DEBUG){
            System.out.println(deck);
        }

        System.out.println("\n");
        /* Display all players information */
        user.displayState();
        if(GameConstants.DEBUG){
            for(int i = 0; i < numOpponents; i++){
                AIPlayer opp = opponents.get(i);
                opp.displayState();
            }
        }

        /* choose player to begin game */
        /* current_player stores the index of the player in the alllist */
        int player_index = chooseStartingPlayer(all_players);
        System.out.printf("%s will begin the game\n", all_players.get(player_index));
        System.out.println("Press ENTER to let the game begin");
        System.in.read();

        /* begin game loop */
        /* boolean to end game when no deck and no hands remain */
        boolean gameOver = false;

        while(!gameOver){

            Player current_player = all_players.get(player_index++ % all_players.size());
            System.out.println(current_player + "'s turn.\n");

            // CODE FOR HUMAN PLAYER
            // System.out.println("Choose the number of the player you'd like to choose a card from...");
            // /* Prints out all opponent options and choices to the left */
            // for(int k = 1; k <= numOpponents; k++){
            //     System.out.println(k + ". " + all_players.get(k));
            // }
            // System.out.print("Choice: ");
            // int player_choice = userIn.nextInt();
            // System.out.println();
            // System.out.println("Choose the number of the card you'd like to ask for...");
            // /* Prints out all card choices in hand and choices to the left */
            // for(int j = 0; j < all_players.get(playOrder[i]).hand.size(); j++){
            //     System.out.println((j +1) + ". " + all_players.get(playOrder[i]).get(j));
            // }
            // System.out.print("Choice: ");
            // int card_choice = userIn.nextInt();
            // System.out.println();
            //
            //

            /* GameConstants.DEBUG and player_index > 10 are just an arbitrary quit condition
            until lose conditions are established */
            if((GameConstants.DEBUG && player_index >  10) || player_index > 10)
                gameOver = true;
        }
    }

    /* static routine that deals cards out to each player
    at the beginning of each game */
    public static void dealCards(ArrayList<Player> players)throws InterruptedException{
        for(Player p: players){
            for(int i = 0; i < GameConstants.STARTING_HAND; i++){
                p.drawCard();
            }
            Thread.sleep(GameConstants.TIME_DELAY);
        }

        // if(GameConstants.DEBUG){
        //     System.out.print(p.toString() + "\t was dealt: ");
        //             for(int i = 0; i < GameConstants.STARTING_HAND; i++){
        //                 Card c = p.drawCard();
        //                 if(GameConstants.DEBUG && c!=null)
        //                     System.out.print(c+" ");
        //             }
        //             if(GameConstants.DEBUG)
        //                 System.out.println();
        // /* else if human, print what player is dealt */
        // }else if(p.toString().equals(GameConstants.PLAYER_NAME)){
        //     System.out.print(p.toString() + " was dealt: ");
        //     for(int i = 0; i < GameConstants.STARTING_HAND; i++){
        //         Card c = p.drawCard();
        //         System.out.print(c+" ");
        //     }
        //     System.out.println();
        //     Thread.sleep(GameConstants.TIME_DELAY);
        // /* else if computer, hide what computer is dealt */
        // }else{
        //     for(int i = 0; i < GameConstants.STARTING_HAND; i++){
        //         Card c = p.drawCard();
        //     }
        //     System.out.println(GameConstants.STARTING_HAND + " cards were dealt to " + p.toString());
        //     Thread.sleep(GameConstants.TIME_DELAY);
        // }

    }


    /* chooses the starting player by having each player
    roll a random die. The player with the highest roll
    is the player who starts */
    public static int chooseStartingPlayer(ArrayList<Player> playerlist)
                throws InterruptedException{
        /* keeps track of the largest roll */
        int max_roll = 0;
        /* keeps track of the player with the largest roll */
        int starting_player = -1;
        /* a random number generate to generate the dice rolls */
        Random r = new Random();

        System.out.println("\n\nRolling 20 sided dice to decide starting player...");
        /* each player will roll a dice
        the player with the highest dice roll will go first */
        for(int i = 0; i < playerlist.size(); i++){
            /* wait for a second to give player time to read info */
            Thread.sleep(GameConstants.TIME_DELAY);

            /* roll a number 1 - 20 */
            int players_roll = r.nextInt(20)+1;
            if(players_roll > max_roll){
                max_roll = players_roll;
                starting_player = i;
            }

            /* display the player's roll */
            /* use the longest player's name + 13 characters for the " rolled a..." */
            System.out.printf("%-"+(GameConstants.LONGEST_NAME_WIDTH+13)+"s %d\n",playerlist.get(i).getName()+" rolled a...", players_roll);
        }

        Thread.sleep(GameConstants.TIME_DELAY);

        assert starting_player != -1;
        return starting_player;
    }
}
