import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;


public class average_degree {
	public static void main(String[] args) throws Exception {
		
		//Load file with JSONs
		String file1 = "tweet_input/tweets.txt";
		File jsonfile = new File(file1);
		
		//Load output file ft2
		String file3 = "tweet_output/ft2.txt";
		File ft2 = new File(file3);
		if (!ft2.exists()) ft2.createNewFile();
		
		//Create JSON scanner
		Scanner json = new Scanner(jsonfile);
		
		//Create Print Writer for ft2
		PrintWriter output = new PrintWriter(ft2);
		
		//Create date formatter
		DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss +SSSS yyyy");
		
		//Declare variables to be used
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int left = 0;
		int right = 0;
		float average = 0;
		float edges = 0;
		float n_hashtags = 0;
		String text = "";
		String hashtag = "";
		String date = "";
		String unicode = "";
		String temp = "";
		ArrayList<String> hashlist = new ArrayList<String>();
		ArrayList<String> allhashtags = new ArrayList<String>();
		ArrayList<String> edgelist = new ArrayList<String>();		
		Date tempdate = new Date();
		
		//Read through all JSON entries
		while (json.hasNext()) {
			text = json.nextLine();
			
			//Grab hashtag content from JSON
			left = text.indexOf("\"hashtags\":[") + 12;
			right = text.indexOf("],\"urls\":");
			if (left<0 || right<0) continue;            //This continue is meant to prevent incorrectly formatted JSONs
			hashtag = text.substring(left,right);  //from throwing errors or messing up data
		
			//Grab date content from JSON
			left = text.indexOf("\"created_at\":\"") + 14;
			right = text.indexOf("\",\"id\":");
			if (left<0 || right<0) continue;           //This continue is meant to prevent incorrectly formatted JSONs
			date = text.substring(left,right);  //from throwing errors or messing up data
			
			//Search for unicode
			if (hashtag.contains("\\u")) {				
				//Delete all unicode using while loop
				left = hashtag.indexOf("\\u");				
				while (left>=0) {
					unicode = "\\" + hashtag.substring(left, left+6);
					hashtag = hashtag.replaceAll(unicode, "");
					left = hashtag.indexOf("\\u");
				}				
			} 
			
			//Fix JSON escapes
			hashtag = hashtag.replaceAll("\\\\\\\\", "\\\\"); //backslash
			hashtag = hashtag.replaceAll("\\\\\"", "\""); //double quotations
			hashtag = hashtag.replaceAll("\\\\'", "'"); //single quotations
			hashtag = hashtag.replaceAll("\\\\/", "/");   //forward slash
			hashtag = hashtag.replaceAll("\\\\n", " ");	  //new line
			hashtag = hashtag.replaceAll("\\\\t", " ");    //tab
			
			//Format the hashtags and add them to ongoing lists if they have more than two hashtags
			temp = "";
			if (hashtag.contains("\"text\":")) {
				//Split the hashtags
				String[] hashtags = hashtag.split("\\{\"text\":\"");
				//output.println(hashtag);
				
				
				//for loop to read hashtags to list skipping the first empty entry after split 0
				if (hashtags.length>2) { //check if more than 2 hashtags
					for (i=1; i<hashtags.length; i++) {
						right = hashtags[i].indexOf("\",\"indices\":");
						if (right==0) continue;  //Skip any empty hashtags
						hashtags[i] = "#" + hashtags[i].substring(0,right).toLowerCase() + ",";
					
						//Add to hashlist
						temp = temp + hashtags[i] + " ";
						if (i==hashtags.length-1) {
							//Add to ongoing list of dated hashtags
							temp = temp + "(timestamp: " + date + ")";
							hashlist.add(temp);
							//output.println("+" + temp);
						}
					} 
					for (i=1; i<hashtags.length; i++) {
						if ((hashtags[i].isEmpty())||(hashtags[i].contains("\",\"indices\":"))) continue;   //Skip any empty hashtags
					
						//Check if in allhashtags and if not add
						boolean in_list = false;
						for (j=0; j<allhashtags.size(); j++) {
								//set to true if hashtag is in set
								if (hashtags[i].equals(allhashtags.get(j))) in_list = true;
						}
						//add to allhashtags
						if (!in_list) {
							allhashtags.add(hashtags[i]);
						}		
					
						//Check if in hashtag edges and if not add	
						//i and j only have to shake hands once
						for (j=hashtags.length-1; j>i; j--) {
						
							//test if both hashtags are in edgelist
							in_list = false;	
							for (k=0; k<edgelist.size(); k++)
								if ((edgelist.get(k).contains(hashtags[i])) && (edgelist.get(k).contains(hashtags[j]))) in_list=true;
															
							//if not in edgelist add it
							if (!in_list) {
								edgelist.add(hashtags[i] + "<->" + hashtags[j]);
								//output.println(hashtags[i] + "<->" + hashtags[j]);
							}
						}
					}
						
				}
			}
			
			/*Test hashtags for removal */
			
			//if no hashtags have been added, continue
			if (hashlist.isEmpty()) continue;
			
			//Find date of first hashtag
			left = hashlist.get(0).indexOf("(timestamp: ") + 12;
			right = hashlist.get(0).length()-1;
			tempdate = formatter.parse(hashlist.get(0).substring(left, right));
			
			//if the first entry in hashlist is more than 60 seconds old
			//execute this deletion and retest loop
			if (tempdate.getTime() < (formatter.parse(date).getTime() - 60*1000)) 
					for (i = 0; i<hashlist.size(); i++) {
				
				//if hashtag is within 60 seconds of tweet time then remove it
				if (((tempdate.getTime() < (formatter.parse(date).getTime() - 60*1000)) || (tempdate.getTime() > formatter.parse(date).getTime()))) {

					//Prepare hashlist and skip any null entries
					right = hashlist.get(i).indexOf("(timestamp: ");
					temp = hashlist.get(i).substring(0, right);
					if (temp.isEmpty()) continue;    //skip entries with no hashtags
					
					String [] hashtags = temp.split(", ");
					
					//remove the entry from list
					//output.println("-" + hashlist.get(i));
					hashlist.remove(i);
					
					//test hashtags against allhashtags list and remove if there
					for (j=0; j<hashtags.length; j++) {
						hashtags[j] = hashtags[j] + ",";
						for (k=0; k<allhashtags.size(); k++) {
							//set to true if hashtag is in set
							if (hashtags[j].equals(allhashtags.get(k))) {
								allhashtags.remove(k);
							}
						}
					}
						
					//test hashtags against edges and remove if there
					for (j=0; j<hashtags.length; j++) {
						for (k=hashtags.length-1; k>j; k--) {								
							//test if both are in edgelist
							for (l=0; l<edgelist.size(); l++)
								if ((edgelist.get(l).contains(hashtags[j])) && (edgelist.get(l).contains(hashtags[k]))) 
									edgelist.remove(l);									
						}
					
					}
					
					//continue to next entry
					continue;
				}
								
				/*//if hashtag set is more than 2 minutes old delete it from list and continue
				if (tempdate.getTime() < (formatter.parse(date).getTime() - 10*60*1000)) {
					hashlist.remove(i);
					continue; 
				}*/
				
				right = hashlist.get(i).indexOf("(timestamp: ");
				temp = hashlist.get(i).substring(0, right);
				if (temp.isEmpty()) continue;    //skip entries with no hashtags
				String [] hashtags = temp.split(", ");	
				
				//test hashtags against list and add if not there
				boolean in_list = false;
				for (j=0; j<hashtags.length; j++) {
					if (hashtags[j].isEmpty()) continue;
					hashtags[j] = hashtags[j].trim() + ",";
					for (k=0; k<allhashtags.size(); k++) {
						//set to true if hashtag is in set
						if (hashtags[j].equals(allhashtags.get(k))) in_list = true;
					}
					
					//add to allhashtags
					if (!in_list) {
						allhashtags.add(hashtags[j]);
						//output.println(hashtags[j]);
					}
				}
					
				//look for hashtag edges
				//test if more than one exist
				for (j=0; j<hashtags.length; j++) {
					for (k=hashtags.length-1; k>j; k--) {
						//test if both are in edgelist
						in_list = false;
						for (l=0; l<edgelist.size(); l++)
							if ((edgelist.get(l).contains(hashtags[j])) && (edgelist.get(l).contains(hashtags[k]))) in_list=true;
															
						//if not in edgelist add it
						if (!in_list) {
							edgelist.add(hashtags[j] + "<->" + hashtags[k]);
							//output.println(hashtags[j] + "<->" + hashtags[k]);
						}
					}
				
				} 
			}
			
			//Calculate the average degrees
			//notice that the sum of number of edges time 2 is the same as the sum of all individual hashtags connections
			//each edge adds a one to two separate numbers
			if (allhashtags.size()>0) {
				edges = edgelist.size();
				n_hashtags = allhashtags.size();
				average = 2*(edges)/(n_hashtags);
				output.printf("%.2f", average);
				output.println();
			}
			else output.println("No hashtags so far.");
		}
		
		
		//Close Print Writer
		output.close();
		
		//Close scanner
		json.close();
		
		
	}
}
