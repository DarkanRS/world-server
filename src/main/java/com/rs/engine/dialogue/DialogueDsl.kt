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
open class DialogueBuilder(val stages: MutableMap<String, Dialogue> = mutableMapOf()) {
    private var dialogue = Dialogue()
    val start = dialogue;

    fun player(expression: HeadE, text: String, extraFunctionality: Runnable? = null) {
        dialogue = dialogue.addPlayer(expression, text, extraFunctionality)
    }

    fun npc(id: Int, expression: HeadE, text: String, extraFunctionality: Runnable? = null) {
        dialogue = dialogue.addNPC(id, expression, text, extraFunctionality)
    }

    fun item(itemId: Int, text: String, extraFunctionality: Runnable? = null) {
        dialogue = dialogue.addItem(itemId, text, extraFunctionality)
    }

    fun simple(vararg text: String, extraFunctionality: Runnable? = null) {
        dialogue = if (extraFunctionality != null) {
            dialogue.addSimple(text.first(), extraFunctionality)
        } else {
            dialogue.addSimple(*text)
        }
    }

    fun exec(extraFunctionality: Runnable) {
        dialogue = dialogue.addNext(extraFunctionality)
    }

    fun addItemToInv(player: Player, item: Item, text: String) {
        dialogue = dialogue.addItemToInv(player, item, text)
    }

    fun questStart(quest: Quest) {
        dialogue = dialogue.addQuestStart(quest)
    }

    fun options(title: String? = null, setup: OptionsBuilder.() -> Unit) {
        dialogue = dialogue.addOptions(title) { options ->
            OptionsBuilder(stages).apply(setup).applyToOptions(options)
        }
    }

    fun makeX(itemId: Int, maxAmt: Int = 60) {
        dialogue = dialogue.addMakeX(itemId, maxAmt)
    }

    fun makeX(itemIds: IntArray, maxAmt: Int = 60) {
        dialogue = dialogue.addMakeX(itemIds, maxAmt)
    }

    fun label(stageName: String) {
        stages[stageName] = dialogue
    }

    fun goto(stageName: String, conversation: Conversation? = null) {
        dialogue = if (conversation != null)
            dialogue.addGotoStage(stageName, conversation)
        else
            dialogue.addGotoStage(stages[stageName])
    }

    fun statementWithOptions(statement: Statement, vararg options: DialogueBuilder.() -> Unit) {
        val optionDialogues = options.map { DialogueBuilder(stages).apply(it).build() }
        dialogue = dialogue.addStatementWithOptions(statement, *optionDialogues.toTypedArray())
    }

    fun statementWithActions(statement: Statement, vararg events: Runnable) {
        dialogue = dialogue.addStatementWithActions(statement, *events)
    }

    fun item(itemId: Int, text: String) {
        dialogue = dialogue.addItem(itemId, text)
    }

    fun simple(text: String) {
        dialogue = dialogue.addSimple(text)
    }

    fun npc(npc: NPC, expression: HeadE, text: String, extraFunctionality: Runnable? = null) {
        dialogue = dialogue.addNPC(npc, expression, text, extraFunctionality)
    }

    fun makeX(itemId: Int) {
        dialogue = dialogue.addMakeX(itemId)
    }

    fun makeX(itemIds: IntArray) {
        dialogue = dialogue.addMakeX(itemIds)
    }

    fun nextIf(condition: BooleanSupplier, dialogueSetup: DialogueBuilder.() -> Unit) {
        val nextDialogue = DialogueBuilder(stages).apply(dialogueSetup).build()
        dialogue = dialogue.addNextIf(condition, nextDialogue)
    }

    fun stop() {
        dialogue = dialogue.addStop()
    }

    fun setStage(stageName: String, conversation: Conversation) {
        dialogue = dialogue.setStage(stageName, conversation)
    }

    fun voiceEffect(voiceId: Int) {
        dialogue = dialogue.voiceEffect(voiceId)
    }

    internal open fun build(): Dialogue = start
}

@DialogueDsl
class OptionsBuilder(val stages: MutableMap<String, Dialogue>) {
    private val optionBuilders = mutableListOf<Pair<String, OptionBuilder.() -> Unit>>()

    fun op(name: String, setup: OptionBuilder.() -> Unit = {}) {
        optionBuilders.add(name to setup)
    }

    fun applyToOptions(options: Options) {
        optionBuilders.forEach { (name, setup) ->
            val builder = OptionBuilder(stages).apply(setup)
            options.add(name, builder.build())
        }
    }
}

@DialogueDsl
class OptionBuilder(stages: MutableMap<String, Dialogue>) : DialogueBuilder(stages)

fun dialogue(block: DialogueBuilder.() -> Unit): Dialogue {
    val builder = DialogueBuilder()
    builder.block()
    return builder.build()
}

fun Player.startConversation(block: DialogueBuilder.() -> Unit) {
    startConversation(DialogueBuilder().apply(block).build())
}