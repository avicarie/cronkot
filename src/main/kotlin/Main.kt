fun main(args: Array<String>) {
  val fields = parseArgsToCronColumns(args)
}

fun parseArgsToCronColumns(args: Array<String>) = args[0].split(" ".toRegex())
