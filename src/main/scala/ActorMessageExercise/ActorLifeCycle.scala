package ActorMessageExercise

import ActorMessageExercise.ActorLifeCycle.MyActor.{Decrement, Increment, Print}
import akka.actor._

import scala.language.postfixOps

object ActorLifeCycle extends App {
  object MyActor {
    case object Increment

    case object Decrement

    case object Print
  }

  // Actor class
  class MyActor extends Actor with ActorLogging {
    var count = 0

    override def preStart(): Unit = {
      log.info("Actor is starting")
    }

    override def postStop(): Unit = {
      log.info("Actor has stopped")
    }

    override def receive: Receive = {
      case Increment => count += 1
        log.info(s"Received Increment message. Actor is running. count = $count")
      case Decrement => count -= 1
        log.info(s"Received Decrement message. Actor is running. count = $count")
      case Print => log.info(s"Count value is $count")
        context.stop(self)
    }
  }

  // Create an ActorSystem
  val system = ActorSystem("ActorLifecycleSystem")

  // Create an instance of MyActor
  val actor = system.actorOf(Props[MyActor], "myActor")

  // Send Start message to the actor
  (1 to 5).foreach(_ => actor ! Increment)
  (1 to 3).foreach(_ => actor ! Decrement)
  actor ! Print

  // Terminate the ActorSystem
  system.terminate()
}
