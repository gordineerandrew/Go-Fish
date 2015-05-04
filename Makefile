JC = javac
JR = java
RFLAGS = -ea

PlayerCardTuple.class: PlayerCardTuple.java
	$(JC) PlayerCardTuple.java

GameConstants.class: GameConstants.java
	$(JC) GameConstants.java

Card.class: Card.java
	$(JC) Card.java

Deck.class: Deck.java
	$(JC) Deck.java

TestDeck.class: TestDeck.java Card.class Deck.class
	$(JC) TestDeck.java

TestDeck: TestDeck.class
	$(JR) TestDeck

Player.class: Player.java
	$(JC) Player.java

HumanPlayer.class: HumanPlayer.java Player.class
	$(JC) HumanPlayer.java

AutoHumanPlayer.class: AutoHumanPlayer.java HumanPlayer.class Player.class
	$(JC) AutoHumanPlayer.java

AIPlayer.class: AIPlayer.java Player.class
	$(JC) AIPlayer.java

GoFish.class: GameConstants.class Card.class Deck.class HumanPlayer.class AIPlayer.class PlayerCardTuple.class GoFish.java
	$(JC) GoFish.java

StatsDriver.class: GameConstants.class Card.class Deck.class HumanPlayer.class AIPlayer.class GoFish.class PlayerCardTuple.class HeadlessLogger.class StatsDriver.java
	$(JC) StatsDriver.java

HeadlessLogger.class: GameConstants.class Card.class Deck.class HumanPlayer.class AIPlayer.class GoFish.class PlayerCardTuple.class HeadlessLogger.java
	$(JC) HeadlessLogger.java

GoFish: GoFish.class
	$(JR) $(RFLAGS) GoFish

AutoGoFish: HeadlessLogger.class

StatsDriver: StatsDriver.class

clean:
	rm *.class
