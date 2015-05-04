
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class HeadlessLogger{

	public static PrintWriter out;
	public static int wins;

	public static void main(String[] args) throws IOException, InterruptedException{
		
		/*
		args0 = N trials to run
		args1 = file to log to
		args2 = smart (nonzero) or random auto (0) 
		*/

		if(args.length < 2)
		{
			System.out.println("ERROR: not enough arguments\n");
			return;
		}

		String logfile = GameConstants.LOGFILE;
		if(args.length > 1 && args[1].contains(".txt"))
		{
			logfile = args[1];
		}

		out = new PrintWriter(new BufferedWriter(new FileWriter(logfile, false)));
		wins = 0;

		GameConstants.HEADLESS = true;
		GameConstants.LOG = true;
		GameConstants.AUTO = true;
		if(Integer.parseInt(args[2]) != 0)
		{
			GameConstants.SMART = true;
		}

		int trials = Integer.parseInt(args[0]);
		for(int i = 0; i < trials; i++)
		{
			if(args.length < 3) System.out.printf("Trial %4d\tRunning...\n", i+1);

			GoFish.main(args);
		}

		out.println(wins);

		out.close();

		if(args.length == 3) 
			System.out.printf("TRIALS DONE WITH %s CHARACTER\n", GameConstants.SMART ? "SMART":"RANDOM");

		else 
			StatsDriver.statsPrinter.printf("%.2f, ", (100.0*wins)/trials);
	}
}