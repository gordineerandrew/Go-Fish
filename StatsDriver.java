
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.TreeMap;
import java.util.Map;

public class StatsDriver{
	
	public static PrintWriter statsPrinter; /* file writer for logging purposes*/
	static double percentage = 0.0;					/* static variable used to store result from HeadlessLogger */

	public static void main(String[] args)throws IOException, InterruptedException{
		/*s
		arg0 = N number of trials for each GoFish game
		arg1 = logfile for each trial
		arg2 = smart or random auto
		arg3 = M number of repititions of the N trials
		arg4 = logfile for all trials
		arg5 = logfile for CDF results
		*/
		if(args.length < 6)
		{
			System.err.println("Too Few Arguments\n");
			return;
		}

		TreeMap<Double, Integer> CDFMap = new TreeMap<Double, Integer>(); 

		String logfile = args[4];
		String cdfFile = args[5];

		statsPrinter = new PrintWriter(new BufferedWriter(new FileWriter(logfile, false)));
		PrintWriter cdfWriter = new PrintWriter(new BufferedWriter(new FileWriter(cdfFile, false)));

		int repititions = Integer.parseInt(args[3]);
		

		for(int i = 0; i < repititions; i++)
		{
			System.out.printf("Repitition %d\t\tRunning...\n", i+1);
			HeadlessLogger.main(args);

			if(CDFMap.containsKey(percentage)){
				int value = CDFMap.get(percentage)+1;
				CDFMap.put(percentage, value);
			}
			else{
				CDFMap.put(percentage, 1);
			}

			percentage = 0.0;
		}

		for(Map.Entry<Double, Integer> entry: CDFMap.entrySet())
		{
			cdfWriter.printf("|%-10.3f|%-5d|\n", entry.getKey(), entry.getValue());
			cdfWriter.println("----------------");
			/*entry.getKey() + "/" + entry.getValue()*/
		}

		statsPrinter.close();
		cdfWriter.close();
	}
}