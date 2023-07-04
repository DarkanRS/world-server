package com.rs.game.content.quests.whatliesbelow.books;

import com.rs.engine.book.Book;
import com.rs.engine.book.BookPage;
import com.rs.engine.quest.Quest;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class DagonHaiHistory extends Book {
    public DagonHaiHistory() {
        super("The Fall of the Dagon'Hai",
                new BookPage(
                        new String[] {
                                "'...In scarred lands and",
                                "ruined fields"
                        },
                        new String[] {

                        }
                ));
    }

    public static ObjectClickHandler find = new ObjectClickHandler(new Object[] { 23091 }, new Tile[] { Tile.of(3215, 3494, 0) }, e -> {
        if (e.getPlayer().getQuestStage(Quest.WHAT_LIES_BELOW) >= 4 && !e.getPlayer().containsItem(11001)) {
            e.getPlayer().getInventory().addItem(11001, 1);
            e.getPlayer().sendMessage("You find a musty old tome.");
        } else
            e.getPlayer().sendMessage("You find nothing of interest...");
    });
    public static ItemClickHandler read = new ItemClickHandler(new Object[] { 11001 }, new String[] {"Read"}, e -> new DagonHaiHistory().open(e.getPlayer()));
}
