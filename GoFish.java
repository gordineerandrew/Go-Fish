/*
File:         GoFish.java
Created:      2015/03/21
Last Changed: 2015/04/30
Author:      Scott Munro <scottnmunro@gmail.com>
             Andrew Gordineer <gordineerandrew@gmail.com>

Manages the GoFish simulation, user input, and displaying information
to the player
*/

/* imported utilities */
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Formatter;
import java.util.Locale;

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
    private static Player user;

    /* Global pool of cards still in play */
    private static int[] available;

    /* the main game loop */
    public static void main(String[] args) throws IOException, InterruptedException{

        /* initialize beginning game state */
        initGame();

        /* choose player to begin game */
        /* currentPlayer stores the index of the player in the alllist */
        int player_index = chooseStartingPlayer();
       if(!GameConstants.HEADLESS) System.out.printf("%s will begin the game\n", allPlayers.get(player_index));

        /* GAME LOOP */
        /* boolean to end game when no deck and no hands remain */
        boolean gameOver = false;

        while(!gameOver){

            /* BEGINNING PHASE */
            /* select the player whose turn is next */
            Player currentPlayer = allPlayers.get(player_index++ % allPlayers.size());
            boolean turnOver = false;

            while(!turnOver && !gameOver){
                /* Wait for the player to advance the game */
                if(!GameConstants.HEADLESS){
                    System.out.print("Press enter to continue the game.");
                    if(!GameConstants.AUTO) System.in.read();
                    clearScreen();
                } 

                /* SELECTION PHASE */
                currentPlayer.beginTurn();
                if(!GameConstants.HEADLESS) System.out.println();
                /* ask the current player who they would like to request a card from */
                Player requestedPlayer = selectPlayer(currentPlayer);
                /* ask the current player what card they would like to request */
                Card.Value requestedCard = selectCard(currentPlayer);
                /* tell the rest of the players that the current player has requested a specific card */
                alertRequest(currentPlayer, requestedCard);
                if(!GameConstants.HEADLESS) System.out.println(currentPlayer + " asked " + requestedPlayer + " for a(n) " + Card.valueToString(requestedCard));

                /* ask the requestedPlayer for the requestedCard */
                if(!currentPlayer.cardRequest(requestedCard, requestedPlayer)){
                    /* the player has guessed incorrectly!... 
                    alert the other players that the requested player did not have the card */
                    alertGoFish(requestedPlayer, requestedCard);
                    /* player executes the GoFish action */
                    goFish(currentPlayer);
                    /* end of turn */
                    turnOver = true;
                    if(!GameConstants.HEADLESS) System.out.println();
                }
                else{
                    /* the player has guessed CORRECTLY!... */
                    if(!GameConstants.HEADLESS) {
                        System.out.println("\n" + requestedPlayer + " handed " + currentPlayer + " the " + Card.valueToString(requestedCard));
                        System.out.println(currentPlayer + " created a book of " + Card.valueToString(requestedCard) + "s.");
                    }
                    /* inform the rest of the players that the requested 
                    player no longer has the requested Card */
                    decrementAndZero(requestedPlayer, requestedCard);
                    if(!GameConstants.HEADLESS) System.out.println();
                }

                /* END PHASE */
                /* update the win condition */
                gameOver = isGameOver(deck.deckEmpty(), currentPlayer, requestedPlayer);
            }
        }

        /* when the game is over determine who has won */
        determineWinner();
    }

    /*
    Initializes the game state:
    this means:
    - creating every player
    - initializing the map used to track the 
        probability of each requested card for each player...
    - dealing out all of the cards
     */
    public static void initGame() throws InterruptedException{
        /* global variable used to inform other components that the game
        is being initialized */
        initState = true;

        /* Prompt user for number of opponents to go against */
        if(!GameConstants.HEADLESS) System.out.print("How many opponents (1-3)? ");
        int numOpponents;
        /* while the player inputs a number not between 0 and the max prompt for another number */
        if(!GameConstants.AUTO){
            while(!((numOpponents = userIn.nextInt()) > 0 && numOpponents <= GameConstants.MAX_OPPONENTS)){
               if(!GameConstants.HEADLESS){
                    System.out.println("Number of Opponents must be between 0 and " + GameConstants.MAX_OPPONENTS);
                    System.out.print("How many opponents (1 - " + GameConstants.MAX_OPPONENTS + ")? ");
               } 
            }

            /*flush the input buffer of new line characters */
            userIn.nextLine();
            if(!GameConstants.HEADLESS) System.out.println();
        }

        else{
            numOpponents = 3;
        }

        /* create the deck */
        deck = new Deck();

        /* initialize available */
        available = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};

        /* list of all players
        room for all of the opponents and the human player*/
        allPlayers = new ArrayList<Player>(numOpponents+1);

        /* generate human player */
        if(GameConstants.AUTO)
            user = new AutoHumanPlayer(GameConstants.PLAYER_NAME, deck);
        else
            user = new HumanPlayer(GameConstants.PLAYER_NAME, deck);

        GameConstants.LONGEST_NAME_WIDTH = Math.max(GameConstants.LONGEST_NAME_WIDTH, user.getName().length());
        allPlayers.add(user);

        /* generate opponents */
        ArrayList<AIPlayer> opponents = new ArrayList<AIPlayer>(numOpponents);
        for(int i = 0; i < numOpponents; i++){
            AIPlayer opp = new AIPlayer("NPC-"+(i+1), deck);
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

        /* DEBUG: Prints the deck after cards have been dealt. */
        if(GameConstants.DEBUG && !GameConstants.HEADLESS){
           System.out.println(deck);
        }
       if(!GameConstants.HEADLESS) System.out.println("\n");

        /* Display all players information */
        user.displayState();
        if(GameConstants.DEBUG){
            for(int i = 0; i < numOpponents; i++){
                AIPlayer opp = opponents.get(i);
                opp.displayState();
            }
        }

        /* inform other components to begin normal operation */
        initState = false;
    }

    /*
    initializes the map used to track how many cards
    in a players hand could possibly be a given card
    */
    public static void initProbMap(){
        /* create an entry in the probability map for each player */
        probabilityInfoMap = new HashMap<Player, ArrayList<Integer>>(allPlayers.size());

        /* default each entry to have 0 because cards have not been dealt yet */
        for(Player p : allPlayers){
            ArrayList<Integer> probabilityChart = new ArrayList<Integer>(Deck.NUM_VALUES);
            for(int i = 0; i < Deck.NUM_VALUES; i++){
                probabilityChart.add(0);
            }

            probabilityInfoMap.put(p, probabilityChart);
        }

    }

    /* 
    static routine that deals cards out to each player
    at the beginning of each game
    */
    public static void dealCards() throws InterruptedException{
        for(Player p: allPlayers){
            for(int i = 0; i < GameConstants.STARTING_HAND; i++){
                p.drawCard();
                if(p instanceof HumanPlayer)
                    delay(GameConstants.TIME_DELAY/2);
            }
        }
    }


    /* 
    chooses the starting player by having each player
    roll a random die. The player with the highest roll
    is the player who starts 
    */
    public static int chooseStartingPlayer() throws InterruptedException{
        /* keeps track of the largest roll */
        int max_roll = 0;
        /* keeps track of the player with the largest roll */
        int starting_player = -1;
        /* a random number generate to generate the dice rolls */
        Random r = new Random();

       if(!GameConstants.HEADLESS) System.out.println("\n\nRolling 20 sided dice to decide starting player...");

        /* each player will roll a dice
        the player with the highest dice roll will go first */
        for(int i = 0; i < allPlayers.size(); i++){
            /* wait for a second to give player time to read info */
            delay(GameConstants.TIME_DELAY);

            /* roll a number 1 - 20 */
            int players_roll = r.nextInt(20)+1;
            if(players_roll > max_roll){
                max_roll = players_roll;
                starting_player = i;
            }

            /* display the player's roll */
            /* use the longest player's name + 13 characters for the " rolled a..." */
           if(!GameConstants.HEADLESS) System.out.printf("%-"+(GameConstants.LONGEST_NAME_WIDTH+13)+"s %d\n",allPlayers.get(i).getName()+" rolled a...", players_roll);
        }

        delay(GameConstants.TIME_DELAY);

        /* sanity check to ensure that a starting player was set */
        assert starting_player != -1;
        return starting_player;
    }

    /* 
    method to verify and allow the currentPlayer 
    to request a card from another player 

    inputs:
    currentPlayer - the current player who must select another player to request from

    returns:
    the player who is selected 
    */
    public static Player selectPlayer(Player currentPlayer){

        /* AUTO HUMAN BEHAVIOR */
        if(currentPlayer instanceof AutoHumanPlayer){
            AutoHumanPlayer autop = (AutoHumanPlayer)currentPlayer;
            return autop.getChoice().p;
        }

        /* HUMAN BEHAVIOR */
        else if(currentPlayer instanceof HumanPlayer){
            /* prompt user for player selection */
           if(!GameConstants.HEADLESS) System.out.println("Which player would you like to request a card from? ");
            /* display players to select from. */
            /* Only prompts for players starting at computer player 1 */
            for(int i = 1; i < allPlayers.size(); i++){
               if(!GameConstants.HEADLESS) System.out.println(i + ". " + allPlayers.get(i));
            }
            int request;
            /* check that selection is valid */
            if(!GameConstants.HEADLESS) System.out.print("Selection: ");
            while((request = userIn.nextInt()) < 1 || request >= allPlayers.size()){
                if(!GameConstants.HEADLESS){
                    System.out.println("That is not a valid choice.");
                    System.out.print("Which player would you like to request a card from? ");
                } 

                for(int i = 1; i < allPlayers.size(); i++){
                   if(!GameConstants.HEADLESS) System.out.println(i + ". " + allPlayers.get(i));
                }
               if(!GameConstants.HEADLESS) System.out.print("Selection: ");
            }
            /*flush the input buffer of new line characters */
            userIn.nextLine();
           if(!GameConstants.HEADLESS) System.out.println();
            return allPlayers.get(request);
        }

        /* AI BEHAVIOR */
        else{
            /* AI Selects random player */
            Random r = new Random();
            Player playerChoice;
            /* randomly select players until a player that is not 
            the current player is selected */
            while((playerChoice = allPlayers.get(r.nextInt(allPlayers.size()))) == currentPlayer){}

            return playerChoice;
        }
    }

    /* 
    method to verify and allow the current player 
    to select a card to request 

    inputs:
    currentPlayer - the player who must select a card from their hand to request

    returns:
    the value of the card they select
    */
    public static Card.Value selectCard(Player currentPlayer){
        /* AUTO HUMAN BEHAVIOR */
        if(currentPlayer instanceof AutoHumanPlayer){
            AutoHumanPlayer autop = (AutoHumanPlayer)currentPlayer;
            return autop.getChoice().c;
        }

        /* HUMAN BEHAVIOR */
        else if(currentPlayer instanceof HumanPlayer){
            /* prompt user for card selection based on current hand */
            if(!GameConstants.HEADLESS) System.out.print("Current hand: ");
            currentPlayer.displayHand();
            if(!GameConstants.HEADLESS) System.out.print("NOTE: T = 10\nSelect a card to request: ");
            Card.Value request;
            String requestString;
            /* if the input is not a real card or not in the current player's hand
            then reprompt the player */
            while((requestString = userIn.nextLine()).equals("") || (request = Card.charToValue(requestString.charAt(0))) == Card.Value.NOTAVALUE || currentPlayer.getCard(request) == null){
                if(!GameConstants.HEADLESS){
                    System.out.println("That is not a valid choice.");
                    System.out.print("Current hand: ");
                }

                currentPlayer.displayHand();
                if(!GameConstants.HEADLESS) System.out.print("NOTE: T = 10\nSelect a card to request: ");
            }
            if(!GameConstants.HEADLESS) System.out.println();
            return request;
        }

        else{
            /* AI Selects a random card */
            Random r = new Random();
            Card cardChoice;
            /* randomly pick cards until a card that is in 
            the current AI player's hand is selected */
            while((cardChoice = currentPlayer.getCard(Card.intToValue(r.nextInt(13) + 1))) == null){}
            return cardChoice.getValue();
        }
    }

    /* 
    a function for updating the probability map when a book is created
    specifically at the beginning of the game during initialization 

    inputs:
    p - the player who created the book
    value - the value of the book
    drewCard - was the book created from drawing a card?
    */
    public static void initialAlertBook(Player p, Card.Value value, boolean drewCard){
        /* decrement available pool of cards */
        available[value.ordinal()]-=2;
        /* decrement the number of cards in a players 
        hand to account for the new book */
        decrementProbabilities(p);
        if(drewCard){
            decrementProbabilities(p);
        }
    }

    /* 
    updates the probability map when a book is created 

    inputs:
    p - the player who created the book
    value - the value of the book
    drewCard - was the book created from drawing a card?
    */
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
    a function that handles when a player must go fish 

    inputs:
    p - the player who must "GoFish"
    */
    public static void goFish(Player p){
        /* keep track of their old score for display purposes */
        int oldScore = p.getScore();
        if(!GameConstants.HEADLESS) System.out.println("\nGO-FISH!");
        Card newCard = p.drawCard();
        /* if they created a book (score changed) display that */
        if(oldScore != p.getScore()){
           if(!GameConstants.HEADLESS) System.out.println(p + " created a book of " + Card.valueToString(newCard.getValue()) + "s.");
        }
        if(!GameConstants.HEADLESS) System.out.println("TURN OVER");
    }

    /*
    a function to alert all players when one player does not have a card
    as evidenced by a GoFish

    inputs:
    r - the requestedPlayer who did not have a card
    value - the card value they did not have
    */
    public static void alertGoFish(Player r, Card.Value value){
        probabilityInfoMap.get(r).set(value.ordinal(), 0);
    }

    /* 
    a function to alert the other players that a player has requested
    another player for a card. This is done to let other players know
    that the requestingPlayer 100% has a card

    inputs:
    p - the player who is requested a card
    value - the requested card 
    */
    public static void alertRequest(Player p, Card.Value value){
        probabilityInfoMap.get(p).set(value.ordinal(), 1000);
    }

    /* 
    used to update the probability map to account for new
    cards coming into a players hand 

    inputs: 
    p - the player who has gained a card
    */
    public static void incrementProbabilities(Player p){
        ArrayList<Integer> probabilityChart = probabilityInfoMap.get(p);
        for(int i = 0; i < probabilityChart.size(); i++){
            /* increment the number of cards that could a given card in a players hand by 1 */
            probabilityChart.set(i, probabilityChart.get(i)+1);
            /* any card that has a nonpositive probability is incremented to 1 */
            if(probabilityChart.get(i) < 0){
                probabilityChart.set(i, 1);
            }
        }
    }

    /*
    used to update the probability map to account for a player
    losing a card

    inputs:
    p - the player who has lost a card 
    */
    public static void decrementProbabilities(Player p){
        ArrayList<Integer> probabilityChart = probabilityInfoMap.get(p);
        for(int i = 0; i < probabilityChart.size(); i++){
            if(probabilityChart.get(i) != 0)
                probabilityChart.set(i, probabilityChart.get(i)-1);
        }
    }

    /* 
    used to a update a probability to account for a lost card as well
    as a card that is definitely not in the players hand anymore 
    
    inputs:
    p - the player who has lost a card and definitely does not have a card
    value - the value of the card that the player p definitely does not have
    */
    public static void decrementAndZero(Player p, Card.Value value){
        decrementProbabilities(p);
        /* zero out the card's value */
        probabilityInfoMap.get(p).set(value.ordinal(), 0);
    }

    /*
    a function to decide if the game is over 

    inputs:
    emptyDeck - is the deck empty?
    current - the player whose turn is ending
    other - the player who was interacted with by the current player

    returns:
    whether the gmae is over or not 
    */
    public static boolean isGameOver(boolean emptyDeck, Player current, Player other){
        boolean gameOver = false;

        /* is the deck empty */
        if(emptyDeck){
            if(!GameConstants.HEADLESS) System.out.println("Deck is empty!");
            gameOver = true;
        }

        /* is the current player's hand empty?*/
        else if(current.handEmpty()){
            if(!GameConstants.HEADLESS) System.out.printf("%s's hand is empty!\n", current.getName());
            gameOver = true;
        }

        /* is the interacted-with player's hand empty? */
        else if(other.handEmpty()){
            if(!GameConstants.HEADLESS) System.out.printf("%s's hand is empty!\n", other.getName());
            gameOver = true;
        }

        if(gameOver)
            if(!GameConstants.HEADLESS) System.out.println("GAME OVER!");

        return gameOver;
    }

    /* 
    a routine used to determine which player has won the game

    done by simply iterating over the list and finding the player
    with the highest score 
    */
    public static void determineWinner() throws IOException{
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
        if(!GameConstants.HEADLESS){ 
            System.out.printf("%s wins with a score of %d\n", winner.getName(), maxScore);
            /* print the human player's outcome message */
            String outcome = winner == user ? "Congrats" : "Better luck next time";
            System.out.printf("%s %s\n", outcome, user.getName());
        }

        if(GameConstants.LOG){
            StringBuilder outcome = new StringBuilder();
            outcome.append(winner == user ? "WIN " : "LOSE ");
            outcome.append(scoresToString());
            HeadlessLogger.out.println(outcome.toString());  
            if(winner == user)
                HeadlessLogger.wins++;
        }
    }

    /* 
    Displays all of the probabilities to the human player
    so that they may make the most effective guess possible 
    */
    public static void getProbabilities(){

        ArrayList<ArrayList<PlayerCardTuple>> probabilitySet = new ArrayList<ArrayList<PlayerCardTuple>>(allPlayers.size()-1);

        /* Starting at 1 to not include the human player */     
        for(int i = 1; i < allPlayers.size(); i++){

            ArrayList<PlayerCardTuple> singleSet = new ArrayList<PlayerCardTuple>();
           
            if(!GameConstants.HEADLESS) System.out.print(allPlayers.get(i).toString() + "'s probabilities: ");

            Card.Value cards[] = user.cardsInHand();

            for(int j = 0; j < cards.length; j++){
                double probability = calculateProbability(allPlayers.get(i), cards[j]);
                singleSet.add(new PlayerCardTuple(allPlayers.get(i), cards[j], probability));
                if(!GameConstants.HEADLESS) System.out.printf("\t%.1f%%",probability);
            }

            probabilitySet.add(singleSet);

            if(!GameConstants.HEADLESS) System.out.println();
        }

        if(user instanceof AutoHumanPlayer){
            AutoHumanPlayer auto_user = (AutoHumanPlayer)user;
            auto_user.automateChoice(probabilitySet);
        }
    }

    /* 
    Calculates the probability for a requested card form a requested player using what
    is known about the current game state: how many unknown cards there are, how many
    unknown cards are in the requested players hand etc.

    Basic formula...
    X = the number of cards in the requested Player's hand that could be the requestedCard
    Y = total unknown cards in the game that could be the requested card
    Z = How many of the requestedCard are remaining in the game that are not in your hand

    (X/Y) * Z

    inputs:
    requestedPlayer - the player who you are calculating the probabilty of having "card" card
    card - the card to calculate probabilities for
    */
    public static double calculateProbability(Player requestedPlayer, Card.Value card){
        /* keep track of the unknown number of cards in all hands */
        int unknownInHands = 0;
        /* how many of "card" are remaining */
        int numRemaining = available[card.ordinal()];
        /* how many unknown cards in requestedPlayer's hand could be "card" */
        int unknownInRequestedHand = 0;

        for(int i = 1; i < allPlayers.size(); i++){
            Player currentPlayer = allPlayers.get(i);
            int possibleNumInHand = probabilityInfoMap.get(currentPlayer).get(card.ordinal());

            /* normal case: the player has some nonzero probability of having the requested card */ 
            if(possibleNumInHand <= GameConstants.GUARANTEED_CARD || currentPlayer == requestedPlayer)
                unknownInHands += possibleNumInHand;
            
            /* special case: there is no way the currentPlayer has the specified card 
            the human player has 1 of the cards and the requestdPlayer has the other so this third player
            cannot have it*/
            else if(numRemaining == 2)
                return 0;

            /* mark unknown in requested player's hand */
            if(currentPlayer == requestedPlayer)
                unknownInRequestedHand = possibleNumInHand;
            
        }

        /* return 100 if the player is guaranteed to have this card */
        if(unknownInRequestedHand > GameConstants.GUARANTEED_CARD)
            return 100;

        /* Return 0 if all cards are known not to be card. Fixes NaN issue */
        if(unknownInHands == 0)
            return 0;

        /* calculate the probability */
        int totalUnknown = unknownInHands + deck.size();
        double probability = (100.0*(numRemaining-1))/totalUnknown;
        probability *= unknownInRequestedHand;

        /* DEBUG INFO */
        if(GameConstants.DEBUG){
           if(!GameConstants.HEADLESS) System.out.println("\nunknownInHands = " + unknownInHands);
           if(!GameConstants.HEADLESS) System.out.println("TotalUnknown = " + totalUnknown);
           if(!GameConstants.HEADLESS) System.out.println("unknownInRequestedHand = " + unknownInRequestedHand);
           if(!GameConstants.HEADLESS) System.out.println("numRemaining = " + (numRemaining-1));
           if(!GameConstants.HEADLESS) System.out.println();
        }

        return probability;
    }

    /* Method to print out the scores of all of the players in the game */
    public static String scoresToString(){
        StringBuilder s = new StringBuilder();
        Formatter f = new Formatter(s, Locale.US);
        f.format("SCORES\t");
        for(int i = 0; i < allPlayers.size(); i++){
           f.format("%8s: %d\t", allPlayers.get(i).toString(), allPlayers.get(i).getScore());
        }
        f.format("\n");

        return s.toString();
    }

    public static void delay(int time) throws InterruptedException{
        if(!GameConstants.HEADLESS){
            Thread.sleep(time);
        }
    }

    /* auxillary function to clear the screen for formatting purposes */
    public static void clearScreen(){
        if(!GameConstants.HEADLESS){
           System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
           System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
           System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        }
    }
}