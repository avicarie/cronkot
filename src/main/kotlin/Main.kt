import exception.ArgumentParseException

fun main(args: Array<String>) {
  val fields = parseArgsToCronColumns(args)
  println(fields)
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


