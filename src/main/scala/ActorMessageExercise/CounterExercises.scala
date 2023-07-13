package ActorMessageExercise

import ActorMessageExercise.CounterExercises.Counter.{Decrement, Increment, Print}
import ActorMessageExercise.CounterExercises.Person.LiveTheLife
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object CounterExercises extends App {

  object Counter {
    case object Increment

    case object Decrement

    case object Print
  }

  class Counter extends Actor {

    var count = 0

    override def receive: Receive = {
      case Increment => count += 1
      case Decrement => count -= 1
      case Print => println(s"Count value is $count")
    }
  }

  val system = ActorSystem("CountActor")
  val counter = system.actorOf(Props[Counter], "myCounter")

  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)
  counter ! Print

  // bank Account
  object BankAccount {
    case class Deposit(amount: Int)

    case class Withdraw(amount: Int)

    case object Statement

    case class TransactionSuccess(message: String)

    case class TransactionFailure(message: String)
  }

  class BankAccount extends Actor {

    import BankAccount._

    var funds = 0

    override def receive: Receive = {
      case Deposit(amount) =>
        if (amount < 0) sender() ! TransactionFailure("Invalid Deposit Amount")
        else {
          funds += amount
          sender() ! TransactionSuccess("Successfully Deposited")
        }
      case Withdraw(amount) =>
        if (amount < 0) sender() ! TransactionFailure("Invalid withdraw amount")
        else if (amount > funds) sender() ! TransactionFailure("Insufficient Balance")
        else {
          funds -= amount
          sender() ! TransactionSuccess("Withdraw Success")
        }
      case Statement => sender() ! s"Remaining Funds = $funds"
    }
  }

  object Person {
    case class LiveTheLife(account: ActorRef)
  }

  class Person extends Actor {

    import BankAccount._
    import Person._

    override def receive: Receive = {
      case LiveTheLife(account) =>
        account ! Deposit(1000)
        account ! Withdraw(500)
        account ! Withdraw(600)
        account ! Statement
      case message => println(message.toString)
    }

  }

  val account = system.actorOf(Props[BankAccount], "bankAccount")
  val person = system.actorOf(Props[Person], "Anil")

  person ! LiveTheLife(account)

}