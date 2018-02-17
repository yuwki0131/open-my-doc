package console

import scalaz._
import Scalaz._
import scala.concurrent.Future
import scalaz.std.effect.autoCloseable
import effect._
import effect.IO
import effect.IO._
import java.io._
/*
import slick.codegen.SourceCodeGenerator
import slick.jdbc.meta.MTable
import slick.jdbc.JdbcProfile
import slick.driver.MySQLDriver.api._
import slick.jdbc.MySQLProfile.api._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile._
import java.net.URI
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration._
import slick.model.Model
import javax.swing.table.TableModel
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slick.model.Table
import models.db._
import models.db.UserManager._
*/
object ExampleZ extends App {
  /*
  /** ローンパターン */
  def using[A, C <: { def close() }](closable: C)(f: C => A): A = {
    try {
      f(closable)
    } finally {
      try {
        closable.close()
      } catch {
        case _ => None
      }
    }
  }
  val nodata = "<no data>"
  /** LeftJoin用表示メソッド */
  def printWithPatternsRight(result:(Option[UsersRow], DivisionsRow)) = result match {
    case (Some(user), division) => {
        println(user.userId + ", "
            + user.name.getOrElse(nodata)
            + ", "
            + division.name)
    }
    case (None, division) => {
        println(s"${nodata}, ${nodata}, " + division.name)
    }
  }
  /** rightJoin: 一覧表示(rightJoinのテスト) */
  def showUsersRight(db: Database) {
    val action = (TableQuery[Users]
      joinRight (TableQuery[Divisions])
      on ((user, division) => user.divisionId === division.divisionId)
      result)
    println("user id / names / division id")
    val result = db.stream(action).foreach(printWithPatternsRight)
    Await.result(result, Inf)
  }
  /** LeftJoin用表示メソッド */
  def printWithPatternsLeft(result:(UsersRow, Option[DivisionsRow])) = result match {
    case (user, Some(division)) => {
        println(user.userId + ", "
            + user.name.getOrElse(nodata)
            + ", "
            + division.name)
    }
    case (user, None) => {
        println(user.userId + ", " + user.name.getOrElse(nodata) + s", ${nodata}")
    }
  }
  /** leftJoin: 一覧表示(leftJoinのテスト) */
  def showUsersLeft(db: Database) {
    val action = (TableQuery[Users]
      joinLeft (TableQuery[Divisions])
      on ((user, division) => user.divisionId === division.divisionId)
      result)
    println("user id / names / division id")
    val result = db.stream(action).foreach(printWithPatternsLeft)
    Await.result(result, Inf)
  }
  /** join: 一覧表示(joinのテスト) */
  def showUsers(db: Database) {
    val action = (TableQuery[Users]
      join (TableQuery[Divisions])
      on ((user, division) => user.divisionId === division.divisionId)
      result)
    println("user id / names / division id")
    val result = db.stream(action).foreach {
      case (user, division) => {
        println(s"${user.userId}, " + user.name.getOrElse("<no data>") + s", ${division.name}")
      }
    }
    Await.result(result, Inf)
  }
  /** sort: 一覧表示(sortのテスト) */
  def showUsersSort(db: Database) {
    val action = (TableQuery[Users]
      join (TableQuery[Divisions])
      on ((user, division) => user.divisionId === division.divisionId)
      result)
    println("user id / names / division id")
    val result = db.stream(action).foreach {
      case (user, division) => {
        println(s"${user.userId}, " + user.name.getOrElse("<no data>") + s", ${division.name}")
      }
    }
    Await.result(result, Inf)
  }
  /** read: 一覧表示 */
  def listupUsers(db: Database) {
    val action = (TableQuery[Users]
      map { col => (col.userId, col.name, col.divisionId) }
      result)
    println("user id / names / division id")
    val result = db.stream(action).foreach {
      case (userId, Some(name), Some(divisionId)) => {
        println(s"${userId}, ${name}, ${divisionId}")
      }
      case (userId, _, _) => {
        println(s"${userId}のデータ取得に失敗")
      }
    }
    Await.result(result, Inf)
  }
  /** create: ユーザを追加 */
  def insertUser(db: Database)(userId: String, name: String, divisionId: Int) {
    val users: TableQuery[Users] = TableQuery[Users]
    val action = users += UsersRow(userId, Some(name), Some(divisionId))
    val result = db.run(action).map { records => s"${records}件登録" }
    println(Await.result(result, Inf))
  }
  /** update: ユーザの情報を更新 */
  def updateUser(db: Database)(userId: String, name: String, divisionId: Int) {
    val action = (Users
      filter { col => col.userId === userId }
      map { col => (col.name, col.divisionId) }
      update ((Some(name), Some(divisionId))))
    val result = db.run(action).map { records => s"${records}件更新" }
    println(Await.result(result, Inf))
  }
  /** delte: ユーザ情報を削除 */
  def deleteUser(db: Database)(userId: String) {
    val action = (Users
      filter { col => col.userId === userId } delete)
    val result = db.run(action).map { records => s"${records}件削除" }
    println(Await.result(result, Inf))
  }
 using(Database.forConfig("dbconfig")) { db =>
    // 登録 → 更新 → 削除
    println("-" * 100)
    showUsers(db)
    insertUser(db)("user-id1", "テストユーザ", 1)
    listupUsers(db)
    updateUser(db)("user-id1", "テストユーザ更新後", 2)
    listupUsers(db)
    deleteUser(db)("user-id1")
    listupUsers(db)
    // Join (left/right)
    println("-- users join left divions  " + "-" * 80)
    showUsersLeft(db)
    println("-- users join right divions " + "-" * 80)
    showUsersRight(db)
    println("-" * 100)
  } */

  println("try scalaz below!! --------------------------------")

  println("1-1: Option hogehoge transformer")

  println((true).option("hogehoge").toString)

  println("1-2: Boolean to Value or zero")

  // println((true).valueorzero)

  val testList = Range(0, 50).toList

  println("2-1: List interpose")

  println(testList.intersperse(-1))

  /*
  println("2-1-1: CSV file")
  println(Seq(Seq(1,2,3),Seq(4,5,6),Seq(7,8,9))
    .map(x => x.map(_.toString).intersperse(",").toString).intersperse(","))
	*/

  println("2-2: List tailOption")

  println(testList.tailOption)

  println(List().tailOption)

  println("3-1: Signs")

  // println(List(10, 20, 30) foldMap Π)

  // case class ReMember(name: String, tel: String, mail: String)
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

  // (validateName("山手線") |@| validateTel("0312345678"))(ReMember)
  // (validateName("山手線") |@| validateTel("0312345678") |@| validateMail("0@co.jp"))(Member)
  /* implicit val inResource: Resource[FileInputStream] = resource(fin => fin.close())
  withResource[InputStream, String](
    new FileInputStream("C:\\usr\\hoge.txt"),
    { in: InputStream =>
      val array = new Array[Byte](in.available)
      in.read(array)
      new String(array, "UTF-8")
    }) */

  // https://qiita.com/ryoppy/items/65a3ac21a6b1ac35d637
  // implicit val bfrResource = Resource.resourceFromCloseable[BufferedReader]

  // def readHoge = {
  //   IO(new BufferedReader(new FileReader("/home/yuwki0131/temp/hoge.txt"))).using { buf =>
  //     IO.putStr("message")
  //     IO.putStr(buf.readLine())
  //   }
  // }
  // console.ExampleZ.readHoge.unsafePerformIO
}
