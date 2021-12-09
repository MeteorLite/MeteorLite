
package meteor.config


class ConfigTitleDescriptor : ConfigObject {
    private val key: String? = null
    private val title: ConfigTitle? = null
    override fun key(): String? {
        return key
    }

    override fun name(): String {
        return title!!.name
    }

    override fun position(): Int {
        return title!!.position
    }
}