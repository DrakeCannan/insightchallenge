import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class tweets_cleaned {
	public static void main(String[] args) throws Exception {
		
		//Load file with JSONs
		String file1 = "../tweet_input/tweets.txt";
		File jsonfile = new File(file1);
		
		//Create scanner
		Scanner json = new Scanner(jsonfile);
		
		//Load output file ft1
		String file2 = "../tweet_output/ft1.txt";
		File ft1 = new File(file2);
		if (!ft1.exists()) ft1.createNewFile();
		
		//Create Print Writer for ft1
		PrintWriter output = new PrintWriter(ft1);
		
		//Initialize counter for tweets containing unicode
		int no_uni = 0;
		
		//Read through all JSON entries
		int n_tweets = 0;
		while (json.hasNext()) {
			String text = json.nextLine();
			n_tweets++;
			
			//Grab tweet content from JSON
			int left = text.indexOf("\"text\":\"") + 8;
			int right = text.indexOf("\",\"source\":");
			if (left<=0 || right<=0) continue;            //This continue is meant to prevent incorrectly formatted JSONs
			String tweet = text.substring(left,right);  //from throwing errors or messing up data
		
			//Grab date content from JSON
			left = text.indexOf("\"created_at\":\"") + 14;
			right = text.indexOf("\",\"id\":");
			if (left<=0 || right<=0) continue;           //This continue is meant to prevent incorrectly formatted JSONs
			String date = text.substring(left,right);  //from throwing errors or messing up data
			
			//Search for unicode
			if (tweet.contains("\\u")) {
				no_uni++;
				
				//Delete all unicode using while loop
				left = tweet.indexOf("\\u");				
				while (left>=0) {
					String unicode = "\\" + tweet.substring(left, left+6);
					tweet = tweet.replaceAll(unicode, "");
					left = tweet.indexOf("\\u");
				}
				
			}
			
			//Fix JSON escapes
			tweet = tweet.replaceAll("\\\\\\\\", "\\\\"); //backslash
			tweet = tweet.replaceAll("\\\\\"", "\""); //double quotations
			tweet = tweet.replaceAll("\\\\'", "'"); //single quotations
			tweet = tweet.replaceAll("\\\\/", "/");   //forward slash
			tweet = tweet.replaceAll("\\\\n", " ");	  //new line
			tweet = tweet.replaceAll("\\\\t", " ");    //tab
		
			//Print out cleaned tweet and date data to ft1
			output.printf("%s (timestamp: %s)",tweet,date);
			output.println();
		}
		
		//Print out number of tweets with unicode
		output.println();
		output.printf("%d tweets contained unicode.", no_uni);
		output.println();
		output.printf("%d total tweets.", n_tweets);
		
		//Close Print Writer
		output.close();
		
		//Close scanner
		json.close();
		
		
	}
}
