package meteor.events

import meteor.eventbus.Event

class BeforeMenuRender: Event {
    var consumed = false
    fun consume() {
        consumed = true
    }
}