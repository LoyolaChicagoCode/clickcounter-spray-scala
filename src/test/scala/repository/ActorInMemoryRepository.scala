package edu.luc.etl.cs313.scala.clickcounter.service
package repository

import akka.actor.{Props, ActorSystem, Actor}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.collection.mutable.Map
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import common.Repository
import model.Counter

/** Stackable mixin trait that provides a fake in-memory repository. */
trait ActorInMemoryRepositoryProvider {
  lazy val repository = new ActorInMemoryRepository
}

/** Fake thread-safe in-memory repository for unit testing. */
class ActorInMemoryRepository extends Repository {

  val system = ActorSystem()

  val actorRef = system.actorOf(Props[RepositoryActor])

  implicit val timeout = Timeout(5 seconds)

  override def keys: Future[Set[String]] = ask(actorRef, KEYS).mapTo[Set[String]]
  override def set(id: String, counter: Counter) = ask(actorRef, SET(id, counter)).mapTo[Boolean]
  override def get(id: String) = ask(actorRef, GET(id)).mapTo[Option[Counter]]
  override def del(id: String) = ask(actorRef, DEL(id)).mapTo[Long]
  override def update(id: String, f: Counter => Int) = ask(actorRef, UPDATE(id, f)).mapTo[Option[Boolean]]

  override def subscribe(id: String)(handler: Option[Counter] => Unit) = ???
}

case object KEYS
case class SET(id: String, counter: Counter)
case class GET(id: String)
case class DEL(id: String)
case class UPDATE(id: String, f: Counter => Int)

class RepositoryActor extends Actor {

  private val data = Map.empty[String, Counter]

  def receive = {
    case KEYS => sender() ! data.keys.toSet
    case SET(id, counter) => data.put(id, counter); sender() ! true
    case GET(id) => sender() ! data.get(id)
    case DEL(id) => data.remove(id); sender() ! 1L
    case UPDATE(id, f) => sender() !
      (data.get(id) match {
        case Some(c@Counter(min, value, max)) =>
          // found item, attempt update
          Try(Counter(min, f(c), max)) match {
            case Success(newCounter) =>
              data.put(id, newCounter)
              Some(true)
            case Failure(_) =>
              // precondition for update not met
              Some(false)
          }
        case None => None // item not found
      })
  }
}
