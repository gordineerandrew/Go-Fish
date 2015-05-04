/*
File:         GameConstants.java
Created:      2015/03/21
Last Changed: 2015/04/30
Author:      Scott Munro <scottnmunro@gmail.com>
             Andrew Gordineer <gordineerandrew@gmail.com>

Describes all of the simple global constants used through the game
*/

public class GameConstants{
    public static final int MAX_OPPONENTS = 3;			// maximum opponents that a player can play with
    public static final int STARTING_HAND = 7;			// how many cards start in each players hand
    public static final int TIME_DELAY = 1500;			// how long the average time delay is between turns
    public static final boolean DEBUG = false;			// used for debugging the game
    public static String PLAYER_NAME = "Player";		// Default name of the player
    public static int LONGEST_NAME_WIDTH = 0;			// used for formatting
    public static final int GUARANTEED_CARD = 13;		// constant used to determine if a player is 
    													// 		guaranteed to have a card in their hand
    public static boolean HEADLESS = false;				// is the game headless or not
    public static boolean LOG = false;					// should the output of the game be logged?
    public static String LOGFILE = "log.txt";			// name of the log file
}
