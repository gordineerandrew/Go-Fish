/*
## GameDriver
DONE * shuffle deck at start of game (shuffle 7 times for true shuffle)
DONE * deal 7 cards to each player
DONE * auto collect any books.
DONE * select player to take the first turn at random
### Game Loop
* DONE specify the current players turn
* STARTED choose card to look for
* STARTED choose player to look for card from
* DONE if selected correctly then book is created
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

    /* initialize a scanner to get user input throughout the game */
    public static Scanner userIn = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException{
        /* GAME SETUP */
        /* Prompt user for number of opponents to go against */
        System.out.print("How many opponents (1-3)? ");
        int numOpponents;
        /* while the player inputs a number not between 0 and the max prompt for another number */
        while(!((numOpponents = userIn.nextInt()) > 0 && numOpponents <= GameConstants.MAX_OPPONENTS)){
            System.out.println("Number of Opponents must be between 0 and " + GameConstants.MAX_OPPONENTS);
            System.out.print("How many opponents (1 - " + GameConstants.MAX_OPPONENTS + ")? ");
        }
        /*flush the input buffer of new line characters */
        userIn.nextLine();

        System.out.println();
        /* create the deck */
        Deck deck = new Deck();

        /* list of all players
        room for all of the opponents and the human player*/
        ArrayList<Player> allPlayers = new ArrayList<Player>(numOpponents+1);

        /* generate human player */
        HumanPlayer user = new HumanPlayer(GameConstants.PLAYER_NAME, deck);
        /* update the longest name width when another player is created */
        GameConstants.LONGEST_NAME_WIDTH = Math.max(GameConstants.LONGEST_NAME_WIDTH, user.getName().length());
        /* add to all player list to keep track of user */
        allPlayers.add(user);

        /* generate opponents */
        ArrayList<AIPlayer> opponents = new ArrayList<AIPlayer>(numOpponents);
        for(int i = 0; i < numOpponents; i++){
            AIPlayer opp = new AIPlayer("COM Player "+(i+1), deck);
            GameConstants.LONGEST_NAME_WIDTH = Math.max(GameConstants.LONGEST_NAME_WIDTH, opp.getName().length());

            /* add to both opplist and alllist so
            that the new opponent can be kept track of */
            opponents.add(opp);
            allPlayers.add(opp);
        }

        /* deal 7 cards to each players */
        dealCards(allPlayers);

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
        /* currentPlayer stores the index of the player in the alllist */
        int player_index = chooseStartingPlayer(allPlayers);
        System.out.printf("%s will begin the game\n", allPlayers.get(player_index));

        /* GAME LOOP */
        /* boolean to end game when no deck and no hands remain */
        boolean gameOver = false;
        
        while(!gameOver){
            Thread.sleep(GameConstants.TIME_DELAY);
            /* BEGINNING PHASE */
            Player currentPlayer = allPlayers.get(player_index++ % allPlayers.size());
            boolean turnOver = false;
            while(!turnOver && !gameOver){
                System.out.print("Press enter to continue the game.");
                System.in.read();
                clearScreen();
                /* SELECTION PHASE */
                currentPlayer.beginTurn();
                System.out.println();
                Player requestedPlayer = selectPlayer(allPlayers, currentPlayer);
                Card.Value requestedCard = selectCard(currentPlayer);
                System.out.println(currentPlayer + " asked " + requestedPlayer + " for a(n) " + Card.valueToString(requestedCard));

                /* ask the requestedPlayer for the requestedCard */
                if(!currentPlayer.cardRequest(requestedCard, requestedPlayer)){
                    /* if they don't have it GO FISH */
                    int oldScore = currentPlayer.getScore();
                    System.out.println("\nGO-FISH!");
                    Card newCard = currentPlayer.drawCard();
                    if(oldScore != currentPlayer.getScore()){
                        System.out.println(currentPlayer + " created a book of " + Card.valueToString(newCard.getValue()) + "s.");
                    }
                    System.out.println("TURN OVER");
                    turnOver = true;
                }
                else{
                    System.out.println("\n" + requestedPlayer + " handed " + currentPlayer + " the " + Card.valueToString(requestedCard));
                    System.out.println(currentPlayer + " created a book of " + Card.valueToString(requestedCard) + "s.");
                }
                System.out.println();
                Thread.sleep(GameConstants.TIME_DELAY);
                /* END PHASE */
                /* update the win condition */
                gameOver = isGameOver(deck.deckEmpty(), currentPlayer, requestedPlayer);
            }
        }


        determineWinner(allPlayers, user);
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

    /* method to verify and allow the currentPlayer to request a card from another player */
    public static Player selectPlayer(ArrayList<Player> players, Player currentPlayer){
        if(currentPlayer instanceof HumanPlayer){
            /* prompt user for player selection */
            System.out.println("Which player would you like to request a card from? ");
            /* display players to select from. */
            /* Only prompts for players starting at computer player 1 */
            for(int i = 1; i < players.size(); i++){
                System.out.println(i + ". " + players.get(i));
            }
            int request;
            /* check that selection is valid */
            System.out.print("Selection: ");
            while((request = userIn.nextInt()) < 1 || request >= players.size()){
                System.out.println("That is not a valid choice.");
                System.out.print("Which player would you like to request a card from? ");
                for(int i = 1; i < players.size(); i++){
                    System.out.println(i + ". " + players.get(i));
                }
                System.out.print("Selection: ");
            }
            /*flush the input buffer of new line characters */
            userIn.nextLine();
            System.out.println();
            return players.get(request);
        }

        else{
            /* PLACEHOLDER AI Selects random player */
            Random r = new Random();
            Player playerChoice;
            while((playerChoice = players.get(r.nextInt(players.size()))) == currentPlayer){}

            return playerChoice;
        }
    }

    public static Card.Value selectCard(Player currentPlayer){
        /* if the current player is a human... */
        if(currentPlayer instanceof HumanPlayer){
            /* prompt user for card selection based on current hand */
            System.out.print("Current hand: ");
            currentPlayer.displayHand();
            System.out.print("NOTE: T = 10\nSelect a card to request: ");
            Card.Value request;
            String requestString;
            /* if the input is not a real card or not in the current player's hand
            then reprompt the player */
            while((requestString = userIn.nextLine()).equals("") || (request = Card.charToValue(requestString.charAt(0))) == Card.Value.NOTAVALUE || currentPlayer.getCard(request) == null){
                System.out.println("That is not a valid choice.");
                System.out.print("Current hand: ");
                currentPlayer.displayHand();
                System.out.print("NOTE: T = 10\nSelect a card to request: ");
            }
            System.out.println();
            return request;
        }
        /* PLACEHOLDER edit this for the computer player */
        else{
            /* PLACEHOLDER AI Selects a random card */
            Random r = new Random();
            Card cardChoice;
            while((cardChoice = currentPlayer.getCard(Card.intToValue(r.nextInt(13) + 1))) == null){}
            return cardChoice.getValue();
        }

    }

    public static boolean isGameOver(boolean emptyDeck, Player current, Player other){
        boolean gameOver = false;
        if(emptyDeck){
            System.out.println("Deck is empty!");
            gameOver = true;
        }
        else if(current.handEmpty()){
            System.out.printf("%s's hand is empty!\n", current.getName());
            gameOver = true;
        }
        else if(other.handEmpty()){
            System.out.printf("%s's hand is empty!\n", other.getName());
            gameOver = true;
        }

        if(gameOver)
            System.out.println("GAME OVER!");

        return gameOver;
    }

    public static void determineWinner(ArrayList<Player> players, HumanPlayer hPlayer){
        Player winner = null;
        int maxScore = 0;

        /* find the player with the max score */
        for(Player p: players){
            int pScore = p.getScore();
            if(pScore > maxScore){
                maxScore = pScore;
                winner = p;
            }

            /* if there is a tie break it */
            else if(pScore == maxScore){
                if(winner != null)
                    winner = p.handEmpty() ? p : winner;
                else
                    winner = p;
            }
        }

        /* display the winner */
        System.out.printf("%s wins with a score of %d\n", winner.getName(), maxScore);
        /* print the human player's outcome message */
        String outcome = winner == hPlayer ? "Congrats" : "Better luck next time";
        System.out.printf("%s %s\n", outcome, hPlayer.getName());
    }
    public static void clearScreen(){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }
}
