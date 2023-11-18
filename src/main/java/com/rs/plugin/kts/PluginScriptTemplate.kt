package com.rs.plugin.kts

import com.rs.game.model.entity.Entity
import com.rs.plugin.events.*
import com.rs.plugin.handlers.*
import com.rs.utils.TriFunction
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*

object PluginScriptConfiguration : ScriptCompilationConfiguration(
    {
        defaultImports("com.rs.plugin.kts.*", "com.rs.engine.dialogue.DialogueDsl", "com.rs.engine.dialogue.*", "com.rs.engine.dialogue.HeadE.*")
        ide {
            acceptedLocations(ScriptAcceptedLocation.Everywhere)
        }
    })

@KotlinScript(fileExtension = "plugin.kts", compilationConfiguration = PluginScriptConfiguration::class)
abstract class PluginScriptTemplate {
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

    fun onButtonClick(vararg interfaceIds: Int, eventHandler: (ButtonClickEvent) -> Unit) {
        ButtonClickEvent.registerMethod(ButtonClickEvent::class.java, ButtonClickHandler(interfaceIds) { eventHandler(it) })
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


}
