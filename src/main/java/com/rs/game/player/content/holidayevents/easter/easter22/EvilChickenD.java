package com.rs.game.player.content.holidayevents.easter.easter22;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;

public class EvilChickenD extends Conversation {

    private static final int EVIL_CHICKEN = 15262;
    private static final int CHOCATRICE = 15259;
    private static final int EGGSTERMINATOR = 24145;
    private static final int XP_LAMP = 123;

    public EvilChickenD(Player player) {
        super(player);

        Options startHuntOps = new Options() {
            @Override
            public void create() {
                EvilChickenD.this.create("startHuntOps");
                option("Start the hunt.", () -> {
                    if (!player.getInventory().containsItem(EGGSTERMINATOR) && !player.getBank().containsItem(EGGSTERMINATOR, 1) && player.getEquipment().getWeaponId() != EGGSTERMINATOR) {
                        addItem(EGGSTERMINATOR, "You're handed (or 'winged') the Eggsterminator.");
                        addNext(() -> {
                            player.getInventory().addItem(EGGSTERMINATOR, 1);
                        });
                    }
                    addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Hunt down the five eggs scattered across Runescape. Blow them open with the Eggsterminator and splatter the chick that comes from within.");
                    addSimple("These eggs can be found around Runescape. You can search for them yourself or with your friends."); //Information can also be found on the Runescape official forums.
                    addSimple("Finding all 5 eggs in a single hunt will unlock additional rewards."); //Some eggs only appear in members parts of the world, so only members gain these additional benefits.
                });
                option("Who are you?");
                option("Talk about rewards.");
                option("Who's winning the current egg hunt?");
//                                        if (player has joined hunt) //TODO - TRACK WHO HAS JOINED A TEAM FOR THE HUNT
//                                            option("Can I have a hint?", () -> {
//                                                addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Well, the Chocatrice and I have a gentlefowl's agreement not to tell our seekers the locations of any of the eggs. But between you and me...");
//                                                addOption("Listen to the hint?", "Yes", "No");
//                                                addSimple("The bird lowers its voice.");
//                                                addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "One egg can be found " /*+ HINT*/); //TODO - ADD HINT - 1 PER HUNT
//                                            });
            }
        };

        Options takingSides = new Options() {
            @Override
            public void create() {
                EvilChickenD.this.create("takingSides");
                option("Chocolate does sound better.", () -> {
                    addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Traitor! *bwaaak*!");
                    addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "That stupid bird thinks he can turn more chicks into chocolate than I can into drumsticks. If it's war he wants, it's war he'll get.");
                    addOptions(startHuntOps);
                });
                option("I'm more of a friend chicken kind of " + (player.getAppearance().isMale() ? "guy." : "gal."));
            }
        };

        Options eventDetailsOps = new Options() {
            @Override
            public void create() {
                EvilChickenD.this.create("eventDetailsOps");
                option("Certainly!", () -> {
                    addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "It's crucial that you don't listen to that Chocatrice. He thinks we should turn those chicks into chocolate. Disgusting.... Deep-fried chicken is far more tasty.");
                    addOptions(takingSides);
                });
                option("Why are you deep-frying chickens?");
            }
        };

        //TODO - Clear temp attributes "talkedWithEvilChicken" && "talkedWithChocatrice" for online players if a new hunt starts.
        create("newHunt");
        if (player.getTempAttribs().getB("talkedWithChocatrice") == true)
            addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Traitor! Don't listen to that chocolate bar on legs!"); //TODO find this dialogue
        else {
            if (player.getTempAttribs().getB("joinedTheHunt") == false) {
                if (player.getTempAttribs().getB("talkedWithEvilChicken") == false) {
                    addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "I'm glad you spoke with me first and not that chocolate bar on legs.");
                    player.getTempAttribs().setB("startedWithEvilChicken", true);
                } else {
                    addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Report in, soldier. *bwaaak*");
                    addNext(getStage("startHuntOps"));
                }
            }
        }

        create("initialInfo");
        addPlayer(HeadE.CALM_TALK, "What are you two doing here?");
        addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "I'm hosting this year's *bwaaak* Easter hunt. Time to take things in-wing. I'm sick of the incessant, poorly timed *bwaaak* predictable egg jokes you get every single Easter.");
        addPlayer(HeadE.CALM_TALK, "They must be getting old.");
        addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Egg-xactly. *bwaaak* *bwaaak* Now, you may have heard that the Easter Bunny is recovering from a terrible accident. " +
                "He mistakenly fell, from quite a height, into a pit of lava. *bwaaak* What a terribly unfortunate accident.");
        addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Luckily, his accident occurred after placing the eggs for this year's hunt. Five delicious, giant, magical eggs, scattered around Runescape.");
        addPlayer(HeadE.CALM_TALK, "So you want me to find these giant eggs?");
        addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Yes, and blow them open with this.");
        addItem(EGGSTERMINATOR, "The Evil Chicken shows you the Eggsterminator.");
        addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Once the egg is obliterated, a cute little chick will escape...");
        addPlayer(HeadE.CHEERFUL, "Awww...");
        addNPC(EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Deep-fry it with the cannon! *bwaaak* *bwaaak* *bwaaak*");
        addOptions(eventDetailsOps);
    }
}