package com.rs.engine.dialogue;

import com.rs.engine.dialogue.statements.Statement
import com.rs.engine.quest.Quest
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import java.util.function.BooleanSupplier

@DslMarker
annotation class DialogueDsl

@DialogueDsl
open class DialogueBuilder() {
    private val dialogue = Dialogue()

    fun player(expression: HeadE, text: String, extraFunctionality: Runnable? = null) {
        dialogue.addPlayer(expression, text, extraFunctionality)
    }

    fun npc(id: Int, expression: HeadE, text: String, extraFunctionality: Runnable? = null) {
        dialogue.addNPC(id, expression, text, extraFunctionality)
    }

    fun item(itemId: Int, text: String, extraFunctionality: Runnable? = null) {
        dialogue.addItem(itemId, text, extraFunctionality)
    }

    fun simple(vararg text: String, extraFunctionality: Runnable? = null) {
        if (extraFunctionality != null) {
            dialogue.addSimple(text.first(), extraFunctionality)
        } else {
            dialogue.addSimple(*text)
        }
    }

    fun exec(extraFunctionality: Runnable) {
        dialogue.addNext(extraFunctionality)
    }

    fun addItemToInv(player: Player, item: Item, text: String) {
        dialogue.addItemToInv(player, item, text)
    }

    fun questStart(quest: Quest) {
        dialogue.addQuestStart(quest)
    }

    fun options(title: String? = null, setup: OptionsBuilder.() -> Unit) {
        dialogue.addOptions(title) { options ->
            OptionsBuilder().apply(setup).applyToOptions(options)
        }
    }

    fun makeX(itemId: Int, maxAmt: Int = 60) {
        dialogue.addMakeX(itemId, maxAmt)
    }

    fun makeX(itemIds: IntArray, maxAmt: Int = 60) {
        dialogue.addMakeX(itemIds, maxAmt)
    }

    fun gotoStage(stageName: String, conversation: Conversation) {
        dialogue.addGotoStage(stageName, conversation)
    }

    fun statementWithOptions(statement: Statement, vararg options: DialogueBuilder.() -> Unit) {
        val optionDialogues = options.map { DialogueBuilder().apply(it).build() }
        dialogue.addStatementWithOptions(statement, *optionDialogues.toTypedArray())
    }

    fun statementWithActions(statement: Statement, vararg events: Runnable) {
        dialogue.addStatementWithActions(statement, *events)
    }

    fun item(itemId: Int, text: String) {
        dialogue.addItem(itemId, text)
    }

    fun simple(text: String) {
        dialogue.addSimple(text)
    }

    fun npc(npc: NPC, expression: HeadE, text: String, extraFunctionality: Runnable? = null) {
        dialogue.addNPC(npc, expression, text, extraFunctionality)
    }

    fun makeX(itemId: Int) {
        dialogue.addMakeX(itemId)
    }

    fun makeX(itemIds: IntArray) {
        dialogue.addMakeX(itemIds)
    }

    fun nextIf(condition: BooleanSupplier, dialogueSetup: DialogueBuilder.() -> Unit) {
        val nextDialogue = DialogueBuilder().apply(dialogueSetup).build()
        dialogue.addNextIf(condition, nextDialogue)
    }

    fun stop() {
        dialogue.addStop()
    }

    fun setStage(stageName: String, conversation: Conversation) {
        dialogue.setStage(stageName, conversation)
    }

    fun voiceEffect(voiceId: Int) {
        dialogue.voiceEffect(voiceId)
    }

    internal open fun build(): Dialogue = dialogue
}

@DialogueDsl
class OptionsBuilder() {
    private val optionBuilders = mutableListOf<Pair<String, OptionBuilder.() -> Unit>>()

    fun option(name: String, setup: OptionBuilder.() -> Unit = {}) {
        optionBuilders.add(name to setup)
    }

    fun applyToOptions(options: Options) {
        optionBuilders.forEach { (name, setup) ->
            val builder = OptionBuilder().apply(setup)
            options.add(name, builder.build())
        }
    }
}

@DialogueDsl
class OptionBuilder : DialogueBuilder()

fun dialogue(block: DialogueBuilder.() -> Unit): Dialogue {
    val builder = DialogueBuilder()
    builder.block()
    return builder.build()
}

fun Player.startConversation(block: DialogueBuilder.() -> Unit) {
    startConversation(DialogueBuilder().apply(block).build())
}