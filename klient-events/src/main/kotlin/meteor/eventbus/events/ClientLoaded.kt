package meteor.eventbus.events

import meteor.Event

class ClientLoaded: Event() {
    var msToStart: Long? = null
}