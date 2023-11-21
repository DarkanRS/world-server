onNpcClick("Man", options = arrayOf("Talk-to")) { e ->
    e.player.startConversation {
        npc(e.npc.id, HAPPY_TALKING, "Hello there, adventurer! What brings you to our town?")
        options {
            //Conditional options example
            if (e.player.inventory.containsItem(1050, 1))
                option("Ask about the town") {
                    npc(e.npc.id, HAPPY_TALKING, "This town is known for its ancient history and beautiful landscapes.")
                    player(HAPPY_TALKING, "That sounds fascinating!")
                    npc(e.npc.id, HAPPY_TALKING, "Feel free to explore and enjoy your stay!")
                }
            option("Ask about quests") {
                npc(e.npc.id, HAPPY_TALKING, "Oh, there are many adventures to be had here!")
                player(HAPPY_TALKING, "Any specific recommendations?")
                npc(e.npc.id, HAPPY_TALKING, "You should visit the old wizard on the hill for a magical quest.")
            }
            option("Say goodbye") {
                player(HAPPY_TALKING, "Thanks for your time. I must be going now.")
                npc(e.npc.id, HAPPY_TALKING, "Safe travels, adventurer!")
            }
        }
    }
}