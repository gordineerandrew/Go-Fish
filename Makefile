JC = javac
JR = java

Card.java:
	$(JC) Card.java

Deck.java:
	$(JC) Deck.java

TestDeck.java: Card.java Deck.java
	$(JC) TestDeck.java

TestDeck: TestDeck.java
	$(JR) TestDeck
