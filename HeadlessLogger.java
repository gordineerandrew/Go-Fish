
import java.io.IOException;

public class HeadlessLogger{
	public static void main(String[] args) throws IOException, InterruptedException{
		if(args.length < 1)
		{
			System.out.println("ERROR: not enough arguments\n");
			return;
		}

		if(args.length > 1 && args[1].contains(".txt"))
		{
			GameConstants.LOGFILE = args[1];
		}

		// GameConstants.HEADLESS = true;
		// GameConstants.LOG = true;
		GameConstants.AUTO = true;

		int trials = Integer.parseInt(args[0]);
		for(int i = 0; i < trials; i++)
		{
			System.out.printf("Trial %4d\tRunning...\n", i+1);
			GoFish.main(args);
		}

		System.out.printf("TRIALS DONE\n");
	}
}