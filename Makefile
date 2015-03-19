JC = javac
JR = java
RFLAGS = -ea

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

AIPlayer.class: AIPlayer.java Player.class
	$(JC) AIPlayer.java

GoFish.class: GoFish.java Card.class Deck.class HumanPlayer.class AIPlayer.class
	$(JC) GoFish.java

GoFish: GoFish.class
	$(JR) $(RFLAGS) GoFish

clean:
	rm *.class
