package app.jerry.drink

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.DrinkRank
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.home.HomeViewModel
import app.jerry.drink.util.Logger
import app.jerry.drink.util.Util
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

//    @get: Rule
//    var instantExecutorRule = Instant

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun drinkRank_isCorrect() {
        val mockComment = listOf(
            Comment(star = 3, drink = Drink(drinkId = "1")),
            Comment(star = 5, drink = Drink(drinkId = "1")),
            Comment(star = 3, drink = Drink(drinkId = "1")),
            Comment(star = 5, drink = Drink(drinkId = "1")),
            Comment(star = 3, drink = Drink(drinkId = "2")),
            Comment(star = 3, drink = Drink(drinkId = "2")),
            Comment(star = 3, drink = Drink(drinkId = "2")),
            Comment(star = 3, drink = Drink(drinkId = "3")),
            Comment(star = 2, drink = Drink(drinkId = "3"))
        )

        val target = listOf(
            DrinkRank(
                commentList = mutableListOf(
                    Comment(star = 3, drink = Drink(drinkId = "1")),
                    Comment(star = 5, drink = Drink(drinkId = "1")),
                    Comment(star = 3, drink = Drink(drinkId = "1")),
                    Comment(star = 5, drink = Drink(drinkId = "1"))
                )
                , drink = Drink(drinkId = "1")
                , score = 4F
            ),
            DrinkRank(
                commentList = mutableListOf(
                    Comment(star = 3, drink = Drink(drinkId = "2")),
                    Comment(star = 3, drink = Drink(drinkId = "2")),
                    Comment(star = 3, drink = Drink(drinkId = "2"))
                )
                , drink = Drink(drinkId = "2")
                , score = 3F
            ),
            DrinkRank(
                commentList = mutableListOf(
                    Comment(star = 3, drink = Drink(drinkId = "3")),
                    Comment(star = 2, drink = Drink(drinkId = "3"))
                )
                , drink = Drink(drinkId = "3")
                , score = 2.5F
            )
        )

        val test = Util.getDrinkRank(mockComment)
        assertEquals(target, test)
    }

    @Test
    fun test_isCorrect() {
    }

//    @Test
//    fun testDrinkDrank() {
//        //given
//        val drinkRepository = mockk<DrinkRepository>()
//        val viewModel = HomeViewModel(drinkRepository)
////        viewModel.apiManager = serverApi
//        val commentList = listOf<Comment>()
//        val resultCommentList = Result.Success(commentList)
////        every { serverApi.requestUser() }.returns(userList)
//        runBlocking { `when`(drinkRepository.getNewComment()).thenReturn(resultCommentList) }
//
//        val mockComment = listOf(
//            Comment(star = 3, drink = Drink(drinkId = "1")),
//            Comment(star = 5, drink = Drink(drinkId = "1")),
//            Comment(star = 3, drink = Drink(drinkId = "1")),
//            Comment(star = 5, drink = Drink(drinkId = "1")),
//            Comment(star = 3, drink = Drink(drinkId = "2")),
//            Comment(star = 3, drink = Drink(drinkId = "2")),
//            Comment(star = 3, drink = Drink(drinkId = "2")),
//            Comment(star = 3, drink = Drink(drinkId = "3")),
//            Comment(star = 2, drink = Drink(drinkId = "3"))
//        )
//
//        val target = listOf(
//            DrinkRank(
//                commentList = mutableListOf(
//                    Comment(star = 3, drink = Drink(drinkId = "1")),
//                    Comment(star = 5, drink = Drink(drinkId = "1")),
//                    Comment(star = 3, drink = Drink(drinkId = "1")),
//                    Comment(star = 5, drink = Drink(drinkId = "1"))
//                )
//                , drink = Drink(drinkId = "1")
//                , score = 4F
//            ),
//            DrinkRank(
//                commentList = mutableListOf(
//                    Comment(star = 3, drink = Drink(drinkId = "2")),
//                    Comment(star = 3, drink = Drink(drinkId = "2")),
//                    Comment(star = 3, drink = Drink(drinkId = "2"))
//                )
//                , drink = Drink(drinkId = "2")
//                , score = 3F
//            ),
//            DrinkRank(
//                commentList = mutableListOf(
//                    Comment(star = 3, drink = Drink(drinkId = "3")),
//                    Comment(star = 2, drink = Drink(drinkId = "3"))
//                )
//                , drink = Drink(drinkId = "3")
//                , score = 2.5F
//            )
//        )
//
//        viewModel.getDrinkRank(mockComment)
//        assertEquals(target, viewModel.newDrinkRank.value)
////        runBlocking {  }
////        //when
////        viewModel.getUsers()
////
////        //then
////        assertEquals(1, viewModel.getUsers().value?.size)
//    }
}
