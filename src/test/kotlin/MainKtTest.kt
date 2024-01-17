import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import kotlin.test.Test
import io.mockk.*
import kotlin.test.AfterTest

class MainKtTest {

  @AfterTest
  fun tearDown() {
    unmockkAll()
  }
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
    val expected = arrayListOf("*/15", "0", "1,15", "*", "1-5", "/usr/bin/find")

    assertThat(parseArgsToCronColumns(arrayOf(arg))).isEqualTo(expected);
  }

  @Test
  fun `parseCronColumn should return correct values from column input`(){
    val expected1 = "minute        0 15 30 45"
    val expected2 = "hour          1 15"
    val expected3 = "minute        4 19 34 49"

    assertThat(parseCronColumn("*/15", CronField.MINUTE)).isEqualTo(expected1);
    assertThat(parseCronColumn("1,15", CronField.HOUR)).isEqualTo(expected2);
    assertThat(parseCronColumn("4/15", CronField.MINUTE)).isEqualTo(expected3);
  }
  @Test
  fun `parseCronColumn should  call parseCronList having earlier replaced asterisk to min-max range`(){
    mockkStatic(::parseCronList)

    parseCronColumn("*", CronField.DAY_OF_MONTH)
    verify {parseCronList("1-31", CronField.DAY_OF_MONTH)}

    parseCronColumn("*", CronField.DAY_OF_WEEK)
    verify {parseCronList("0-6", CronField.DAY_OF_WEEK)}

    parseCronColumn("*", CronField.MINUTE)
    verify {parseCronList("0-59", CronField.MINUTE)}
  }

  @Test
  fun `parseCronList should call parseRepeatCron as many times as many values are separated by comma`(){
    mockkStatic(::parseRepeatCron)

    parseCronList("1,3,5", CronField.DAY_OF_WEEK)
    verify(exactly = 3) {parseRepeatCron(any(), CronField.DAY_OF_WEEK)}
  }

  @Test
  fun `parseCronList should call parseRepeatCron once if there is no commas in the argument string`(){
    mockkStatic(::parseRepeatCron)

    parseCronList("1", CronField.DAY_OF_WEEK)
    verify(exactly = 1) {parseRepeatCron(any(), CronField.DAY_OF_WEEK)}
  }


  @Test
  fun `parseRepeatCron should call parseRangeCron with same value as was passed to parseRepeatCron if there is no slash characters in that argument`(){
    mockkStatic(::parseRangeCron)

    parseRepeatCron("1", CronField.DAY_OF_WEEK)
    verify{parseRepeatCron("1", CronField.DAY_OF_WEEK)}
  }

  /**
   * example:
   * value : 4/2, should call parseRangeCron with 4-(max value for cronField)
   * cronField should state the same\
   * value: 4/2 -> 4-6, 2
   *
   */
  @Test
  fun `parseRepeatCron should call parseRangeCron with range from value before slash character to max value for certain cronField`(){
    mockkStatic(::parseRangeCron)

    parseRepeatCron("4/2", CronField.DAY_OF_WEEK)
    verify{parseRangeCron("4-6", 2)}


    parseRepeatCron("4/3", CronField.DAY_OF_MONTH)
    verify{parseRangeCron("4-31", 3)}
  }

  @Test
  fun `parseRepeatCron should call parseRangeCron with default interval of 1 and no changes to value if there is no slash character present`(){
    mockkStatic(::parseRangeCron)

    parseRepeatCron("4", CronField.DAY_OF_WEEK)
    verify{parseRangeCron("4", 1)}


    parseRepeatCron("4-16", CronField.DAY_OF_MONTH)
    verify{parseRangeCron("4-16", 1)}
  }

  @Test
  fun `parseRangeCron should return array of valid repeat numbers`(){
    val expected1 = arrayOf(1,2,3,4)
    val expected2 = arrayOf(4)

    assertThat(parseRangeCron("1-4", 1)).isEqualTo(expected1)
    assertThat(parseRangeCron("4", 1)).isEqualTo(expected2)
  }

}