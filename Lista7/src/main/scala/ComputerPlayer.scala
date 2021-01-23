import HumanPlayer.ChoosePit
import Server.Move
import akka.actor.Actor

import scala.util.Random

class ComputerPlayer(val number: Byte) extends Actor{
  override def receive: Receive = {
    case ChoosePit(board: Board) =>
//      var choice = -1
//      while(!board.isChosenPitCorrect(choice)){
//        choice = Random.between(0, 13)
//      }
//      println(s"Computer player's choice: $choice")
//      sender() ! Move(choice)
      val choice = getBestPossibleMove(board)
      println(s"Computer player's choice: $choice")
      sender() ! Move(choice)
  }

  private def minMax(currentBoard: Board, depth: Int, isMaximizing: Boolean, callingPlayerNumber: Byte): Int= {
    var currentAdvantage = Int.MinValue
    if(depth == 0) return currentBoard.calculateAdvantage(currentBoard.getActivePlayerNumber)
    if(!currentBoard.isNextMovePossible){
      currentBoard.endGameCollectSeeds()
      if(currentBoard.calculateAdvantage(callingPlayerNumber) > 0) {
        return Int.MaxValue
      }
      else return currentBoard.calculateAdvantage(currentBoard.getActivePlayerNumber)
    }

    if(isMaximizing){
      for(i <- 0 to 13){
        if(currentBoard.isChosenPitCorrect(i)){
          val newBoard = currentBoard.clone()
          newBoard.moveSeedsFrom(i)
          newBoard.determineNextPlayerNumber()
          val advantage = minMax(newBoard, depth - 1, if(callingPlayerNumber == newBoard.getActivePlayerNumber) true else false, callingPlayerNumber)
          currentAdvantage = currentAdvantage.max(advantage)
        }
      }
    }
    else{
      currentAdvantage = Int.MaxValue

      for(i <- 0 to 13){
        if(currentBoard.isChosenPitCorrect(i)){
          val newBoard = currentBoard.clone()
          newBoard.moveSeedsFrom(i)
          newBoard.determineNextPlayerNumber()
          val advantage = minMax(newBoard, depth - 1, if(callingPlayerNumber != newBoard.getActivePlayerNumber) true else false, callingPlayerNumber)
          currentAdvantage = currentAdvantage.min(advantage)
        }
      }
    }
    currentAdvantage
  }

  private def getBestPossibleMove(board: Board): Int = {
    var currentBestMove = -1
    var highestAdvantage = Int.MinValue

    for(i <- 0 to 13){
      if(board.isChosenPitCorrect(i)){
        val newBoard = board.clone()
        newBoard.moveSeedsFrom(i)
        newBoard.determineNextPlayerNumber()
        val advantage = minMax(newBoard, 9, board.getActivePlayerNumber == newBoard.getActivePlayerNumber, board.getActivePlayerNumber)

        if(advantage > highestAdvantage){
          highestAdvantage = advantage
          currentBestMove = i
        }
      }
    }
    currentBestMove
  }
}
