
import java.util.ArrayList;

public class AutoHumanPlayer extends HumanPlayer{
	public PlayerCardTuple choice;

	/* CONSTRUCTOR */
	public AutoHumanPlayer(String name, Deck deckRef){
        super(name, deckRef);
        this.choice = null;
    }

    public void automateChoice(ArrayList<ArrayList<PlayerCardTuple>> options){

    	Player playerChoice;
    	Card.Value cardChoice;
    	PlayerCardTuple tupleChoice = null;
    	for(ArrayList<PlayerCardTuple> playerProbabilities : options){
    		
    		PlayerCardTuple temp = null;

    		for(PlayerCardTuple pct: playerProbabilities){
    			if(temp == null || pct.prb > temp.prb){
    				temp = pct;
    			}
    		} 

    		if(tupleChoice == null || temp.prb > tupleChoice.prb){
    			tupleChoice = temp;
    		}

    	}

    	this.choice = tupleChoice;
    }

    public PlayerCardTuple getChoice(){
    	return this.choice;
    }
}