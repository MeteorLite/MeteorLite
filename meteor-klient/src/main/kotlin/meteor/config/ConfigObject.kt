
package meteor.config

interface ConfigObject {
    fun key(): String?
    fun name(): String
    fun position(): Int
}