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
        super("The Journal of Sin'Keth Magis", """
                <u>'...2nd Pentember, Fifth Age, 70</u>
                We have worked for days. It is a weary and tiring journey that my brothers and I must take, but we are close to success! Elder Dag'eth has led us well and he has told us that Zamorak will reward us greatly for our service to Him. The priests of Saradomin haunt our very steps and I fear our discovery. Yet, soon will be the hour of our glory. The Dagon'hai will prevail and the city will be ours! We will throw down the vile yoke of Saradomin and the Dagon'hai will be victorious!
                 
                                  
                <u>9th Pentember, Fifth Age, 70</u>
                Today we donned the filthy robes of the Saradomin priests. It was a foul deed and distasteful to my very soul, yet it had to be done. Without the disguise, we would surely have been found out and ruined. We erected a statue of Saradomin himself just outside the city to the east. Our Lord Zamorak must be laughing in the faces of our enemies at such a deception, for this statue holds the key to our success. Beneath the arrogant caricature of this worthless deity lies the entrance to our most sacred work yet: the Tunnel of Chaos. With this tunnel, we are able to traverse to the very source of our power, the Chaos Temple itself. Those foolish followers of Saradomin do not even sense what we have achieved. They have filled the statue with their accursed holy magic, covering even the merest traces of our work beneath. They have granted us the most perfect of disguises. Zamorak be praised!
                                  
                                  
                <u>11th Pentember, Fifth Age 70</u>
                Excellent news! I have been chosen by Elder Dag'eth to be the next Hyeraph. I, Sin'keth Magis, will lead our people in the incantation of Zamorak's Will. Surely this means I will become High Elder! I must prove worthy to Lord Zamorak. He will not find me wanting. There is much to do in preparation for the ceremony and I do not have long.
                                  
                                  
                <u>24th Septober, Fifth Age, 70</u>
                Disaster! The incantation of Zamorak's Will was discovered by a loathsome watchman, of all people. Zamorak's Blood! The fates are cruel! We could not finish the final rites of the spell. Our work has been undone and we have no time to gather our forces together and hide. We are being followed by the guards and the Priests of Filth are at our heels. We must flee the city! Elder La'nou and Elder Kree'nag were slain whilst protecting the sanctum. Elder Dag'eth will not leave with us. Zamorak take him, he will stand against the hordes that follow us! I am the last of the Elders. The order looks to me now.
                                  
                                 
                <u>27th Septober, Fifth Age, 70</u>
                Today, the last of our order entered the Tunnel of Chaos. We will journey to the Chaos Temple and let Zamorak Himself decide our fate. What happened to Elder Dag'eth, I know not. As the city guards closed upon us, I cast an Earth Bolt spell to collapse the entrance of the tunnel in an avalanche of earth and stone, saving us and dooming us in one breath. There is only one place for us to go now...
                """);
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
