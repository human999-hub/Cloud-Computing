# Top 20 Hashtags Counter using PySpark and Amazon EMR
# Overview
This project processes large Twitter JSON datasets to extract and count hashtags using PySpark, a distributed data processing framework. The pipeline is deployed on Amazon EMR (Elastic MapReduce) to handle large-scale data efficiently. The final output includes the top 20 hashtags along with their frequency counts, which are stored in AWS S3 for further analysis.

# Steps Followed
Data Input:
Input data is a JSON file containing tweets stored in an AWS S3 bucket.
Spark Initialization:
A SparkSession is initialized to process the data in a distributed fashion.
Hashtag Extraction:
The hashtag_mapper function scans through the JSON structure to identify hashtags using regular expressions.
Data Processing:
The JSON data is loaded into an RDD using PySpark.
The hashtags are extracted and mapped as (hashtag, 1) pairs.
Hashtag counts are aggregated using reduceByKey.
Top 20 Hashtags:
The top 20 hashtags are identified using takeOrdered and sorted in descending order based on counts.
Output Storage:
The results are formatted into a text file and stored back in the specified AWS S3 bucket.
# Tools and Technologies Used
PySpark: Distributed data processing framework. Amazon EMR: Managed Hadoop/Spark cluster on AWS for big data processing. AWS S3: Cloud storage for input and output data. Python: Programming language to implement the logic. Regular Expressions: Extract hashtags from text efficiently.
