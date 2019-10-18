package com.java.spark;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class SparkWordCount {

	public static void main(String[] args) throws Exception{
		//System.out.println(System.getProperty("hadoop.home.dir"));

        //String inputPath = args[0];
		String inputPath = "/Users/mac/Desktop/sample.txt.rtf";
		//String outputPath = args[1];
		String outputPath = "/Users/mac/Desktop/output.txt.rtf";
		FileUtils.deleteQuietly(new File(outputPath));

        /**
         * The first thing a Spark program must do is to create a JavaSparkContext object,
         * which tells Spark how to access a cluster.
         *
         *  1- The AppName will be shown in the cluster UI: Mesos, Spark, ot YARN
         *  2- The master is the name of the machine, we use local if the user run the program in a local machine
         *  3- A property of the number of cores to be use by the software
         */


        //SparkConf conf = new SparkConf().setAppName("word-counter").setMaster("local").set("spark.cores.max", "10");
		SparkConf conf = new SparkConf().setAppName("word-counter").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        /**
         * Text file RDDs can be created using SparkContext’s textFile method textFile function. This method takes an URI for the file
         * either a local path on the machine, or a hdfs://, s3n://, etc URI) and reads it as a collection of lines. Every line in the file is
         * an element in the JavaRDD list.
         *
         * Important **Note**: This line defines a base RDD from an external file. This dataset is not loaded in memory or otherwise acted on:
         * lines is merely a pointer to the file.
         *
         * Here is an example invocation:
         */

		JavaRDD<String> rdd = sc.textFile(inputPath);

        /**
         * The function collect, will get all the elements in the RDD into memory for us to work with them.
         * For this reason it has to be used with care, specially when working with large RDDs. In the present
         * example we will filter all the words that contain @ to check all the references to other users in twitter.
         *
         * The function collect return a List
         */

        /*JavaPairRDD<String, Integer> counts = rdd.flatMap(x -> Arrays.asList(x.split(" ")).iterator())
                .mapToPair(x -> new Tuple2<>(x, 1)).countByKey();
                .reduceByKey((x, y) -> x + y); */

        JavaRDD<String> words =
        	    rdd.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
        	JavaPairRDD<String, Integer> count =
        	    words.mapToPair(w -> new Tuple2<String, Integer>(w, 1))
        	         .reduceByKey((x, y) -> x + y);
        
        
        /* List<Tuple2<String, Integer>> finalCounts = counts.filter((x) -> x._1().contains("@"))
                .collect();

        for(Tuple2<String, Integer> count: finalCounts)
                System.out.println(count._1() + " " + count._2());  */

        /**
         * This function allow to compute the number of occurrences for a particular word, the first instruction flatMap allows to create the key of the map by splitting
         * each line of the JavaRDD. Map to pair do not do anything because it only define that a map will be done after the reduce function reduceByKey.
         *
         */

		 /* counts = rdd.flatMap(x -> Arrays.asList(x.split(" ")).iterator())
                 .mapToPair(x -> new Tuple2<>(x, 1))
                 .reduceByKey((x, y) -> x + y); */

        /**
         * This function allows you to filter the JavaPairRDD for all the elements that the number
         * of occurrences are bigger than 20.
         */

        /*counts = counts.filter((x) -> x._2() > 2);

        long time = System.currentTimeMillis();
        long countEntries = counts.count();
        System.out.println(countEntries + ": " + String.valueOf(System.currentTimeMillis() - time)); */

        /**
         * The RDDs can be save to a text file by using the saveAsTextFile function which export the RDD information to a text representation,
         * either a local path on the machine, or a hdfs://, s3n://, etc URI)
         */
        	System.out.println("Output:"+count.collect());
		count.saveAsTextFile(outputPath);
		sc.close();

	}


	}


