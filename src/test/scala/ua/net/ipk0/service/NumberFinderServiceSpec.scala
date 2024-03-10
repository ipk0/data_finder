package ua.net.ipk0.service

import org.scalatest.{FlatSpec, Matchers}

class NumberFinderServiceSpec extends FlatSpec with Matchers {

  it should "solve the problem within predefined datasets" in new NumberFinderService {
    solve(Array(-1, 0, 2, 5), 1) shouldBe List(Output(0 :: 2 :: Nil, -1 :: 2:: Nil))
    solve(Array(-1, 0, 2, 1), 3) shouldBe List(Output(2 :: 3 :: Nil, 2 :: 1:: Nil))
    solve(Array(11, -8, 9, -4, -5), 3) shouldBe List(Output(0 :: 1 :: Nil, 11 :: -8:: Nil))
    solve(Array(0, 1, 2, 5), 9) shouldBe Nil
    solve(Array(8, 9, 1, -4, -5), 9) shouldBe List(Output(0 :: 2 :: Nil, 8 :: 1:: Nil))
  }
}
