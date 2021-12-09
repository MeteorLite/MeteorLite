package meteor.events

import meteor.eventbus.Event

class ClientLoaded: Event {
    var msToStart: Long? = null
}