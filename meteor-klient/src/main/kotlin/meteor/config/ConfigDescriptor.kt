
package meteor.config






class ConfigDescriptor(
    private val group: ConfigGroup, private val sections: Collection<ConfigSectionDescriptor>,
    private val titles: Collection<ConfigTitleDescriptor>, private val items: Collection<ConfigItemDescriptor>
)