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
import java.util.HashMap;
import java.util.Map;

public class GoFish{

    /* initialize a scanner to get user input throughout the game */
    public static Scanner userIn = new Scanner(System.in);

    /* game state; true if initialize game, false otherwise*/
    public static boolean initState;

    /* Global playerlist */
    private static ArrayList<Player> allPlayers;

    /* Global map for probabilities */
    private static Map<Player, ArrayList<Integer>> probabilityInfoMap;

    /* Global Deck for Game */
    private static Deck deck;

    /* Global User Player */
    private static HumanPlayer user;

    /* Global pool of cards still in play */
    private static int[] available;

    public static void main(String[] args) throws IOException, InterruptedException{
        
        /* initialize beginning game state */
        initGame();

        /* choose player to begin game */
        /* currentPlayer stores the index of the player in the alllist */
        int player_index = chooseStartingPlayer();
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
                Player requestedPlayer = selectPlayer(currentPlayer);
                Card.Value requestedCard = selectCard(currentPlayer);
                System.out.println(currentPlayer + " asked " + requestedPlayer + " for a(n) " + Card.valueToString(requestedCard));

                /* ask the requestedPlayer for the requestedCard */
                if(!currentPlayer.cardRequest(requestedCard, requestedPlayer)){
                    alertGoFish(requestedPlayer, requestedCard);
                    goFish(currentPlayer);
                    turnOver = true;
                    System.out.println();
                }
                else{
                    /* let players know that this player has made a book of this card */
                    alertBook(currentPlayer, requestedCard, false);
                    /* display */
                    System.out.println("\n" + requestedPlayer + " handed " + currentPlayer + " the " + Card.valueToString(requestedCard));
                    System.out.println(currentPlayer + " created a book of " + Card.valueToString(requestedCard) + "s.");
                    /* update the player who gave away the card as well */
                    decrementAndZero(requestedPlayer, requestedCard);
                    System.out.println();
                }
                
                Thread.sleep(GameConstants.TIME_DELAY);
                /* END PHASE */
                /* update the win condition */
                gameOver = isGameOver(deck.deckEmpty(), currentPlayer, requestedPlayer);
            }
        }


        determineWinner();
    }

    public static void initGame() throws InterruptedException{
        initState = true;

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
        deck = new Deck();

        /* initialize available */
        available = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};

        /* list of all players
        room for all of the opponents and the human player*/
        allPlayers = new ArrayList<Player>(numOpponents+1);

        /* generate human player */
        user = new HumanPlayer(GameConstants.PLAYER_NAME, deck);
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

        /* initialize the probability map */
        initProbMap();

        /* deal 7 cards to each players */
        dealCards();

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

        initState = false;
    }

    public static void initProbMap(){
        /* create an entry in the probability map for each player */
        probabilityInfoMap = new HashMap<Player, ArrayList<Integer>>(allPlayers.size());

        /* default each entry to have 0 because cards have not been dealt yet */
        for(Player p : allPlayers){
            ArrayList<Integer> probabilityChart = new ArrayList<Integer>(Deck.NUM_VALUES);
            for(int i = 0; i < Deck.NUM_VALUES; i++){
                probabilityChart.set(i, 0);
            }

            probabilityInfoMap.put(p, probabilityChart);
        }

    }

    /* static routine that deals cards out to each player
    at the beginning of each game */
    public static void dealCards() throws InterruptedException{
        for(Player p: allPlayers){
            for(int i = 0; i < GameConstants.STARTING_HAND; i++){
                p.drawCard();
            }
            Thread.sleep(GameConstants.TIME_DELAY);
        }
    }


    /* chooses the starting player by having each player
    roll a random die. The player with the highest roll
    is the player who starts */
    public static int chooseStartingPlayer() throws InterruptedException{
        /* keeps track of the largest roll */
        int max_roll = 0;
        /* keeps track of the player with the largest roll */
        int starting_player = -1;
        /* a random number generate to generate the dice rolls */
        Random r = new Random();

        System.out.println("\n\nRolling 20 sided dice to decide starting player...");
        /* each player will roll a dice
        the player with the highest dice roll will go first */
        for(int i = 0; i < allPlayers.size(); i++){
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
            System.out.printf("%-"+(GameConstants.LONGEST_NAME_WIDTH+13)+"s %d\n",allPlayers.get(i).getName()+" rolled a...", players_roll);
        }

        Thread.sleep(GameConstants.TIME_DELAY);

        assert starting_player != -1;
        return starting_player;
    }

    /* method to verify and allow the currentPlayer to request a card from another player */
    public static Player selectPlayer(Player currentPlayer){
        if(currentPlayer instanceof HumanPlayer){
            /* prompt user for player selection */
            System.out.println("Which player would you like to request a card from? ");
            /* display players to select from. */
            /* Only prompts for players starting at computer player 1 */
            for(int i = 1; i < allPlayers.size(); i++){
                System.out.println(i + ". " + allPlayers.get(i));
            }
            int request;
            /* check that selection is valid */
            System.out.print("Selection: ");
            while((request = userIn.nextInt()) < 1 || request >= allPlayers.size()){
                System.out.println("That is not a valid choice.");
                System.out.print("Which player would you like to request a card from? ");
                for(int i = 1; i < allPlayers.size(); i++){
                    System.out.println(i + ". " + allPlayers.get(i));
                }
                System.out.print("Selection: ");
            }
            /*flush the input buffer of new line characters */
            userIn.nextLine();
            System.out.println();
            return allPlayers.get(request);
        }

        else{
            /* PLACEHOLDER AI Selects random player */
            Random r = new Random();
            Player playerChoice;
            while((playerChoice = allPlayers.get(r.nextInt(allPlayers.size()))) == currentPlayer){}

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

    public static void initialAlertBook(Player p, Card.Value value, boolean drewCard){
        /* decrement available pool of cards */
        available[value.ordinal()]-=2;
        decrementProbabilities(p);
        if(drewCard){
            decrementProbabilities(p);
        }
    }

    public static void alertBook(Player p, Card.Value value, boolean drewCard){
        /* decrement available pool of cards */
        available[value.ordinal()]-=2;
        /* zero out player for the given value*/
        decrementAndZero(p, value);
        if(drewCard){
            decrementProbabilities(p);
            probabilityInfoMap.get(p).set(value.ordinal(), 0);
        }

    }

    /*
    initialize map to all zeros
    decremeent inside of initialAlertBook
    increment inside of drawCard
    */

    public static void goFish(Player p){
        /* if they don't have it GO FISH */
        int oldScore = p.getScore();
        System.out.println("\nGO-FISH!");
        Card newCard = p.drawCard();
        if(oldScore != p.getScore()){
            System.out.println(p + " created a book of " + Card.valueToString(newCard.getValue()) + "s.");
        }
        System.out.println("TURN OVER");
    }

    public static void alertGoFish(Player r, Card.Value value){
        probabilityInfoMap.get(r).set(value.ordinal(), 0);
    }

    public static void incrementProbabilities(Player p){
        ArrayList<Integer> probabilityChart = probabilityInfoMap.get(p);
        for(int i = 0; i < probabilityChart.size(); i++){
            probabilityChart.set(i, probabilityChart.get(i)+1);
            if(probabilityChart.get(i) < 0){
                probabilityChart.set(i, 1);
            }
        }
    }

    public static void decrementProbabilities(Player p){
        ArrayList<Integer> probabilityChart = probabilityInfoMap.get(p);
        for(int i = 0; i < probabilityChart.size(); i++){
            probabilityChart.set(i, probabilityChart.get(i)-1);
        }
    }

    public static void decrementAndZero(Player p, Card.Value value){
        decrementProbabilities(p);
        /* zero out the card's value */
        probabilityInfoMap.get(p).set(value.ordinal(), 0);
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

    public static void determineWinner(){
        Player winner = null;
        int maxScore = 0;

        /* find the player with the max score */
        for(Player p: allPlayers){
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
        String outcome = winner == user ? "Congrats" : "Better luck next time";
        System.out.printf("%s %s\n", outcome, user.getName());
    }

    public static void clearScreen(){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }
}
