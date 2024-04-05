package com.rs.engine.dialogue

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
    private var pendingLabel: String? = null
    val start = dialogue

    fun player(expression: HeadE, text: String, extraFunctionality: Runnable? = null) {
        dialogue = dialogue.addPlayer(expression, text, extraFunctionality)
        applyPendingLabel()
    }

    fun npc(id: Int, expression: HeadE, text: String, extraFunctionality: Runnable? = null) {
        dialogue = dialogue.addNPC(id, expression, text, extraFunctionality)
        applyPendingLabel()
    }

    fun item(itemId: Int, text: String, extraFunctionality: Runnable? = null) {
        dialogue = dialogue.addItem(itemId, text, extraFunctionality)
        applyPendingLabel()
    }

    fun simple(vararg text: String, extraFunctionality: Runnable? = null) {
        dialogue = if (extraFunctionality != null) {
            dialogue.addSimple(text.first(), extraFunctionality)
        } else {
            dialogue.addSimple(*text)
        }
        applyPendingLabel()
    }

    fun exec(extraFunctionality: Runnable) {
        dialogue = dialogue.addNext(extraFunctionality)
        applyPendingLabel()
    }

    fun addItemToInv(player: Player, item: Item, text: String) {
        dialogue = dialogue.addItemToInv(player, item, text)
        applyPendingLabel()
    }

    fun addItemToInv(player: Player, itemId: Int, text: String) {
        dialogue = dialogue.addItemToInv(player, Item(itemId, 1), text)
        applyPendingLabel()
    }

    fun questStart(quest: Quest) {
        dialogue = dialogue.addQuestStart(quest)
        applyPendingLabel()
    }

    fun options(title: String? = null, setup: OptionsBuilder.() -> Unit) {
        dialogue = dialogue.addOptions(title) { options ->
            OptionsBuilder(stages).apply(setup).applyToOptions(options)
        }
        applyPendingLabel()
    }

    fun makeX(itemId: Int, maxAmt: Int = 60) {
        dialogue = dialogue.addMakeX(itemId, maxAmt)
        applyPendingLabel()
    }

    fun makeX(itemIds: IntArray, maxAmt: Int = 60) {
        dialogue = dialogue.addMakeX(itemIds, maxAmt)
        applyPendingLabel()
    }

    fun label(stageName: String) {
        pendingLabel = stageName
    }

    fun goto(stageName: String, conversation: Conversation? = null) {
        dialogue = if (conversation != null)
            dialogue.addGotoStage(stageName, conversation)
        else
            dialogue.addGotoStage(stageName, stages)
    }

    fun statementWithOptions(statement: Statement, vararg options: DialogueBuilder.() -> Unit) {
        val optionDialogues = options.map { DialogueBuilder(stages).apply(it).build() }
        dialogue = dialogue.addStatementWithOptions(statement, *optionDialogues.toTypedArray())
        applyPendingLabel()
    }

    fun statementWithActions(statement: Statement, vararg events: Runnable) {
        dialogue = dialogue.addStatementWithActions(statement, *events)
        applyPendingLabel()
    }

    fun item(itemId: Int, text: String) {
        dialogue = dialogue.addItem(itemId, text)
        applyPendingLabel()
    }

    fun simple(text: String) {
        dialogue = dialogue.addSimple(text)
        applyPendingLabel()
    }

    fun npc(npc: NPC, expression: HeadE, text: String, extraFunctionality: Runnable? = null) {
        dialogue = dialogue.addNPC(npc, expression, text, extraFunctionality)
        applyPendingLabel()
    }

    fun makeX(itemId: Int) {
        dialogue = dialogue.addMakeX(itemId)
        applyPendingLabel()
    }

    fun makeX(itemIds: IntArray) {
        dialogue = dialogue.addMakeX(itemIds)
        applyPendingLabel()
    }

    fun nextIf(condition: BooleanSupplier, dialogueSetup: DialogueBuilder.() -> Unit) {
        val nextDialogue = DialogueBuilder(stages).apply(dialogueSetup).build()
        dialogue = dialogue.addNextIf(condition, nextDialogue)
        applyPendingLabel()
    }

    fun next(nextDialogue: Dialogue) {
        dialogue = dialogue.addNext(nextDialogue)
        applyPendingLabel()
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

    private fun applyPendingLabel() {
        if (pendingLabel != null) {
            stages[pendingLabel!!] = dialogue
            pendingLabel = null
        }
    }

    internal open fun build(): Dialogue = start
}

@DialogueDsl
class OptionsBuilder(val stages: MutableMap<String, Dialogue>) {
    private sealed class OptionOperation {
        data class BuilderOp(val name: String, val setup: OptionBuilder.() -> Unit) : OptionOperation()
        data class ExecOp(val name: String, val exec: Runnable) : OptionOperation()
    }

    private val operations = mutableListOf<OptionOperation>()

    fun op(name: String, setup: OptionBuilder.() -> Unit = {}) {
        operations.add(OptionOperation.BuilderOp(name, setup))
    }

    fun opExec(name: String, exec: Runnable) {
        operations.add(OptionOperation.ExecOp(name, exec))
    }

    fun applyToOptions(options: Options) {
        operations.forEach { operation ->
            when (operation) {
                is OptionOperation.BuilderOp -> {
                    val builder = OptionBuilder(stages).apply(operation.setup)
                    options.add(operation.name, builder.build())
                }
                is OptionOperation.ExecOp -> {
                    options.add(operation.name, operation.exec)
                }
            }
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

fun createDialogueSection(block: DialogueBuilder.() -> Unit): Dialogue {
    val builder = DialogueBuilder()
    builder.block()
    return builder.build().head
}

fun Player.startConversation(block: DialogueBuilder.() -> Unit) {
    startConversation(DialogueBuilder().apply(block).build())
}

fun Player.sendOptionsDialogue(title: String? = null, setup: OptionsBuilder.() -> Unit) {
    startConversation(DialogueBuilder().apply {
        options(title, setup)
    }.build())
}