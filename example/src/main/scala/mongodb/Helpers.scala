package mongodb

import org.mongodb.scala._

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Helpers {

  implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document] {
    override val converter: Document => String = doc => doc.toJson()
  }

  implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
    override val converter: C => String = doc => doc.toString
  }

  trait ImplicitObservable[C] {
    val observable: Observable[C]
    val converter: C => String

    def printHeadResult(initial: String = ""): Unit = println(s"$initial${converter(headResult)}")

    def headResult: C = Await.result(observable.head(), Duration(10, TimeUnit.SECONDS))

    def printResults(initial: String = ""): Unit = {
      if (initial.nonEmpty)
        print(initial)
      results.foreach(res => println(converter(res)))
    }

    def results: Seq[C] = Await.result(observable.toFuture(), Duration(10, TimeUnit.SECONDS))
  }

}
