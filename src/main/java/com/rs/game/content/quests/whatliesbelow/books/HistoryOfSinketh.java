package com.rs.game.content.quests.whatliesbelow.books;

import com.rs.engine.book.Book;
import com.rs.engine.book.BookPage;
import com.rs.engine.quest.Quest;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class HistoryOfSinketh extends Book {

    public HistoryOfSinketh() {
        super("The Journal of Sin'Keth Magis",
                new BookPage(
                        new String[] {

                        },
                        new String[] {

                        }
                ));
    }

    public static ObjectClickHandler find = new ObjectClickHandler(new Object[] { 23092 }, new Tile[] { Tile.of(3216, 3494, 0) }, e -> {
        if (e.getPlayer().getQuestStage(Quest.WHAT_LIES_BELOW) >= 4 && !e.getPlayer().containsItem(11002)) {
            e.getPlayer().getInventory().addItem(11002, 1);
            e.getPlayer().sendMessage("You find an old diary.");
        } else
            e.getPlayer().sendMessage("You find nothing of interest...");
    });
    public static ItemClickHandler read = new ItemClickHandler(new Object[] { 11002 }, new String[] {"Read"}, e -> new HistoryOfSinketh().open(e.getPlayer()));
}
