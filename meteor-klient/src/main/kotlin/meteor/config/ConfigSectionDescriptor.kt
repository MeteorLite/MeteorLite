
package meteor.config

import lombok.Value


@Value
class ConfigSectionDescriptor : ConfigObject {
    private val key: String? = null
    private val section: ConfigSection? = null
    override fun key(): String {
        return key!!
    }

    override fun name(): String {
        return section!!.name
    }

    override fun position(): Int {
        return section!!.position
    }
}