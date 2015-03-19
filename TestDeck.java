public class TestDeck{
    public static void main(String[] args){
        /*create a deck*/
        Deck testDeck = new Deck();
        int[] valcounter = new int[13];

        /* get all the cards off the deck until there are no more */
        Card c;
        while((c=testDeck.draw()) != null){
            System.out.print(c + " ");
            valcounter[c.getValue().ordinal()]++;
        }
        System.out.println();

        /* count all of the numbers of cards */
        for(int i = 1; i <= 13; i++){
            String val = Card.valueToString(Card.intToValue(i));
            System.out.printf("%s - %d\n", val, valcounter[i-1]);
        }
    }
}
