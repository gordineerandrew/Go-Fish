# Go-Fish
Probability Project

Things Needed:
    Player Class
        Human
        AI
        Hands for each player
            collection of cards (Start with 7)
        collection of books for each player
        Actions
            Select Player
            select card
            go fish


Game Driver
    shuffle deck at start
    give cards to all players (7 each)
    any books the players have auto selected
    selects player turn (based on dice roll, showing results)
    game loop
        specify current players turn
        player choose card and player to search from
        if selected correctly then book created
            otherwise go fish
            if no cards in center, nothing happens
        end turn
    delay during AI turns
    on human turn display extra information


Deck of cards
    52 card objects
    deck random
    arraylist to store card objects in the deck
    separate array to store counts of each card
    use the list.shuffle to shuffle deck 7 times
    position index to go through deck


Card object
    suit
    value of card

Book Object
    2 cards for a book
    Suit does not matter


UI
    Score - for each player
    Players
    current hand - human player
    Optional - view other player hands throughout match
    Probabilities of each card for each player (except human) when
            card is selected.
