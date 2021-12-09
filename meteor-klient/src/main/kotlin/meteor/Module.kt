package meteor
import net.runelite.api.hooks.Callbacks
import org.koin.core.component.KoinComponent
import org.koin.dsl.bind
import org.koin.dsl.module

object Module: KoinComponent {
    val CLIENT_MODULE = module {
        single { UI() }
        single<Callbacks> { Hooks() } bind Callbacks::class
    }
}

