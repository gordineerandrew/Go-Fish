# Go-Fish
A Project for M362K that will explore the use of conditional probability to play a better game of Go-Fish.

# Objects

* GameDriver - the class that will drive the game loop and manage global assets
* Player - an abstract base player class that will define 'global' player behavior
    * Human Player - defines player behavior specific to human players
    * AI Player - defines player behavior specific to AI players
* Deck - defines behavior and state associated with the running game's deck of cards
* Card - defines the state of a card
* Book - defines the state of a book of cards

# SPECIFICATIONS

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


## Player
### State
* each player has a hand...
    * hand is just a collection of cards
* ...a set of books...
    * a collection of pairs of cards
* ... and a score..
    * score specified by the number of books the player has
### Behavior
* Draw from the deck / Go-Fish
* request cards from other players

## Card
### State
* Suit
* Value
### Behavior
* toString - to get a human-readable form of the card object

## Book
### State
* value of the book

## Deck
### State
* begins with 52 card objects
* an array of 13 values to keep track of how many of each value is left in the deck
* keep a reference of the index within the deck
    * don't bother removing things since nothing can be added back to the deck

### Behavior
* use list.shuffle to shuffle the deck
* grab from the top of the deck when Go-Fish or any kind of draw
