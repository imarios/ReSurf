package com.resurf

import com.twitter.util.{Duration, Time}

/**
 * Contains common utility methods
 */
package object common {

  def writeDataToDisk[T](data: Seq[T], fname: String) = {
    import java.io._
    val pw = new PrintWriter(new File(fname))
    data.foreach(x => pw.write(x + "\n"))
    pw.close()
  }

  def getCurrentTime = Time.now

  def averageDuration(data: Seq[Duration]): Duration =
    Duration.fromNanoseconds(data.map(_.inNanoseconds).sum / data.size)
}
