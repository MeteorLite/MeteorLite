
package meteor.config




class ConfigItemDescriptor : ConfigObject {
    private val item: ConfigItem? = null
    private val type: Class<*>? = null
    private val range: Range? = null
    private val alpha: Alpha? = null
    private val units: Units? = null
    private val icon: Icon? = null
    override fun key(): String {
        return item!!.keyName
    }

    override fun name(): String {
        return item!!.name
    }

    override fun position(): Int {
        return item!!.position
    }
}