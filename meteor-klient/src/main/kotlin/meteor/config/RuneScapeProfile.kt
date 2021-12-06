
package meteor.config


class RuneScapeProfile {
    private val displayName: String? = null

    private val loginHash: ByteArray
        get() {
            TODO()
        }

    /**
     * Profile key used to save configs for this profile to the config store. This will always start
     * with [ConfigManager.RSPROFILE_GROUP]
     */
    private val key: String? = null
}