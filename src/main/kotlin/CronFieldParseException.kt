class CronFieldParseException(message: String, cronField: CronField) :
  RuntimeException("$message at field ${cronField.title}")