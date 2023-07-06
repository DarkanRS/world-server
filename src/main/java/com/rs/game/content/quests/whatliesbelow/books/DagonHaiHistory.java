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
        super("The Fall of the Dagon'Hai", """
                '...In scarred lands and ruined fields, that circle flaming towers high, where cruel and cursed torments yield, the Dark of Man, the Dagon'hai.'
                   
                'The Prophecies of Zamorak'
                                 
                ...until the day that the Zamorakian Magi were welcomed among the human numbers. The Zamorakian numbers grew and some moved their order to the city of Varrock. These were the Dagon'hai.
                                 
                The practices of the Dagon'hai were looked darkly upon by the priests of Saradomin within the walls of Varrock and, thus, the Dagon'hai were forced into a hidden war, away from the eyes of the citizens of Varrock.
                                 
                Priests of Saradomin were found dead in darkened streets and crumbling houses. Dagon'hai were cast into cells and tormented through purification. Varrock was torn between the followers of two warring deities. Yet, the people of Varrock did nothing...
                                
                ...until the tower fell. The famed Wizards' Tower in the south burned for what seemed like an age and the people of Varrock knew they had been misled. They turned upon the Zamorakian mages with murderous intent and the Dagon'hai, even with their skills in dark wizardry, were unable to confront such a vengeance-filled assault against their order, supported in full by the priests of Saradomin.
                                 
                The Dagon'hai were all but decimated. Their numbers were shattered and they hid themselves in the darkest houses in the most dimly lit of streets within the city. Those that survived the exodus delved deeper into the most evil of arcane magic, intent on exacting retribution upon the priests of Saradomin and the people of Varrock.
                                 
                Varrock may have fallen if not for the purest of chances. A young guardsman on patrol in the city happened across a dimly lit house in a corner of the city. Sensing something evil in the air, he cautiously peered through one of the small windows and was aghast to see the High Elders of the Dagon'hai engaged in a sacrificial ritual. Sounding the alarm, the guard called for help and the Dagon'hai, knowing they would be slaughtered, fled into the night.
                                 
                The last remaining Dagon'hai were chased throughout Varrock to the great statue of Saradomin himself. Then, they all but disappeared. Nobody knows what happened to them, but some say they were set upon by creatures from the Wilderness. Others say that they teleported themselves away to a place of safety. A few claimed they saw men disappearing into the earth beneath the statue itself. It was as though the statue of Saradomin had claimed their lives in his name.
                                 
                The Dagon'hai never reappeared in Varrock, yet the people knew that one day they would return to have their revenge.
                """);
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
