package com.resurf

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
}
