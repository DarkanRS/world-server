val messages = arrayOf(
    Pair(HAPPY_TALKING, "I'm fine!"),
    Pair(CALM_TALK, "I think we need a new king. The one we've got isn't good."),
    Pair(CALM_TALK, "Not too bad. But I'm quite worried about the goblin population these days."),
    Pair(CONFUSED, "Who are you?.."),
    Pair(HAPPY_TALKING, "Hello."),
    Pair(NERVOUS, "I've heard there are many fearsome creatures that dwell underground..."),
    Pair(WORRIED, "I'm a little worried. I've heard there are people killing citizens at random.")
)

onNpcClick("Man", "Woman", options = arrayOf("Talk-to")) { e ->
    val (chathead, text) = messages.random()
    e.player.startConversation {
        player(HAPPY_TALKING, "Hello, how's it going?")
        npc(e.npc.id, chathead, text)
    }
}