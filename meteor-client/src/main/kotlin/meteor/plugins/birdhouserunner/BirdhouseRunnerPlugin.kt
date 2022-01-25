package meteor.plugins.birdhouserunner

import com.google.inject.Inject
import dev.hoot.api.commons.Time
import dev.hoot.api.coords.Area
import dev.hoot.api.coords.RectangularArea
import dev.hoot.api.entities.NPCs
import dev.hoot.api.entities.TileObjects
import dev.hoot.api.game.Game
import dev.hoot.api.game.GameThread
import dev.hoot.api.game.Skills
import dev.hoot.api.items.Bank
import dev.hoot.api.items.Inventory
import dev.hoot.api.movement.Movement
import dev.hoot.api.packets.DialogPackets
import dev.hoot.api.packets.MousePackets
import dev.hoot.api.widgets.Dialog
import meteor.eventbus.Subscribe
import meteor.game.ItemManager
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import net.runelite.api.*
import net.runelite.api.events.GameTick
import net.runelite.api.events.ItemContainerChanged
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import org.apache.commons.lang3.time.StopWatch
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Predicate
import kotlin.collections.ArrayList

@PluginDescriptor(
    name = "Birdhouse Runner",
    description = "Does birdhouse runs for you.",
    disabledOnStartup = true,
    enabledByDefault = false
)
class BirdhouseRunnerPlugin : Plugin() {

    companion object {
        private val HOUSE_ON_HILL = RectangularArea(3757, 3876, 3773, 3862, 1)
        private val VERDANT_VALLEY = RectangularArea(3750, 3763, 3762, 3751)
        private val MUSHROOM_MEADOW = RectangularArea(3671, 3875, 3680, 3866)
        private val DIGSITE_PENDANT_IDS = listOf(
            ItemID.DIGSITE_PENDANT_1,
            ItemID.DIGSITE_PENDANT_2,
            ItemID.DIGSITE_PENDANT_3,
            ItemID.DIGSITE_PENDANT_4,
            ItemID.DIGSITE_PENDANT_5
        )
        private val birdhouseObjectIDs = mapOf(
            State.HOUSE1 to 30568,
            State.HOUSE2 to 30567,
            State.HOUSE3 to 30565,
            State.HOUSE4 to 30566
        )

        private fun inventoryContainsN(predicate: Predicate<Item>, quantity: Int): Boolean {
            val container = Game.getClient().getItemContainer(InventoryID.INVENTORY) ?: return false
            var count = 0
            for (item in container.items) {
                if (predicate.test(item)) {
                    count += item.quantity

                    if (count >= quantity)
                        return true
                }
            }
            return false
        }
    }
    internal enum class BirdhouseType(
        val logID: Int,
        val birdhouseID: Int,
        private val hunterLevelReq: Int,
        private val craftingLevelReq: Int
    ) {
        REGULAR(ItemID.LOGS, ItemID.BIRD_HOUSE, 5, 5), OAK(ItemID.OAK_LOGS, ItemID.OAK_BIRD_HOUSE, 15, 14), WILLOW(
            ItemID.WILLOW_LOGS,
            ItemID.WILLOW_BIRD_HOUSE,
            25,
            24
        ),
        TEAK(ItemID.TEAK_LOGS, ItemID.TEAK_BIRD_HOUSE, 35, 34), MAPLE(
            ItemID.MAPLE_LOGS,
            ItemID.MAPLE_BIRD_HOUSE,
            45,
            44
        ),
        MAHAOGANY(ItemID.MAHOGANY_LOGS, ItemID.MAHOGANY_BIRD_HOUSE, 50, 49), YEW(
            ItemID.YEW_LOGS,
            ItemID.YEW_BIRD_HOUSE,
            60,
            59
        ),
        MAGIC(ItemID.MAGIC_LOGS, ItemID.MAGIC_BIRD_HOUSE, 75, 74), REDWOOD(
            ItemID.REDWOOD_LOGS,
            ItemID.REDWOOD_BIRD_HOUSE,
            90,
            89
        );


        fun canMake(): Boolean {
            val hunterLevel = Skills.getLevel(Skill.HUNTER)
            val craftingLevel = Skills.getLevel(Skill.CRAFTING)
            if (hunterLevel >= hunterLevelReq && craftingLevel >= craftingLevelReq) {
                return true
            }
            return false
        }

        companion object {
            val typeToBuild: BirdhouseType?
                get() {
                    val types = values()
                    return types.reversed().firstOrNull { it.canMake() }
                }
        }
    }

    private class InventoryRequirement(val quantity: Int) : Predicate<Item> {
        var ids: List<Int>? = null
        var id: Int? = null

        val containedInInventory
            get() = inventoryContainsN(this, quantity)

        //Accepts list of IDs or an id
        constructor(ids: List<Int>, quantity: Int) : this(quantity) {
            this.ids = ids
        }

        constructor(id: Int, quantity: Int) : this(quantity) {
            this.id = id
        }

        //Can use this class as a predicate
        override fun test(t: Item): Boolean {
            val ids = this.ids
            return (ids?.contains(t.id)) ?: (t.id == id)
        }

        //Gets a match from bank, prioritizing first items in list
        fun getFromBank(): Item? {
            if (ids == null)
                return Bank.getFirst(id!!)
            else {
                var item: Item? = null
                for (x in ids!!) {
                    item = Bank.getFirst(x)
                    if (item != null)
                        return item
                }
                return item
            }
        }

        fun toRequirementListString(itemManager: ItemManager): String {
            val id = ids?.get(0) ?: id!!
            var itemName = itemManager.getItemComposition(id).name
            //Remove (1) from item name
            itemName = itemName.replace(Regex("\\(\\d+\\)"), "").trim()
            return "${quantity}x $itemName"
        }

    }

    @Inject
    lateinit var executor: ScheduledExecutorService

    @Inject
    lateinit var itemManager: ItemManager

    private val player: Player?
        get() = client.localPlayer

    private val baseRequirements = listOf(
        InventoryRequirement(ItemID.CHISEL, 1),
        InventoryRequirement(ItemID.HAMMER, 1),
        InventoryRequirement(DIGSITE_PENDANT_IDS, 1),
        InventoryRequirement(ItemID.BARLEY_SEED, 40),
    )
    private val requirements: List<InventoryRequirement>
        get() {
            val items = ArrayList(baseRequirements)
            items.add(InventoryRequirement(birdhouseType.logID, 4))
            return items
        }


    private fun shutDownWithError(error: String) {
        error.lines().forEach {
            client.addChatMessage(
                ChatMessageType.GAMEMESSAGE,
                "meteor.plugins.changtester.BirdhouseRunPlugin",
                it,
                null
            )
        }
        logger.info("ERROR: $error")
        toggle(false)
    }

    private lateinit var birdhouseType: BirdhouseType

    override fun startup() {
        GameThread.invoke {
            if (!Bank.isOpen() && bankEntity == null) {
                shutDownWithError("Cannot open bank!")
            }
            state = State.BANKING
            val type = BirdhouseType.typeToBuild
            if (type == null) {
                shutDownWithError("Missing level requirements!")
            } else {
                birdhouseType = type
            }
        }
    }

    internal enum class State {
        BANKING,
        TELEPORTING,
        MUSHTREE1,
        HOUSE1,
        HOUSE2,
        MUSHTREE2,
        HOUSE3,
        HOUSE4
    }

    private val birdhouseObjectID
        get() = birdhouseObjectIDs[state] ?: -1
    private val currentBirdhouseObject
        get() = TileObjects.getNearest(birdhouseObjectID)

    private val bankEntity: Pair<Interactable, String>?
        get() {
            val banker = NPCs.getNearest { it.hasAction("Bank") }
            if (banker != null) {
                return Pair(banker, "Bank")
            }
            val bankObject = TileObjects.getNearest { it.hasAction("Bank") }
            if (bankObject != null) {
                return Pair(bankObject, "Bank")
            }
            val bankChest = TileObjects.getNearest { it.name.lowercase(Locale.getDefault()) == "bank chest" }
            if (bankChest != null) {
                return Pair(bankChest, "Use")
            }
            return null
        }
    private var stateTimer = StopWatch()
    private var state = State.BANKING
        set(value) {
            logger.info("Transitioning states: ${state.name} -> ${value.name}")
            stateTimer.reset()
            field = value
            when (value) {
                State.HOUSE1, State.HOUSE2, State.HOUSE3, State.HOUSE4 -> {
                    numSeeds = Inventory.getFirst(ItemID.BARLEY_SEED).quantity
                    numClockworks = Inventory.getAll(ItemID.CLOCKWORK).size
                    numBirdhouses = Inventory.getAll(birdhouseType.birdhouseID).size
                    birdhouseState = BirdhouseState.EMPTYING
                }
            }
        }

    private var tickDelay = 0

    private var tickCounter = 0

    @Subscribe
    fun onGameTick(event: GameTick) {
        tickCounter++
        if (tickDelay > 0) {
            tickDelay--
            return
        }
        when (state) {
            State.BANKING -> doBanking()
            State.TELEPORTING -> doTeleporting()
            State.MUSHTREE1 -> doMushtree1()
            State.HOUSE1, State.HOUSE2, State.HOUSE3 -> doBirdhouse()
            State.MUSHTREE2 -> doMushtree2()
            State.HOUSE4 -> doHouse4()
        }
    }


    private fun doHouse4() {
        if (birdhouseState == BirdhouseState.DONE) {
            toggle(false)
            return
        }
        val birdhouse = TileObjects.getNearest(birdhouseObjectID)
        if (birdhouse == null) {
            if (!Movement.isWalking()) {
                Movement.walkTo(3679, 3814)
            }
        }
        else if (birdhouse.distanceTo(player) > 4){
            if (!Movement.isWalking()) {
                birdhouse.interact(2)
            }
        }
        else {
            doBirdhouse()
        }
    }

    private fun doBirdhouse() {
        val npc = currentBirdhouseObject ?: return
        if (stateTimer.getTime(TimeUnit.SECONDS) >= 10) {
            shutDownWithError("TIMEOUT. Something went wrong!")
            return
        }
        if (player?.isAnimating == true)
            return
        when (birdhouseState) {
            BirdhouseState.EMPTYING -> {
                if (player?.isMoving == true)
                    return
                if (Dialog.isViewingOptions()) {
                    birdhouseState = BirdhouseState.DONE
                    Dialog.chooseOption(2)
                    tickDelay = 1
                    return
                } else {
                    npc.interact(2)
                }
            }
            BirdhouseState.REBUILDING -> {
                val clockwork = Inventory.getFirst(ItemID.CLOCKWORK)
                val logs = Inventory.getFirst(
                    birdhouseType.logID
                )
                if (clockwork == null || logs == null) {
                    return
                }
                clockwork.useOn(logs)
            }
            BirdhouseState.REPLACING -> {
                val birdhouseItem = Inventory.getFirst(birdhouseType.birdhouseID) ?: return
                birdhouseItem.useOn(npc)
            }
            BirdhouseState.INSERTING_SEEDS -> {
                val seeds = Inventory.getFirst(ItemID.BARLEY_SEED)
                seeds.useOn(npc)
            }
            BirdhouseState.DONE -> {
                when (state) {
                    State.HOUSE1 -> state = State.HOUSE2
                    State.HOUSE2 -> state = State.MUSHTREE2
                    State.HOUSE3 -> state = State.HOUSE4
                }
            }
        }
    }

    private fun doBanking() {
        if(stateTimer.getTime(TimeUnit.SECONDS) > 10){
            shutDownWithError("Banking error!")
        }
        if (!Bank.isOpen()) {
            if (player?.isMoving != true) {
                val entity = bankEntity ?: return
                entity.first.interact(entity.second)
            }
        }
        if (Bank.getQuantityMode() != Bank.QuantityMode.ONE) {
            Bank.setQuantityMode(Bank.QuantityMode.ONE)
            tickDelay = 1
            return
        }
        fun handleReq(req: InventoryRequirement): Boolean {
            if (req.containedInInventory)
                return true
            val quantity = req.quantity
            val bankItem = req.getFromBank() ?: return false
            if (bankItem.quantity >= quantity) {
                if (quantity > 5) {
                    MousePackets.queueClickPacket(0, 0)
                    client.invokeMenuAction(
                        "",
                        "",
                        6,
                        MenuAction.CC_OP_LOW_PRIORITY.id,
                        bankItem.slot,
                        WidgetInfo.BANK_ITEM_CONTAINER.packedId
                    )
                    DialogPackets.sendNumberInput(quantity,false);
                }
                else {
                    for (x in 0 until quantity) {
                        Bank.withdraw(req, 1, Bank.WithdrawMode.ITEM)
                    }
                }
                return true
            }
            return false
        }
        for (x in requirements) {
            if (!handleReq(x)) {
                val errorString =
                    StringBuilder("Missing item Requirements\nPlease ensure you have all of the following items in bank:\n")
                for (r in requirements) {
                    errorString.append(r.toRequirementListString(itemManager))
                    errorString.append('\n')
                }
                shutDownWithError(errorString.toString())
                return
            }
        }
        state = State.TELEPORTING
    }

    private fun doTeleporting() {
        if (stateTimer.getTime(TimeUnit.MINUTES) > 1) {
            shutDownWithError("Banking timeout. Required items may be missing!")
            return
        }
        if (Inventory.getFreeSlots() < 2) {
            shutDownWithError("Please free up inventory spaces and re-run.")
            return
        }
        if (HOUSE_ON_HILL.contains(player) || player?.animation == 111) {
            state = State.MUSHTREE1
            return
        }
        if (player?.isAnimating == true)
            return
        if (requirements.all { inventoryContainsN(it, it.quantity) }) {
            Inventory.getFirst { it.id in DIGSITE_PENDANT_IDS }?.interact("Rub")
            executor.execute {
                Time.sleepUntil({ Dialog.isViewingOptions() }, 1800)
                if (!Dialog.isViewingOptions())
                    return@execute
                Dialog.chooseOption(2)
            }
            tickDelay = 1
        }
    }

    private val mushtreeTP2: Widget?
        get() = client.getWidget(608, 8)
    private val mushtreeTP4: Widget?
        get() = client.getWidget(608, 16)

    private fun doMushtree1() = doMushtree(mushtreeTP2, VERDANT_VALLEY, State.HOUSE1)
    private fun doMushtree2() = doMushtree(mushtreeTP4, MUSHROOM_MEADOW, State.HOUSE3)

    //Handle mushtree TP
    private fun doMushtree(tpWidget: Widget?, destination: Area, nextState: State) {
        if (player?.isMoving == true)
            return
        if (destination.contains(player)) {
            state = nextState
            return
        }
        if (tpWidget != null) {
            MousePackets.queueClickPacket(0, 0)
            client.invokeMenuAction("", "", 0, MenuAction.WIDGET_TYPE_6.id, -1, tpWidget.id)
            tickDelay = 10000
            executor.execute {
                Time.sleepUntil({ destination.contains(player) }, 3000)
                tickDelay = 0
            }
            return
        }
        val mushtree = TileObjects.getNearest("Magic Mushtree") ?: return
        mushtree.interact("Use")
        tickDelay = 1
    }


    enum class BirdhouseState {
        EMPTYING,
        REBUILDING,
        REPLACING,
        INSERTING_SEEDS,
        DONE
    }

    private var birdhouseState = BirdhouseState.EMPTYING
    private var numSeeds = 0
    private var numClockworks = 0
    private var numBirdhouses = 0

    @Subscribe
    fun onItemContainerChanged(itemContainerChanged: ItemContainerChanged) {
        val container = itemContainerChanged.itemContainer.items
        val newNumClockworks = container.filter { it.id == ItemID.CLOCKWORK }.size
        val newNumSeeds = container.firstOrNull { it.id == ItemID.BARLEY_SEED }?.quantity ?: 0
        val newNumBirdhouses = container.filter { it.id == birdhouseType.birdhouseID }.size

        //Clockworks
        when (birdhouseState) {
            BirdhouseState.EMPTYING -> {
                if (newNumClockworks == numClockworks + 1) {
                    birdhouseState = BirdhouseState.REBUILDING
                }
            }
            BirdhouseState.REBUILDING -> {
                if (newNumClockworks == numClockworks - 1) {
                    birdhouseState = BirdhouseState.REPLACING
                }
            }
            BirdhouseState.REPLACING -> {
                if (newNumBirdhouses == numBirdhouses - 1) {
                    birdhouseState = BirdhouseState.INSERTING_SEEDS
                }
            }
            BirdhouseState.INSERTING_SEEDS -> {
                if (newNumSeeds < numSeeds) {
                    birdhouseState = BirdhouseState.DONE
                }
            }
        }
        numClockworks = newNumClockworks
        numBirdhouses = newNumBirdhouses
        numSeeds = newNumSeeds
    }

}