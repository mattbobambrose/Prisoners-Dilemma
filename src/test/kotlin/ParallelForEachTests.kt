import com.mattbobambrose.prisoner.common.ParallelForEach.forEach
import org.junit.Assert.assertThrows
import org.junit.Test

class ParallelForEachTests {
    @Test
    fun `Make sure all items are read once`() {
        val actualList = mutableListOf<Int>()
        val testList = List(10) { it }
        testList.forEach(3) {
//            println(it)
            actualList.add(it)
        }
        assert(actualList.sorted() == testList.sorted())
    }

    @Test
    fun `Throw exception if coroutine count is less than 1`() {
        assertThrows(IllegalArgumentException::class.java) {
            listOf(1, 2, 3).forEach(0) {
                println(it)
            }
        }
    }
}