package meteor.eventbus

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import meteor.Event
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class EventBus(override val coroutineContext: CoroutineContext
               = Executors.newSingleThreadExecutor().asCoroutineDispatcher())
    : CoroutineScope {
    val handlers = HashMap<Class<out Event>, EventBus>()

    private val channel = BroadcastChannel<Event>(1)

    fun getHandler(type: Class<out Event>): EventBus {
        if (!handlers.keys.contains(type))
            handlers[type] = EventBus()
        return handlers[type]!!
    }

    fun post(event: Event, context: CoroutineContext = coroutineContext) {
        this.launch(context) {
            channel.send(event)
        }
    }

    fun subscribe(subs: (event: Event) -> Unit,
                  scheduler: CoroutineDispatcher = Dispatchers.Unconfined,
                  filter: ((event: Event) -> Boolean)? = null) {
        this.launch {
            channel.asFlow().collect { item ->
                if (filter?.invoke(item) != false) {
                    withContext(scheduler) {
                        subs.invoke(item)
                    }
                }
            }
        }
    }

    companion object {
        var instance = EventBus()


        fun post(type: Class<out Event>, event: Event) {
            instance.getHandler(type).post(event)
        }

        fun subscribe(type: Class<out Event>, unit: (Event) -> Unit) {
            instance.getHandler(type).subscribe(unit)
        }
    }
}