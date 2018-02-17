import scalaz._
import Scalaz._
import java.io._

object C {
  val result = withResource[InputStream, String](
    new FileInputStream("hoge.txt"),
    { in: InputStream =>
      val array = new Array[Byte](in.available)
      in.read(array)
      new String(array, "UTF-8")
    })
}
