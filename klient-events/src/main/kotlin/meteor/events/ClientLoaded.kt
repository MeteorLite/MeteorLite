package meteor.events

import meteor.Event

class ClientLoaded: Event() {
    var msToStart: Long? = null
}