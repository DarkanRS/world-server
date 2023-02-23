package com.rs.game.content.holidayevents.easter.easter22.npcs;

import com.rs.game.content.holidayevents.easter.easter22.Easter2022;
import com.rs.game.content.holidayevents.easter.easter22.EggHunt;
import com.rs.game.content.world.unorganized_dialogue.StageSelectDialogue;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.music.Music;

@PluginEventHandler
public class EvilChickenD extends Conversation {

    public EvilChickenD(Player player) {
        super(player);
        if (!player.getNSV().getB("talkedWithEvilChicken") && player.getNSV().getB("talkedWithChocatrice")) {
            addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "You shouldn't have spoken to that Chocatrice. He's *bwaaak* *bwaaak* mad.");
        } else if (!player.getNSV().getB("talkedWithEvilChicken") && !player.getNSV().getB("talkedWithChocatrice")) {
            addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "I'm glad you spoke with me first and not that chocolate bar on legs.");
            addPlayer(HeadE.CALM_TALK, "What are you two doing here?");
        } else if (player.getNSV().getB("talkedWithEvilChicken")) {
            addNPC(Easter2022.EVIL_CHICKEN, HeadE.CAT_SHOUTING, "Report in, soldier. *bwaaak*");

            if (EggHunt.hasCompletedHunt(player)) {
                if (!player.getEmotesManager().unlockedEmote(Easter2022.EASTER_EMOTE)) {
                    player.getEmotesManager().unlockEmote(Easter2022.EASTER_EMOTE);
                    player.sendMessage("You have unlocked an Easter emote, " + Easter2022.EASTER_EMOTE.name().toLowerCase().replace("_", " ") + ".");
                }
                if (!player.getMusicsManager().hasMusic(Easter2022.EASTER_TRACK)) {
                    player.getMusicsManager().unlockMusic(Easter2022.EASTER_TRACK);
                    player.sendMessage("You have unlocked an Easter track, " + Music.getSong(Easter2022.EASTER_TRACK).getName() + ".");
                }
                addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Well done, soldier, you've found all the eggs this hunt. You're delightfully despicable.");
                addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, EggHunt.getTimeString());
            }

            if (player.getCounterValue(Easter2022.STAGE_KEY + "CompletedHunts") >= 3 && !player.getDiangoReclaim().contains(Easter2022.PERMANENT_EGGSTERMINATOR)) {
                addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Good work, you must enjoy smashing those eggs. Seeing you've been so helpfully destructive I can enchant your Eggsterminator so that it will last after Easter. " +
                        "Want me to do that for you?");
                addOptions(new Options("unlockPermanent", EvilChickenD.this) {
                    @Override
                    public void create() {
                        option("Yes", new Dialogue()
                                .addNext(() -> {
                                    player.addDiangoReclaimItem(Easter2022.PERMANENT_EGGSTERMINATOR);
                                    if (player.getEquipment().getWeaponId() == Easter2022.EGGSTERMINATOR) {
                                    	player.getEquipment().setNoPluginTrigger(Equipment.WEAPON, new Item(Easter2022.PERMANENT_EGGSTERMINATOR)); 
                                        player.sendMessage("The Evil Chicken enchants your Eggsterminator...");
                                    }
                                })
                                .addItem(Easter2022.PERMANENT_EGGSTERMINATOR, "You will now be able to keep the Eggsterminator after the Easter event.")
                                .addGotoStage("huntOps", EvilChickenD.this));
                        option("No", new Dialogue().addGotoStage("continued", EvilChickenD.this));
                    }
                });
            }

            if (player.getInventory().containsItem(Easter2022.EVIL_DRUMSTICK, 3) && !player.getDiangoReclaim().contains(Easter2022.EGG_ON_FACE_MASK)) {
                addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "You have three succulent drumsticks on you. May I have them?");
                addOptions(new Options("buyMask", this) {
                    @Override
                    public void create() {
                        option("Yes", () -> {
                            player.getInventory().deleteItem(Easter2022.EVIL_DRUMSTICK, 3);
                            player.addDiangoReclaimItem(Easter2022.EGG_ON_FACE_MASK);
                            player.getInventory().addItemDrop(Easter2022.EGG_ON_FACE_MASK, 1);
                            addItem(Easter2022.EGG_ON_FACE_MASK, "You receive the egg on face mask.").addGotoStage("huntOps", EvilChickenD.this);
                        });
                        option("No", new Dialogue().addGotoStage("huntOps", EvilChickenD.this));
                    }
                });
            }
            addNext(new StageSelectDialogue("huntOps", this));
        }

        player.getNSV().setB("talkedWithEvilChicken", true);
        create("continued", addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "I'm hosting this year's *bwaaak* Easter hunt. Time to take things in-wing. I'm sick of the incessant, poorly timed *bwaaak* predictable egg jokes you get every single Easter."));
        addPlayer(HeadE.CALM_TALK, "They must be getting old.");
        addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Egg-xactly. *bwaaak* *bwaaak* Now, you may have heard that the Easter Bunny is recovering from a terrible accident. " +
                "He mistakenly fell, from quite a height, into a pit of lava. *bwaaak* What a terribly unfortunate accident.");
        addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Luckily, his accident occurred after placing the eggs for this year's hunt. Five delicious, giant, magical eggs, scattered around Runescape.");

        if (player.getNSV().getB("talkedWithChocatrice")) {
            addPlayer(HeadE.CALM_TALK, "Let me guess. You want me to hunt down these eggs and blow them open with the Eggsterminator?");
            addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "So the Chocatrice already told you, did he?");
        } else {
            addPlayer(HeadE.CALM_TALK, "So you want me to find these giant eggs?");
            addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Yes, and blow them open with this.");
            addItem(Easter2022.EGGSTERMINATOR, "The Evil Chicken shows you the Eggsterminator.");
        }

        addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Once the egg is obliterated, a cute little chick will escape...");
        addPlayer(HeadE.CHEERFUL, "Awww...");
        addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Deep-fry it with the cannon! *bwaaak* *bwaaak* *bwaaak*");

        addOptions(new Options("takingSides", this) {
            @Override
            public void create() {
                option("Certainly!", new Dialogue()
                        .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "It's crucial that you don't listen to that Chocatrice. He thinks we should turn those chicks into chocolate. Disgusting.... Deep-fried chicken is far more tasty.")
                        .addOptions(new Options() {
                            @Override
                            public void create() {
                                option("Chocolate does sound better.", new Dialogue()
                                        .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Traitor! *bwaaak*!")
                                        .addGotoStage("declareWar", EvilChickenD.this));
                                option("I'm more of a friend chicken kind of " + (player.getAppearance().isMale() ? "guy." : "gal."), new Dialogue()
                                        .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Yes... Juicy, fried chicken. Delicious!")
                                        .addGotoStage("declareWar", EvilChickenD.this));
                            }
                        }));
                option("Why are you deep-frying chickens?", new Dialogue()
                        .addGotoStage("declareWar", EvilChickenD.this));
            }
        });

        create("declareWar",
                addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "That stupid bird thinks he can turn more chicks into chocolate than I can into drumsticks. If it's war he wants, it's war he'll get."));

        addOptions(new Options("huntOps", EvilChickenD.this) {
            @Override
            public void create() {
                if (!player.getInventory().containsItem(Easter2022.EGGSTERMINATOR) && !player.getBank().containsItem(Easter2022.EGGSTERMINATOR, 1) && player.getEquipment().getWeaponId() != Easter2022.EGGSTERMINATOR &&
                        !player.getInventory().containsItem(Easter2022.PERMANENT_EGGSTERMINATOR) && !player.getBank().containsItem(Easter2022.PERMANENT_EGGSTERMINATOR, 1) && player.getEquipment().getWeaponId() != Easter2022.PERMANENT_EGGSTERMINATOR) {
                    option("Start the hunt.", (player.getEquipment().getWeaponId() != -1 || player.getEquipment().getShieldId() != -1) ?
                            new Dialogue()
                                    .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Your hands must be free. *bwaaak*") :
                            new Dialogue()
                                    .addItem(Easter2022.EGGSTERMINATOR, "You're handed (or 'winged') the Eggsterminator.")
                                    .addNext(() -> { 
                                    	player.getEquipment().setSlot(Equipment.WEAPON, new Item(Easter2022.EGGSTERMINATOR));
                                    	player.getAppearance().generateAppearanceData();
                                    })
                                    .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Hunt down the five eggs scattered across Runescape. Blow them open with the Eggsterminator and splatter the chick that comes from within.")
                                    .addSimple("These eggs can be found around Runescape. You can search for them yourself or with your friends.") //Information can also be found on the Runescape official forums.
                                    .addSimple("Finding all 5 eggs in a single hunt will unlock additional rewards.")); //Some eggs only appear in members parts of the world, so only members gain these additional benefits.
                } else {
                    option("How do I hunt the eggs?", new Dialogue()
                            .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "The Easter Bunny has hidden magical eggs across Runescape. Unfortunate, the Easter Bunny is temporarily...indisposed...and so is unable to perform his duties as huntmaster. " +
                                    "I am now in charge of the hunt.")
                            .addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "No, you aren't. I'm the huntmaster!")
                            .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Quiet, you.")
                            .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Every two hours, a new hunt will begin. Hunt down the five eggs, smash them open using the Eggsterminator and then shoot at the chick that emerges with the Eggsterminator. " +
                                    "This will turn the chick into a tasty treat."));
                    
                    option("Can I have a hint?", (EggHunt.active() ? (player.getVars().getVarBit(10954) == 3 ?
                            new Dialogue()
                                    .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "You've already found the egg I have information on.") :
                            new Dialogue()
                                    .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Well, the Chocatrice and I have a gentlefowl's agreement not to tell our seekers the locations of any of the eggs. But between you and me...")
                                    .addOption("Listen to the hint?", "Yes", "No")
                                    .addSimple("The chicken lowers its voice.")
                                    .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "One egg can be found " + EggHunt.getHint())) :
                           new Dialogue()
                                    .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "The hunt has ended. " + EggHunt.getTimeString())));
                }
                option("Who is winning the egg hunt?", new Dialogue()
                		.addSimple("Evil Chicken: " + EggHunt.getEvilChickenScore() + "<br><br> vs <br><br>" + "Chocatrice: " + EggHunt.getChocatriceScore()));
                option("Who are you?", new Dialogue()
                        .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "But surely everybody has heard of the Evil Chicken.")
                        .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Some say I came from the Abyss. Others say I was just a normal chicken, who grew angry at human domination over my species. But do you want to know where I really came from?")
                        .addSimple("The Evil Chicken lowers its voice. You lean in.")
                        .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "*Bwaaak* *Bwaaak* *Bwaaak*")
                        .addSimple("You get the impression that this chicken is quite mad."));
                option("Talk about rewards.", new Dialogue().addOptions(new Options() {
                    @Override
                    public void create() {
                        option("Mask rewards", new Dialogue()
                                .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Bring me three drumsticks and I'll reward you with an egg on face mask."));
                        option("Eggsterminator (permanent)", new Dialogue()
                                .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "The Eggsterminator will melt after the Easter period as it's made of chocolate. If you find all five eggs in a single hunt, and do this three times " +
                                        "I'll see if I can go about enchanting it."));
                        option("Treats and XP lamps", new Dialogue()
                                .addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Every time you turn a chick into an evil drumstick you can either eat it, or keep it. If you gather 3 delicious evil drumsticks for me I'll trade them for an egg on face mask.")
                                .addItem(Easter2022.XP_LAMP, "Players will also gain an experience lamp if they find all five eggs in a single hunt."));
                        option("Talk about something else.", new Dialogue()
                                .addGotoStage("huntOps", EvilChickenD.this));
                    }
                }));
            }
        });
        create();
    }

    public static NPCClickHandler handleEvilChicken = new NPCClickHandler(new Object[] { Easter2022.EVIL_CHICKEN, Easter2022.EVIL_CHICKEN_MEDIUM, Easter2022.EVIL_CHICKEN_LARGE }, e -> {
    	 if (!Easter2022.ENABLED)
             return;
         if (e.getOption().equals("Talk to")) {
             e.getPlayer().startConversation(new EvilChickenD(e.getPlayer()));
         }
    });

    public static ItemOnNPCHandler handleItemOnEvilChicken= new ItemOnNPCHandler(new Object[] { Easter2022.EVIL_CHICKEN, Easter2022.EVIL_CHICKEN_MEDIUM, Easter2022.EVIL_CHICKEN_LARGE }, e -> {
    	if (!Easter2022.ENABLED)
            return;
        if (e.getItem().getId() == Easter2022.CHOCOLATE_EGG_ON_FACE_MASK)
            e.getPlayer().startConversation(new Dialogue().addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "That mask is unflattering and makes you look fat."));
        if (e.getItem().getId() == Easter2022.EGG_ON_FACE_MASK)
            e.getPlayer().startConversation(new Dialogue().addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "That mask is *entirely* homemade. *bwaaak*"));
        if (e.getItem().getId() == Easter2022.CHOCOTREAT)
            e.getPlayer().startConversation(new Dialogue().addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Eurgh - disgusting! Do you know that chocolate is my one weakness?"));
        if (e.getItem().getId() == Easter2022.EVIL_DRUMSTICK)
            e.getPlayer().startConversation(new Dialogue().addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Tasty."));
    });
}