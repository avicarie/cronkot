import exception.ArgumentParseException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import kotlin.test.Test

class MainKtTest {

  @Test
  fun `if there is more than one argument throw argumentParseException with correct message`() {
    assertThatThrownBy {
      parseArgsToCronColumns(
        arrayOf(
          "first arg", "secondArg"
        )
      )
    }.isInstanceOf(ArgumentParseException::class.java).hasMessage("Invalid number of arguments. Expected: 1, got: 2")
  }

  @Test
  fun `if there is different amount of columns in first argument throw argumentParseException with correct message`() {
    assertThatThrownBy {
      parseArgsToCronColumns(
        arrayOf(
          "first arg test",
        )
      )
    }
      .isInstanceOf(ArgumentParseException::class.java)
      .hasMessage("Invalid cron expression. Invalid number of fields - Expected: 6 fields, got: 3")
  }

  @Test
  fun `if correct amount of arguments and columns is provided list with columns split by space character should be returned`() {
    val arg = "*/15 0 1,15 * 1-5 /usr/bin/find"
    val EXPECTED = arrayListOf("*/15", "0", "1,15", "*", "1-5", "/usr/bin/find")

    assertThat(parseArgsToCronColumns(arrayOf(arg))).isEqualTo(EXPECTED);
  }
}