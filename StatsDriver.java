
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class StatsDriver{
	
	public static PrintWriter statsPrinter;

	public static void main(String[] args)throws IOException, InterruptedException{
		/*s
		arg0 = N number of trials for each GoFish game
		arg1 = logfile for each trial
		arg2 = smart or random auto
		arg2 = M number of repititions of the N trials
		arg3 = logfile for all trials
		*/
		if(args.length < 5)
		{
			System.err.println("Too Few Arguments\n");
			return;
		}

		String logfile = args[4];
		statsPrinter = new PrintWriter(new BufferedWriter(new FileWriter(logfile, false)));

		int repititions = Integer.parseInt(args[3]);
		

		for(int i = 0; i < repititions; i++)
		{
			System.out.printf("Repitition %d\t\tRunning...\n", i+1);
			HeadlessLogger.main(args);
		}

		statsPrinter.close();
	}
}