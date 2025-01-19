import re
from pyspark.sql import SparkSession


def extract_hashtags(tweet):
    """Extract and return hashtags from a tweet.

    Args:
        tweet (str): A tweet containing zero or more hashtags.

    Returns:
        list of str: A list of extracted hashtags.
    """
    return re.findall(r'#(\w+)', tweet)


def get_top_hashtags(tweets_rdd, top_n=20):
    """Get the top N hashtags from the RDD of tweets.

    Args:
        tweets_rdd (RDD): An RDD of tweets.
        top_n (int): The number of top hashtags to return.

    Returns:
        list of (str, int): A list of tuples containing the top hashtags and their counts.
    """
    hashtags = tweets_rdd.flatMap(extract_hashtags).map(lambda hashtag: hashtag.lower())
    hashtag_counts = hashtags.map(lambda hashtag: (hashtag, 1)).reduceByKey(lambda a, b: a + b)

    return hashtag_counts.takeOrdered(top_n, key=lambda x: -x[1])


def main():
    # Initialize Spark session
    spark = SparkSession.builder \
        .appName("Top 20 Hashtags") \
        .getOrCreate()

    # Read the file containing tweets and create an RDD
    #tweets_rdd = spark.read.text('/Users/hinjam/Downloads/smallTwitter.json').rdd.map(lambda row: row.value)
    tweets_rdd = spark.read.text('s3://p2-inputdata/smallTwitter.json').rdd.map(lambda row: row.value)



    # Get the top 20 hashtags and print them
    top_hashtags = get_top_hashtags(tweets_rdd)
    print("Top 20 Hashtags:")
    for hashtag, count in top_hashtags:
        print(f"#{hashtag}: {count}")

    output_loc = "\n".join([f"#{hashtag} {count}" for hashtag, count in top_hashtags])
    #print(output_loc)

    # Saving the result to an S3 bucket
    result_path = "s3://cloudcomputingvt/Haritha/Top_20_HashTag_Output"
    spark.sparkContext.parallelize([output_loc]).saveAsTextFile(result_path)



    # Stop the Spark session
    spark.stop()


if __name__ == "__main__":
    main()


