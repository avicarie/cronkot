fun main(args: Array<String>) {
  val columns = parseArgsToCronColumns(args)
  for ((index, column) in columns.withIndex()) {
    println(parseCronColumn(column, CronField.values()[index]))
  }
}

fun parseArgsToCronColumns(args: Array<String>): List<String> {
  if (args.size != 1) {
    throw ArgumentParseException("Invalid number of arguments. Expected: 1, got: ${args.size}");
  }
  val fields = args[0].split(" ".toRegex())
  if (fields.size != 6) {
    throw ArgumentParseException("Invalid cron expression. Invalid number of fields - Expected: 6 fields, got: ${fields.size}");
  }
  return fields
}

fun parseCronColumn(column: String, cronField: CronField): String {
  var columnTemp = column;
  if (cronField == CronField.COMMAND) { // skip parsing for command
    return "${cronField.title.padEnd(14, ' ')}${columnTemp}"
  }


  //replace '*' and '?' with min-max values for each column
  if (columnTemp.contains("*")) {
    columnTemp = columnTemp.replace("*", "${cronField.min}-${cronField.max}")
  }
  if (columnTemp.contains("?")) {
    columnTemp = columnTemp.replace("*", "${cronField.min}-${cronField.max}")
  }

  return "${cronField.title.padEnd(14, ' ')}${
    parseCronList(columnTemp, cronField).joinToString(" ")
  }"
}

/**
 * parse cron by ',' separator
 */
fun parseCronList(value: String, cronField: CronField): Array<Int> {
  val elements = value.split(',')

  if (elements.any { it.isEmpty() }) {
    throw CronFieldParseException("Invalid cron list value", cronField)
  }
  // if the elements length is greater than 1 it is a cron list
  if (elements.size > 1) {
    // parse each of them as repeat sequence by '/'
    return elements.flatMap { parseRepeatCron(it, cronField).asList() }.toTypedArray()

  } else {
    // if size is equal less or equal to 1 then there is no list so parse whole of it as repeat sequence by '/'
    return parseRepeatCron(value, cronField)
  }
}

/**
 * parse cron by '/' separator
 */
fun parseRepeatCron(value: String, cronField: CronField): Array<Int> {
  val elements = value.split('/').toMutableList()
  if (elements.size > 2) {
    throw CronFieldParseException("Invalid cron repeat value", cronField)
  }

  if (elements.size == 2) {
    // since 1/5 == 1-maxValueForColumn/5 we convert any numeric value before '/' to that value - max
    if (elements[0].all { it.isDigit() }) {
      elements[0] = "${elements[0]}-${cronField.max}"
    }
    return parseRangeCron(elements[0], elements[1].toInt())
  }

  return parseRangeCron(value, 1);
}

/**
 * parse cron by '-' separator
 */
fun parseRangeCron(value: String, interval: Int): Array<Int> {
  val elements = value.split('-')

  if (elements.size == 1) {
    return arrayOf(value.toInt())
  }
  val valuesList = mutableListOf<Int>()
  if (elements.size > 1) {
    val min = elements[0].toInt()
    val max = elements[1].toInt()
    // toDo: validate if those are numbers, are in range, and if max if greater than min
    // toDo: validate interval

    var repeatIndex = interval;
    for (i in min..max) {
      if (interval > 0 && repeatIndex % interval == 0) {
        repeatIndex = 1;
        valuesList.add(i)
      } else {
        repeatIndex++;
      }
    }
  }
  return valuesList.toTypedArray()
}


