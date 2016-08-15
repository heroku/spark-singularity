package spark.in.space

import java.io.File
import java.net.URI

import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.event.{ProgressEvent, ProgressListener}
import com.amazonaws.services.s3.transfer.TransferManager
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}


/**
  * Created by sclasen on 8/10/16.
  */
object Import {
  val log = org.slf4j.LoggerFactory.getLogger("Import")
  val bucket = sys.env("SPARK_JOB_S3_BUCKET_NAME")
  val n1gram = "ngrams/books/20090715/eng-us-all/1gram/data"
  val dlFile = "/app/googleNGram1Gram.lzo.sequenceLongString"
  val publicBucket = "datasets.elasticmapreduce"
  val parqFile = s"s3n://${bucket}${dlFile}.parquet"

  def main(args: Array[String]): Unit = {
    if(! new File(dlFile).exists()) {
      println("downloading")
      val tx = new TransferManager(new AnonymousAWSCredentials())
      val download = tx.download(publicBucket, n1gram, new File(dlFile))
      download.addProgressListener(new ProgressListener {
        override def progressChanged(progressEvent: ProgressEvent): Unit = {
          println(s"type: ${progressEvent.getEventType.name()} bytes: ${progressEvent.getBytes} transferred:${progressEvent.getBytesTransferred}")
        }
      })
      download.waitForCompletion()
    }
    val conf = new SparkConf().setAppName("spark-in-space-app")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    import sqlContext.implicits._
    val theRDD = sc.sequenceFile[Long,String](s"file://$dlFile", 80)
    val theParsed = theRDD.map{ case (_, row) => rowToNGram(row) }
    val df = theParsed.toDF
    FileSystem.get(new URI(s"s3n://$bucket"), sc.hadoopConfiguration).delete(new Path(dlFile), true)
    df.write.parquet(parqFile)

  }

  def rowToNGram(in: String) : NGram = {
    val split = in.split("\t")
    NGram(split(0), split(1), split(2).toInt, split(3).toInt, split(4).toInt)
  }
}



case class NGram(ngram:String, year:String, occurrences:Int, pages:Int, books:Int)



