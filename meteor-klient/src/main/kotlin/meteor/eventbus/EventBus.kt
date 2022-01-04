package meteor.eventbus

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import meteor.Event
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
class EventBus(override val coroutineContext: CoroutineDispatcher = Dispatchers.Unconfined)
    : CoroutineScope {
    private val handlers = HashMap<Any, EventBus>()

    @OptIn(ObsoleteCoroutinesApi::class)
    private val channel = BroadcastChannel<Any>(1)

    fun getHandler(type: Any): EventBus {
        if (!handlers.keys.contains(type))
            handlers[type] = EventBus()
        return handlers[type]!!
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    fun post(event: Any, context: CoroutineContext = coroutineContext) {
        this.launch(context) {
            channel.send(event)
        }
    }

    fun subscribe(subs: (event: Any) -> Unit,
                  scheduler: CoroutineDispatcher = Dispatchers.Unconfined,
                  filter: ((event: Any) -> Boolean)? = null) {
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

        fun post(type: Any, event: Any) {
            instance.getHandler(type).post(event)
        }

        fun subscribe(type: Any, unit: (Any) -> Unit) {
            instance.getHandler(type).subscribe(unit)
        }
    }
}