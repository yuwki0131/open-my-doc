package vt

import scalaz._
import Scalaz._

// :load src/main/scala/example/ValidationTest.scala
// V.validate(V.Member("パブロ・ピカソ", "110", "1000"))

object V {
  case class Member(name: String, tel: String, age: String)

  def validateName(name: String) = {
    if (name.length < 84) {
      name.successNel[String]
    } else {
      "ピカソよりも名前が長い".failureNel[String]
    }
  }

  def validateTel(tel: String) = {
    if (tel != "110") {
      tel.successNel[String]
    } else {
      "110番を登録しないで下さい".failureNel[String]
    }
  }

  def validateAge(age: String) = {
    if (age.toInt < 117) {
      age.successNel[String]
    } else {
      "存命中の世界の長寿者十傑を超える".failureNel[String]
    }
  }

  def validate(re: Member) = {
    val result = (
      validateName(re.name) |@|
        validateTel(re.tel) |@|
        validateAge(re.age))(Member)
    result
  }
  // ExampleZ.validate(ExampleZ.Member("パブロ・ピカソ", "110", "1000"))

  def toInts(maybeInts: List[String]): ValidationNel[Throwable, List[Int]] = {
    val validationList = maybeInts map { s =>
      Validation.fromTryCatchNonFatal(s.toInt :: Nil).toValidationNel
    }
    validationList reduce (_ +++ _)
  }
}
