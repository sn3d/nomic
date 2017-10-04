package nomic.hive

/**
 * @author zdenko.vrabel@wirecard.com
 */
class InvalidHiveQueryException(query: String, e: Throwable) : RuntimeException("Invalid query ${query}. See error below", e)
class CannotDropHiveTableException(schema: String, table: String) : RuntimeException("Cannot drop the table '${schema}.${table}'.")
