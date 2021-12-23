package meteor.plugins

class Plugin {
    var enabled = false

    fun start() {
        enabled = true
    }

    fun stop() {
        enabled = false
    }

    fun onStart() {

    }

    fun onStop() {

    }
}