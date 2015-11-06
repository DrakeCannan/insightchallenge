# Insight Data Engineering Challenge
I programmed my solution to this challenge on Java, testing on version 1.8.0_65 and using the libraries in the Java Development Toolkit 8 (8u65) as well as the default libraries. I tested on Cygwin so hopefully everything compiles properly on Linux.

## Part 1: Cleaning tweet text
My approach to this was simply to read the tweets.txt document line by line, grab the text and created_at datafields from the JSON, and then format the text-field from escape sequences and any unparsed unix characters, counting them in the process. The result is then printed in the format 
```
<text> (created at: <time>)
```

At the end of the file, the number of unparsed unix characters is printed as well as the total number of tweets for comparison.

## Part 2: Counting hashtag coupling
The first part of this is fairly simple, and it's similar to part 1 described above.

After the hashtags are cleaned and extracted, I saved them into three updating lists:

- List of hashtags from tweets with multiple hashtags, with format: 
```
#<hashtag1>, #<hashtag2>, #<hashtag3>, (Created at: <time>)
```
- List of all hashtags, with format: 
```
#<hashtag>,
```
- List of all "edges", with format:
```
#<hashtag1>, <-> #<hashtag2>,
```
  
When a new tweet with more than one hashtag is read, it is added into the dated list of all hashtags. The code searches through the list of individual hashes to see if all hashes are listed there and adds them if not. The code also searches through the list of hashtag "edges" and sees if all groupings are listed there, again adding them if not.

After that is the deleting and updating stage, implemented with the assumption that tweets would be in order chronologically. The first entry in the dated list of hashtags is tested versus the current tweet to see if it occured within 60 seconds. If not, this tweet and any further tweets over 60 seconds old compared to the recent tweet must be deleted along with their entries in the list of all hashtags and the list of edges. 

When this deletion happens, the updated list of dated hashtags is compared to the list of individual hashes and the list of edges to see if there is still a more recent list of hashtags with those entries so they can be readded.

I opted for this methodology over the more processor-intensive method of simply running through the dated list of tweet hashtags and finding edges and unique hashtags for every new tweet. Full scans through the data lists are a big source of increased processing time, so I tried to minimize them as much as possible. 

Finally, the list of all edges is counted (by computing the size of the list of edges) and multiplied by two; this represents the total amount of connections, each unique edge reaches out and increments the number of connections for two hashtags by one each. This sum of connections is then divided by the number of hashtags (computed by the size of the list of all hashtags). This figure is calculated and printed out for each new tweet.
