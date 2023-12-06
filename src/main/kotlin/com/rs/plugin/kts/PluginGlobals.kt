package com.rs.plugin.kts

import com.rs.cache.loaders.ObjectType
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.events.*
import com.rs.plugin.handlers.*
import com.rs.utils.TriFunction

fun overrideNpcLOS(vararg npcNamesOrIds: Any) {
    npcNamesOrIds.forEach { require(it is String || it is Int) { "npcNamesOrIds must contain only String or Int types" } }
    npcNamesOrIds.forEach {
        when (it) {
            is String -> Entity.addLOSOverride(it)
            is Int -> Entity.addLOSOverride(it)
        }
    }
}

fun overrideNpcLOS(func: TriFunction<Entity, Any, Boolean, Boolean>) {
    Entity.addLOSOverride(func);
}

fun onNpcClick(vararg npcNamesOrIds: Any, checkDistance: Boolean = true, options: Array<String>? = null, eventHandler: (NPCClickEvent) -> Unit) {
    npcNamesOrIds.forEach { require(it is String || it is Int) { "npcNamesOrIds must contain only String or Int types" } }
    NPCClickEvent.registerMethod(NPCClickEvent::class.java, NPCClickHandler(checkDistance, npcNamesOrIds, options) { eventHandler(it) })
}

fun onObjectClick(vararg objectNamesOrIds: Any, tiles: Array<Tile>? = null, checkDistance: Boolean = true, eventHandler: (ObjectClickEvent) -> Unit) {
    objectNamesOrIds.forEach { require(it is String || it is Int) { "objectNamesOrIds must contain only String or Int types" } }
    ObjectClickEvent.registerMethod(ObjectClickEvent::class.java, ObjectClickHandler(checkDistance, objectNamesOrIds, tiles) { eventHandler(it) })
}

fun onObjectClick(vararg objectNamesOrIds: Any, type: ObjectType, eventHandler: (ObjectClickEvent) -> Unit) {
    objectNamesOrIds.forEach { require(it is String || it is Int) { "objectNamesOrIds must contain only String or Int types" } }
    ObjectClickEvent.registerMethod(ObjectClickEvent::class.java, ObjectClickHandler(objectNamesOrIds, type) { eventHandler(it) })
}

fun onPlayerClick(checkDistance: Boolean = true, option: String, eventHandler: (PlayerClickEvent) -> Unit) {
    PlayerClickEvent.registerMethod(PlayerClickEvent::class.java, PlayerClickHandler(checkDistance, option) { eventHandler(it) })
}

fun onButtonClick(vararg interfaceIds: Int, eventHandler: (ButtonClickEvent) -> Unit) {
    ButtonClickEvent.registerMethod(ButtonClickEvent::class.java, ButtonClickHandler(interfaceIds.toTypedArray()) { eventHandler(it) })
}

fun onDestroyItem(vararg itemNamesOrIds: Any, eventHandler: (DestroyItemEvent) -> Unit) {
    itemNamesOrIds.forEach { require(it is String || it is Int) { "itemNamesOrIds must contain only String or Int types" } }
    DestroyItemEvent.registerMethod(DestroyItemEvent::class.java, DestroyItemHandler(itemNamesOrIds) { eventHandler(it) })
}

fun onDropItem(vararg itemNamesOrIds: Any, eventHandler: (DropItemEvent) -> Unit) {
    itemNamesOrIds.forEach { require(it is String || it is Int) { "itemNamesOrIds must contain only String or Int types" } }
    DropItemEvent.registerMethod(DropItemEvent::class.java, DropItemHandler(itemNamesOrIds) { eventHandler(it) })
}

fun onChunkEnter(eventHandler: (EnterChunkEvent) -> Unit) {
    EnterChunkEvent.registerMethod(EnterChunkEvent::class.java, EnterChunkHandler { eventHandler(it) })
}

fun onInterfaceOnInterface(biDirectional: Boolean = false, fromInterfaceIds: IntArray, fromComponentIds:IntArray?, toInterfaceIds: IntArray, toComponentIds: IntArray?, eventHandler: (IFOnIFEvent) -> Unit) {
    IFOnIFEvent.registerMethod(IFOnIFEvent::class.java, InterfaceOnInterfaceHandler(biDirectional, fromInterfaceIds, fromComponentIds, toInterfaceIds, toComponentIds) { eventHandler(it) })
}

//TODO allow object names and ids mappings
fun onInterfaceOnNPC(/*vararg npcNamesOrIds: Any, */checkDistance: Boolean = true, fromInterfaceIds: IntArray, fromComponentIds:IntArray?, eventHandler: (IFOnNPCEvent) -> Unit) {
    //npcNamesOrIds.forEach { require(it is String || it is Int) { "npcNamesOrIds must contain only String or Int types" } }
    IFOnNPCEvent.registerMethod(IFOnNPCEvent::class.java, InterfaceOnNPCHandler(checkDistance, fromInterfaceIds, fromComponentIds) { eventHandler(it) })
}

//TODO allow object names and ids mappings
fun onInterfaceOnObject(/*vararg objectNamesOrIds: Any, */checkDistance: Boolean = true, fromInterfaceIds: IntArray, fromComponentIds:IntArray?, eventHandler: (InterfaceOnObjectEvent) -> Unit) {
    //objectNamesOrIds.forEach { require(it is String || it is Int) { "npcNamesOrIds must contain only String or Int types" } }
    InterfaceOnObjectEvent.registerMethod(InterfaceOnObjectEvent::class.java, InterfaceOnObjectHandler(checkDistance, fromInterfaceIds, fromComponentIds) { eventHandler(it) })
}

fun onInterfaceOnPlayer(checkDistance: Boolean = true, fromInterfaceIds: IntArray, fromComponentIds:IntArray?, eventHandler: (IFOnPlayerEvent) -> Unit) {
    IFOnPlayerEvent.registerMethod(IFOnPlayerEvent::class.java, InterfaceOnPlayerHandler(checkDistance, fromInterfaceIds, fromComponentIds) { eventHandler(it) })
}

fun onItemAddedToInventory(vararg itemNamesOrIds: Any, eventHandler: (ItemAddedToInventoryEvent) -> Unit) {
    itemNamesOrIds.forEach { require(it is String || it is Int) { "itemNamesOrIds must contain only String or Int types" } }
    ItemAddedToInventoryEvent.registerMethod(ItemAddedToInventoryEvent::class.java, ItemAddedToInventoryHandler(itemNamesOrIds) { eventHandler(it) })
}

fun onItemClick(vararg itemNamesOrIds: Any, options: Array<String>? = null, eventHandler: (ItemClickEvent) -> Unit) {
    itemNamesOrIds.forEach { require(it is String || it is Int) { "itemNamesOrIds must contain only String or Int types" } }
    ItemClickEvent.registerMethod(ItemClickEvent::class.java, ItemClickHandler(itemNamesOrIds, options) { eventHandler(it) })
}

fun onItemEquip(vararg itemNamesOrIds: Any, eventHandler: (ItemEquipEvent) -> Unit) {
    itemNamesOrIds.forEach { require(it is String || it is Int) { "itemNamesOrIds must contain only String or Int types" } }
    ItemEquipEvent.registerMethod(ItemEquipEvent::class.java, ItemEquipHandler(itemNamesOrIds) { eventHandler(it) })
}

fun onItemOnItem(used: IntArray, usedWith: IntArray, eventHandler: (ItemOnItemEvent) -> Unit) {
    ItemOnItemEvent.registerMethod(ItemOnItemEvent::class.java, ItemOnItemHandler(used, usedWith) { eventHandler(it) })
}

fun onItemOnItem(vararg used: Int, eventHandler: (ItemOnItemEvent) -> Unit) {
    ItemOnItemEvent.registerMethod(ItemOnItemEvent::class.java, ItemOnItemHandler(used) { eventHandler(it) })
}

fun onItemOnItem(directKeys: Boolean, keys: Array<Any>, eventHandler: (ItemOnItemEvent) -> Unit) {
    ItemOnItemEvent.registerMethod(ItemOnItemEvent::class.java, ItemOnItemHandler(directKeys, keys) { eventHandler(it) })
}

fun onItemOnPlayer(vararg itemNamesOrIds: Any, eventHandler: (ItemOnPlayerEvent) -> Unit) {
    itemNamesOrIds.forEach { require(it is String || it is Int) { "itemNamesOrIds must contain only String or Int types" } }
    ItemOnPlayerEvent.registerMethod(ItemOnPlayerEvent::class.java, ItemOnPlayerHandler(itemNamesOrIds) { eventHandler(it) })
}

//TODO allow mapping by item names and ids
fun onItemOnNpc(vararg npcNamesOrIds: Any, checkDistance: Boolean = true, eventHandler: (ItemOnNPCEvent) -> Unit) {
    npcNamesOrIds.forEach { require(it is String || it is Int) { "npcNamesOrIds must contain only String or Int types" } }
    ItemOnNPCEvent.registerMethod(IFOnNPCEvent::class.java, ItemOnNPCHandler(checkDistance, npcNamesOrIds) { eventHandler(it) })
}

fun onItemOnObject(objectNamesOrIds: Array<Any>, itemNamesOrIds: Array<Any>, tiles: Array<Tile>?, checkDistance: Boolean = true, eventHandler: (ItemOnObjectEvent) -> Unit) {
    objectNamesOrIds.forEach { require(it is String || it is Int) { "objectNamesOrIds must contain only String or Int types" } }
    ItemOnObjectEvent.registerMethod(ItemOnObjectEvent::class.java, ItemOnObjectHandler(checkDistance, objectNamesOrIds, itemNamesOrIds, tiles) { eventHandler(it) })
}

fun onLogin(eventHandler: (LoginEvent) -> Unit) {
    LoginEvent.registerMethod(LoginEvent::class.java, LoginHandler { eventHandler(it) })
}

fun onNpcDeath(vararg npcNamesOrIds: Any, eventHandler: (NPCDeathEvent) -> Unit) {
    npcNamesOrIds.forEach { require(it is String || it is Int) { "npcNamesOrIds must contain only String or Int types" } }
    NPCDeathEvent.registerMethod(NPCDeathEvent::class.java, NPCDeathHandler(npcNamesOrIds) { eventHandler(it) })
}

fun onNpcDrop(npcNamesOrIds: Array<Any>?, itemNamesOrIds: Array<Any>?, eventHandler: (NPCDropEvent) -> Unit) {
    npcNamesOrIds?.forEach { require(it is String || it is Int) { "npcNamesOrIds must contain only String or Int types" } }
    itemNamesOrIds?.forEach { require(it is String || it is Int) { "itemNamesOrIds must contain only String or Int types" } }
    require(npcNamesOrIds != null || itemNamesOrIds != null) { "no keys passed for npc drop plugin" }
    NPCDropEvent.registerMethod(NPCDropEvent::class.java, NPCDropHandler(npcNamesOrIds, itemNamesOrIds) { eventHandler(it) })
}

fun instantiateNpc(vararg npcNamesOrIds: Any, instantiator: (Int, Tile) -> NPC) {
    npcNamesOrIds.forEach { require(it is String || it is Int) { "npcNamesOrIds must contain only String or Int types" } }
    NPCInstanceEvent.registerMethod(NPCInstanceEvent::class.java, NPCInstanceHandler(npcNamesOrIds) { npcId, tile -> instantiator(npcId, tile) })
}

fun getInteractionDistance(vararg npcNamesOrIds: Any, calc: (Player, NPC) -> Int) {
    npcNamesOrIds.forEach { require(it is String || it is Int) { "npcNamesOrIds must contain only String or Int types" } }
    NPCInteractionDistanceEvent.registerMethod(NPCInteractionDistanceEvent::class.java, NPCInteractionDistanceHandler(npcNamesOrIds) { player, npc -> calc(player, npc) })
}

fun onPickupItem(vararg itemNamesOrIds: Any, tiles: Array<Tile>?, eventHandler: (PickupItemEvent) -> Unit) {
    itemNamesOrIds.forEach { require(it is String || it is Int) { "itemNamesOrIds must contain only String or Int types" } }
    PickupItemEvent.registerMethod(PickupItemEvent::class.java, PickupItemHandler(itemNamesOrIds, tiles) { eventHandler(it) })
}

fun onPlayerStep(vararg tiles: Tile, eventHandler: (PlayerStepEvent) -> Unit) {
    PlayerStepEvent.registerMethod(PlayerStepEvent::class.java, PlayerStepHandler(tiles) { eventHandler(it) })
}

fun onXpDrop(eventHandler: (XPGainEvent) -> Unit) {
    XPGainEvent.registerMethod(XPGainEvent::class.java, XPGainHandler { eventHandler(it) })
}