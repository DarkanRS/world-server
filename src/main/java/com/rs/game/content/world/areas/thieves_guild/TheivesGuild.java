package com.rs.game.content.world.areas.thieves_guild;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.quest.Quest;
import com.rs.game.content.skills.thieving.PickPocketDummy;
import com.rs.game.content.skills.thieving.PickPocketDummyMK2;
import com.rs.game.ge.GE;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.*;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class TheivesGuild {

    public static LoginHandler login = new LoginHandler(e -> {
        if (Miniquest.A_GUILD_OF_OUR_OWN.isImplemented() && e.getPlayer().isMiniquestComplete(Miniquest.A_GUILD_OF_OUR_OWN, null)) {
            e.getPlayer().getVars().saveVarBit(7792, 40);
            return;
        }
        if (Miniquest.LOST_HER_MARBLES.isImplemented() && e.getPlayer().isMiniquestComplete(Miniquest.LOST_HER_MARBLES, null)) {
            e.getPlayer().getVars().saveVarBit(7792, 20);
            return;
        }
        if (Miniquest.FROM_TINY_ACORNS.isImplemented() && e.getPlayer().isMiniquestComplete(Miniquest.FROM_TINY_ACORNS, null)) {
            e.getPlayer().getVars().saveVarBit(7792, 10);
            return;
        }
        if (Quest.BUYERS_AND_CELLARS.isImplemented() && e.getPlayer().isQuestComplete(Quest.BUYERS_AND_CELLARS, null)) {
            e.getPlayer().getVars().saveVarBit(7792, 10);
        }
    });
    public static ObjectClickHandler handleThievesGuildExitLadder = new ObjectClickHandler(new Object[]{ 52308 }, e ->
            e.getPlayer().useLadder(Tile.of(3223, 3269, 0))
    );

    public static ObjectClickHandler handleThievesGuildEntrance = new ObjectClickHandler(new Object[]{ 52309 }, e -> {
        if (e.getPlayer().getSkills().getLevel(Skills.THIEVING) <= 4) {
            e.getPlayer().sendMessage("The cellar is securely locked.");
            return;
        }
        if (Miniquest.A_GUILD_OF_OUR_OWN.isImplemented() && e.getPlayer().isMiniquestComplete(Miniquest.A_GUILD_OF_OUR_OWN, null)) {
            e.getPlayer().useLadder(Tile.of(4762, 5763, 0));
            e.getPlayer().getVars().saveVarBit(7792, 40); //Darren T4
            e.getPlayer().getMusicsManager().unlockMusic(840);
            return;
        }
        if (Miniquest.LOST_HER_MARBLES.isImplemented() && e.getPlayer().isMiniquestComplete(Miniquest.LOST_HER_MARBLES, null)) {
            e.getPlayer().useLadder(Tile.of(4634, 5763, 0));
            e.getPlayer().getVars().saveVarBit(7792, 20); //Darren T3
            e.getPlayer().getMusicsManager().unlockMusic(838);
            return;
        }
        if (Miniquest.FROM_TINY_ACORNS.isImplemented() && e.getPlayer().isMiniquestComplete(Miniquest.FROM_TINY_ACORNS, null)) {
            e.getPlayer().useLadder(Tile.of(4762, 5891, 0));
            e.getPlayer().getMusicsManager().unlockMusic(839);
            return;
        }
        if (Quest.BUYERS_AND_CELLARS.isImplemented() && e.getPlayer().isQuestComplete(Quest.BUYERS_AND_CELLARS, null)) {
            e.getPlayer().useLadder(Tile.of(4762, 5891, 0));
            e.getPlayer().getVars().saveVarBit(7792, 10); //Darren T2
        } else {
            e.getPlayer().useLadder(Tile.of(4664, 5891, 0));
            e.getPlayer().getMusicsManager().unlockMusic(842);
        }
    });

    public static ObjectClickHandler handleMarkI = new ObjectClickHandler(new Object[]{ 52316 }, e -> {
        if (e.getOption().equalsIgnoreCase("attack")) {
            e.getPlayer().anim(12477);
            e.getPlayer().npcDialogue(11274, HeadE.SHAKING_HEAD, "Now, you see, that's the thing you don't want to be doing if you want to avoid notice.");
        }
        if (e.getOption().equalsIgnoreCase("pickpocket")) {
            e.getPlayer().getActionManager().setAction(new PickPocketDummy(e.getObject()));
        }
    });

    public static ObjectClickHandler handleMarkII = new ObjectClickHandler(new Object[]{52317}, e -> {
        if (e.getOption().equalsIgnoreCase("pickpocket")) {
            e.getPlayer().getActionManager().setAction(new PickPocketDummyMK2(e.getObject()));
        }
    });

    public static ItemClickHandler read = new ItemClickHandler(new Object[]{ 18646 }, new String[]{"Read"}, e -> {
        e.getPlayer().itemDialogue(18646, "You read the instructive document, and feel rather more adept in pilferage and larceny. You gain 175 Thieving XP");
        e.getPlayer().getSkills().addXp(Skills.THIEVING, 175);
        e.getPlayer().getInventory().deleteItem(18646, 1);
    });

    public static NPCClickHandler DodgyDerek = new NPCClickHandler(new Object[]{ 11298 }, e -> {
        switch (e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                    .addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Psst... Need a lockpick or a blackjack? I've got dirty deals to do dirt cheap, so get the goods while they're hot!")
                    .addOptions(ops-> {
                        ops.add("I'll take a look.", () -> ShopsHandler.openShop(e.getPlayer(), "dodgy_dereks_dirty_deals"));
                        ops.add("No.")
                                .addPlayer(HeadE.SHAKING_HEAD, "I'll pass for now, no 'fence meant.");
                    }));
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "dodgy_dereks_dirty_deals");
        }
    });

    @ServerStartupEvent
    public static void addLoSOverrides() {
        Entity.addLOSOverride(11299);
    }

    public static NPCInteractionDistanceHandler MrPinsworthDistance = new NPCInteractionDistanceHandler(new Object[] { 11299 }, (p, n) -> 1);

    public static NPCClickHandler MrPinsworth = new NPCClickHandler(new Object[] { 11299 }, e -> {
        switch(e.getOption()) {
            case "Bank":
                e.getPlayer().getBank().open();
                break;
            case "Collect":
                GE.openCollection(e.getPlayer());
                break;
            case "Talk-to":
                e.getPlayer().startConversation(new Dialogue()
                        .addPlayer(HeadE.CONFUSED, "What're you up to in here?")
                        .addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "We managed to...acquire access to the banking system. Want a look?")
                        .addOptions(ops-> {
                            ops.add("Yes.", () -> e.getPlayer().getBank().open());
                            ops.add("No.")
                                    .addPlayer(HeadE.SHAKING_HEAD, "No thanks.");
                        })
                );
                break;
        }
    });

    public static NPCClickHandler PickpocketingVolunteer = new NPCClickHandler(new Object[]{ 11282, 11284, 11286 }, new String[]{"Talk-to"}, e -> {
        e.getPlayer().npcDialogue(e.getNPCId(), HeadE.SHAKING_HEAD, "Alright there! I'm here for you to practise the ol' dippetydoodah on. If you want to know more, talk to the trainer.");
    });

}
