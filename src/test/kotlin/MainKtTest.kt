import kotlin.test.Test
import kotlin.test.assertEquals

internal class MainKtTest{

    @Test
    fun givenDivision_whenConverting_thenReturnValidOutput() {
        assertEquals( "2.0", divideZeroWise("4", "2"))
        assertEquals("0", divideZeroWise("4", "0"))
    }
}