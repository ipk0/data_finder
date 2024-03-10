package ua.net.ipk0.service

import scala.collection.mutable

class NumberFinderService {

  def solve(data: Array[Int], targetNumber: Int): List[Output] = {
    val tempSet = mutable.HashMap[Int, Int]()

    data.zipWithIndex.flatMap { zipped =>
      val (item, index) = zipped
      val interest = targetNumber - item

      if (tempSet.contains(interest)) {
        val interestIndex = tempSet(interest)
        tempSet.remove(interest)
        Some(Output(interestIndex :: index :: Nil, interest :: item :: Nil))
      } else {
        tempSet.put(item, index)
        None: Option[Output]
      }
    }.toList
  }

/*  def solve(data: Array[Int], targetNumber: Int): List[Output] = {
    data.zipWithIndex.foldLeft(State(List.empty[Output], Map[Int, Int](), targetNumber)){(state, zippedItem) =>
      val (item, index) = zippedItem
      val interest = state.targetNumber - item

      val (updatedMap, list) = if (state.hashMap.contains(interest)){
        val interestIndex = state.hashMap(interest)
        (state.hashMap - interest) -> List(Output(interestIndex :: index :: Nil, interest :: item :: Nil))
      } else {
        val updated: Map[Int, Int] = state.hashMap + (item -> index)
        updated -> List.empty[Output]
      }

      state.copy(acc = list ::: state.acc, hashMap = updatedMap)
    }.acc
  }

  private case class State(acc: List[Output], hashMap: Map[Int, Int], targetNumber: Int)
*/
}