package com.rs.game.player.content.holidayevents.easter.easter22;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;

public class ChocatriceD extends Conversation {
    
    public ChocatriceD(Player player) {
        super(player);

        Options rewardOps = new Options() {
            @Override
            public void create() {
                ChocatriceD.this.create("rewardOps");
                option("Mask rewards");
                option("Eggsterminator (permanent)");
                option("Treats and XP lamps", () -> {
                    addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Every time you turn a chocochick into a chocotreat you can either eat it, or keep it. If you gather 3 scrumptious chocotreats for me I'll trade them for a chocolate egg on face mask.");
                    addItem(Easter2022.XP_LAMP, "Players will also gain an experience lamp if they find all five eggs in a single hunt."); //Replaced Members with Players
                });
                option("Talk about something else.", () -> {
                    addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Every time you turn a chocochick into a chocotreat you can either eat it, or keep it. If you gather 3 scrumptious chocotreats for me I'll trade them for a chocolate egg on face mask.");
                    addItem(Easter2022.XP_LAMP, "Players will also gain an experience lamp if they find all five eggs in a single hunt."); //Replaced Members with Players
                });

            }
        };

        Options startHuntOps = new Options() {
            @Override
            public void create() {
                ChocatriceD.this.create("startHuntOps");
                option("Start the hunt.", () -> {
                    if (!player.getInventory().containsItem(Easter2022.EGGSTERMINATOR) && !player.getBank().containsItem(Easter2022.EGGSTERMINATOR, 1) && player.getEquipment().getWeaponId() != Easter2022.EGGSTERMINATOR) {
                        addItem(Easter2022.EGGSTERMINATOR, "You're handed (or 'winged') the Eggsterminator.");
                        addNext(() -> {
                            player.getInventory().addItem(Easter2022.EGGSTERMINATOR, 1);
                        });
                    }
                    addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Hunt down the five eggs scattered across Runescape. Blow them open with the Eggsterminator and splatter the chick that comes from within.");
                    addSimple("These eggs can be found around Runescape. You can search for them yourself or with your friends."); //Information can also be found on the Runescape official forums.
                    addSimple("Finding all 5 eggs in a single hunt will unlock additional rewards."); //Some eggs only appear in members parts of the world, so only members gain these additional benefits.
                });
                option("Who are you?");
                option("Talk about rewards.", addOptions(rewardOps));
                option("Who's winning the current egg hunt?");
//                                        if (player has joined hunt) //TODO - TRACK WHO HAS JOINED A TEAM FOR THE HUNT
//                                            option("Can I have a hint?", () -> {
//                                                addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "Nasty chicken and I agreed not to help the hunters find any of the eggs, but...");
//                                                addOption("Listen to the hint?", "Yes", "No");
//                                                addSimple("The bird lowers its voice.");
//                                                addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "One egg can be found " /*+ HINT*/); //TODO - ADD HINT - 1 PER HUNT
//                                            });
            }
        };

        Options takingSides = new Options() {
            @Override
            public void create() {
                ChocatriceD.this.create("takingSides");
                option("Chocolate does sound better.", () -> {
                    addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Yes... Delicious, scrummy, tasty chocolate.");
                    addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Evil Chicken declares war on poor Chocatrice, says he will turn more chicks into drumsticks than Chocatrice can turn into chocolate. If Evil Chicken wants war, Chocatrice gives it.");
                    addOptions(startHuntOps);
                });
                option("I'm more of a friend chicken kind of " + (player.getAppearance().isMale() ? "guy." : "gal."), () -> {
                    addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "No! Why deep-fry when you can smother in chocolate and marshmallow? Death by chocolate!");
                    addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Evil Chicken declares war on poor Chocatrice, says he will turn more chicks into drumsticks than Chocatrice can turn into chocolate. If Evil Chicken wants war, Chocatrice gives it.");
                });
            }
        };

        //TODO - Clear temp attributes "talkedWithEvilChicken" && "talkedWithChocatrice" for online players if a new hunt starts.
        create("newHunt");
        if (player.getTempAttribs().getB("talkedWithEvilChicken") == true) {
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "You already spoke to that Evil Chicken! What lies has he told you? Forget them - trust only Chocatrice!");
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Chocatrice is hosting this year's Easter hunt. No more annoying bunnies or pesky, nasty little squirrels.");
            addPlayer(HeadE.CALM_TALK, "Shouldn't the Easter bunny be hosting it?");
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Oh, you not heard? Easter Bunny had an accident. Poor Easter Bunny fell down White Wolf Mountain with a boulder tied to his feet. " +
                    "Poor, poor Easter Bunny. So sad. Chocatrice saw the whole thing. Most messy.");
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Luckily, tragic accident happened after Easter Bunny hid his five giant, magical eggs across Runescape");
            addPlayer(HeadE.CALM_TALK, "Let me guess. You want me to hunt down these eggs and blow them open with the Eggsterminator");
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Oh, what has nasty Evil Chicken told you?");
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Once egg smashed, little chick inside will try to escape. You shoot him down with molten chocolatey marshmallow and turn him into delicious, chocolatey treat.");
            addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Now it very important, you don't speak with Evil Chicken. He think we should turn tasty little chicks into drumsticks. Nasty, nasty Evil Chicken. They would taste much better as chocolate, don't you agree?");
            addOptions(takingSides);

        } else {
            if (player.getTempAttribs().getB("talkedWithChocatrice") == false) { //First time talking to chocatrice
                addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Good - you haven't spoken to that Evil Chicken yet. Nasty, lying chicken, that one. " +
                        "Think he's better than Chocatrice because he's made of flesh and feathers. You talk with Chocatrice; you trust Chocatrice.");
                addPlayer(HeadE.CALM_TALK, "What are you two doing here?");
                addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Chocatrice is hosting this year's Easter hunt. No more annoying bunnies or pesky, nasty little squirrels.");
                player.getTempAttribs().setB("startedWithChocatrice", true);
                addPlayer(HeadE.CALM_TALK, "Shouldn't the Easter bunny be hosting it?");
                addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Oh, you not heard? Easter Bunny had an accident. Poor Easter Bunny fell down White Wolf Mountain with a boulder tied to his feet. " +
                        "Poor, poor Easter Bunny. So sad. Chocatrice saw the whole thing. Most messy.");
                addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Luckily, tragic accident happened after Easter Bunny hid his five giant, magical eggs across Runescape");
                addPlayer(HeadE.CALM_TALK, "So you want me to find these giant eggs?");
                addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Find? Yes. They must be.... cracked... open.");
                addPlayer(HeadE.CALM_TALK, "With a giant teaspoon, right?");
                addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, player.getDisplayName() + ", you are funny. Yes, you are... Not teaspoon. With the Eggsterminator.");
                addItem(Easter2022.EGGSTERMINATOR, "The Chocatrice shows you the Eggsterminator.");
                addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Once egg smashed, little chick inside will try to escape. You shoot him down with molten chocolatey marshmallow and turn him into delicious, chocolatey treat.");
            } else {
                addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Hello again, " + player.getDisplayName());
                if (player.getInventory().containsItem(24148, 5)) {
                    addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Well done, soldier, you've found all the eggs this hunt. You're delightfully despicable.");
                    addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "The next hunt will be starting in " + /*timer +*/ " minutes.");
                    //TODO - offer to buy mask

                } else {
                    addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "The Easter Bunny has hidden magical eggs across Runescape. Unfortunate, the Easter Bunny is temporarily...indisposed...and so is unable to perform his duties as huntmaster. " +
                            "I am not in charge of the hunt.");
                    addNPC(Easter2022.EVIL_CHICKEN, HeadE.NO_EXPRESSION, "No, you aren't. I am!");
                    addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Quiet, you.");
                    addNPC(Easter2022.CHOCATRICE, HeadE.NO_EXPRESSION, "Every two hours, a new hunt will begin. Hunt down the five eggs, smash them open using the Eggsterminator and then shoot at the chick that emerges with the Eggsterminator. " +
                            "This will turn the chick into a tasty treat.");



                }
            }
        }
    }

}
