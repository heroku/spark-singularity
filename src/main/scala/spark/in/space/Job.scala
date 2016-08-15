package spark.in.space

import org.apache.spark.{SparkConf, SparkContext}

object Job {
  def main(args: Array[String]) {
    val bucket = sys.env("SPARK_JOB_S3_BUCKET_NAME")
    val conf = new SparkConf().setAppName("spark-in-space-app")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val ngramDF = sqlContext.read.parquet(Import.parqFile)
    ngramDF.groupBy("ngram").agg("occurrences" -> "max", "year" -> "max").show()
    System.exit(0)
  }
}