package com.rs.game.content.skills.hunter.puropuro;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class ElnockInquisitor extends Conversation {

    public static int ELNOCK_INQUISITOR = 6070;

    public ElnockInquisitor(Player player) {
        super(player);
        if (player.getTempAttribs().getB("puropuro_introduction") == false) {
            addPlayer(HeadE.CONFUSED, "What's a gnome doing here?");
            addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "I'm an investigator. I'm watching the implings.");
            addPlayer(HeadE.CALM, "Why would you want to do that?");
            addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "My client has asked me to find out where certain missing items have been going.");
            addPlayer(HeadE.CALM, "Who is this client?");
            addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "I'm not at liberty to discuss that. Investigator-client confidentiality don't you know. ");
            addOptions("Choose an option:", ops -> {
                ops.add("Where is this place?")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "The fairies call it Puro-Puro. It seems to be the home plane of the implings.")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "I don't think these creatures have a name for it. As you can see there isn't a lot else here other than wheat. ");
                ops.add("How did you get here?")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "The same way you did, I suspect. Through a portal in a wheat field. I followed one back.")
                    .addPlayer(HeadE.CALM, "I haven't noticed them do that.")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "That's why I'm the investigator and you're the adventurer.");
                ops.add("So, what are these implings?")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "That's a very interesting question. My best guess is that they are relatives to imps, which is why there are imps here as well.")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Implings do appear to like collecting objects and, as my clients have noted, have no concept of ownership. However, I do not sense any malicious intent.")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "It is my observation that they collect things that other creatures want, rather than they want them themselves. It seems to provide them with sustenance.")
                    .addPlayer(HeadE.CALM, "So they feed off our desire for things?")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Possibly. Either way, it seems that they almost exclusively collect things that people want, except their younglings who I infer haven't learnt the best things to collect yet.")
                    .addPlayer(HeadE.CALM, "So the more experienced implings have the more desirable items?")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "That is my observation. Yes.");
                ops.add("Can I catch these implings, then?")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Indeed you may. In fact I encourage it. You will, however, require some equipment.")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Firstly you will need a butterfly net in which to catch them and at least one special impling jar to store an impling.")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "You will also require some experience as a Hunter since these creatures are elusive. The more immature implings require less experience, but some of the rarer implings are extraordinarily hard to find and catch.")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Once you have caught one, you may break the jar open and obtain the object the impling is carrying. Alternatively, you may exchange certain combinations of jars with me. I will return the jars to my clients. In")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "exchange I will be able to provide you with some equipment that may help you hunt butterflies more effectively.")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Also, beware. Those imps walking around the maze do not like the fact that their kindred spirits are being captured and will attempt to steal any full jars you have on you, setting the implings free.")
                    .addPlayer(HeadE.CALM, "I've heard I may find dragon equipment in Puro-Puro.")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Have you indeed? Well, that may well be true, though bear in mind that implings are quite small so they are unlikely to be lugging a sizeable shield around with them. However, since it seems that dragon items are")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "very desirable to humans then it certainly is possible that the most expert implings may try to obtain such equipment.")
                    .addPlayer(HeadE.CALM, "So, it's true then? Cool!")
                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "I should warn you, though, if the impling is strong enough to collect dragon equipment, then you will have to be very skilled at hunting implings in order to catch them.")
                        .addNext(() -> { player.getTempAttribs().setB("puropuro_introduction", true); });
            });
        } else {
            addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Ah, good day, it's you again. What can I do for you?");
            addOptions("Choose an option:", ops -> {
                ops.add("Can you remind me how to catch implings again?")
                        .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Certainly.")
                        .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "In Puro-Puro, you will need a butterfly net to catch an impling and at least one special impling jar to store them in. ")
                        .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "You will also require some experience as a Hunter, since these creatures are elusive. The more immature implings require less experience, but some of the rarer implings are extraordinarily hard to find and catch.")
                        .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Some of these implings have escaped to Gielinor, but have been exhausted by the journey. Assuming you are experienced enough, you can catch them without needing any equipment.")
                        .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Once you have caught an impling in a jar, you can break the jar open to obtain the impling's object, or you can exchange certain combinations of jars with me. I will then return the jars to my clients. In exchange, I will provide")
                        .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "equipment that helps you hunt implings and butterflies more effectively.")
                        .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "But beware: imps in the maze do not like their kind being captured, and will attempt to steal any full jars you have on you, setting the implings free.")
                        .addOptions("Choose an option:", ops2 -> {
                            ops2.add("Tell me more about these jars.")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "You cannot use an ordinary butterfly jar as a container, as the implings escape from them with ease. I have, however, done some investigating and come up with a solution: if a butterfly jar is coated with a thin layer of a")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "substance that is noxious to them, implings become incapable of escape.")
                                    .addPlayer(HeadE.CALM, " What substance is that, then?")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "I have tried a few experiments with the help of a friend back home, and it turns out that a combination of anchovy oil and flowers - marigolds, rosemary or nasturtiums - will work.")
                                    .addPlayer(HeadE.CALM, " How do you make anchovy oil then?")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "I'd grind up some cooked anchovies and pass them through a sieve.")
                                    .addPlayer(HeadE.CALM, " Where do I make these jars?")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Well, I believe there is a chemist in Rimmington that has a small still that you could use.")
                                    .addPlayer(HeadE.CALM, " Is there anywhere I can buy these jars?")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Well, I may be able to sell you a few, although I don't have an infinite supply. I can also offer several empty jars in exchange for any full ones you have.");
                            ops2.add("Tell me more about these thieving imps.")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Imps and implings appear to be related, and the imps here are quite protective of their smaller relations. If you allow them to get too close then they will attempt to steal jarred implings from your pack, if you have them.")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "They will then set them free, dropping your jar on the floor. So, if you're quick, you may be able to catch it again. ")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "I have some impling deterrent which I may trade if you prove that you can catch implings well.");
                            ops2.add("So, what's this equipment you can provide, then?")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "I have been given permission by my clients to give three pieces of equipment to able hunters. ")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Firstly, I have some imp deterrent. If you bring me three baby implings, two young implings and one gourmet impling already jarred, I will give you a vial. Imps don't like the smell, so they will be less likely to steal jarred implings")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "from you.")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Secondly, I have magical butterfly nets. If you bring me three gourmet implings, two earth implings and one essence impling I will give you a new net. It will help you catch both implings and butterflies. ")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Lastly, I have magical jar generators. If you bring me three essence implings, two eclectic implings and one nature impling I will give you a jar generator. This object will create either butterfly or impling jars (up to a limited")
                                    .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "number of charges) without having to carry a pack full of them. ");
                            if (player.getBool("puropuro_equipment_claimed"))
                                ops2.add("Do you have some spare equipment I can use?")
                                        .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "I have already given you some equipment.")
                                        .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "If you are ready to start hunting implings, then enter the main part of the maze. Just push through the wheat that surrounds the centre of the maze and get catching!");
                            else
                                ops2.add("Do you have some spare equipment I can use?")
                                        .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Yes! I have some spare equipment for you.")
                                        .addNext(() -> {
                                            player.getInventory().addItemDrop(new Item(10010, 1));
                                            player.getInventory().addItemDrop(new Item(11260, 7));
                                            player.save("puropuro_equipment_claimed", true);
                                        })
                                        .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "There you go. You have everything you need now.")
                                        .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "If you are ready to start hunting implings, then enter the main part of the maze. Just push through the wheat that surrounds the centre of the maze and get catching!");
                        });
                ops.add("Can I trade some jarred implings, please?").addNext(() -> { openPuroPuroInterface(player); });
                ops.add("Can I buy a few impling jars?").addNext(() -> { ShopsHandler.openShop(player, "elnocks_backup_supply"); });
                if (player.getBool("puropuro_equipment_claimed"))
                    ops.add("Do you have some spare equipment I can use?")
                            .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "I have already given you some equipment.")
                            .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "If you are ready to start hunting implings, then enter the main part of the maze. Just push through the wheat that surrounds the centre of the maze and get catching!");
                else
                    ops.add("Do you have some spare equipment I can use?")
                            .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "Yes! I have some spare equipment for you.")
                            .addNext(() -> {
                                player.getInventory().addItemDrop(new Item(10010, 1));
                                player.getInventory().addItemDrop(new Item(11260, 7));
                                player.save("puropuro_equipment_claimed", true);
                            })
                            .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "There you go. You have everything you need now.")
                            .addNPC(ELNOCK_INQUISITOR, HeadE.CALM, "If you are ready to start hunting implings, then enter the main part of the maze. Just push through the wheat that surrounds the centre of the maze and get catching!");
            });
        }
    }

    public enum ElnocksShopSelections {
        IMP_REPELLENT(20, new Item(11262, 1), new Item[] { new Item(11238, 3), new Item(11240, 2), new Item(11242, 1) }),
        MAGIC_BUTTERFLY_NET(22, new Item(11259, 1), new Item[] { new Item(11242, 3), new Item(11244, 2), new Item(11246, 1) }),
        JAR_GENERATOR(24, new Item(11258, 1), new Item[] { new Item(11246, 3), new Item(11248, 2), new Item(11250, 1) }),
        IMPLING_JAR(26, new Item(11260, 3), null);

        private final int componentId;
        private final Item purchased;
        private final Item[] currency;

        ElnocksShopSelections(int componentId, Item purchased, Item[] currency) {
            this.componentId = componentId;
            this.purchased = purchased;
            this.currency = currency;
        }

        public int getComponentId() {
            return componentId;
        }

        public Item getPurchased() {
            return purchased;
        }

        public Item[] getCurrency() {
            return currency;
        }

        public static final Map<Integer, ElnocksShopSelections> ElnocksShop = new HashMap<>();
        static {
            for (ElnocksShopSelections selected : ElnocksShopSelections.values()) {
                ElnocksShop.put(selected.componentId, selected);
            }
        }

        public static ElnocksShopSelections forComponent(int componentId) {
            return ElnocksShop.get(componentId);
        }
    }

    public static NPCClickHandler handleElnock = new NPCClickHandler(new Object[] { ELNOCK_INQUISITOR }, e -> {
        switch(e.getOption()) {
            case "Talk-to" -> { e.getPlayer().startConversation(new ElnockInquisitor(e.getPlayer()));}
            case "Trade" -> { ShopsHandler.openShop(e.getPlayer(), "elnocks_backup_supply"); }
            case "Exchange" -> { openPuroPuroInterface(e.getPlayer()); }
            case "Quick-start" -> { e.getPlayer().sendMessage("If you are ready to start hunting implings, then enter the main part of the maze."); }
            default -> { e.getPlayer().sendMessage("Unhandled option."); }
        }
    });

    public static void openPuroPuroInterface(Player player) {
        player.getInterfaceManager().sendInterface(540);
        for (int component = 60; component < 64; component++)
            player.getPackets().setIFHidden(540, component, false);
        player.setCloseInterfacesEvent(() -> player.getTempAttribs().removeI("puropuro_selection"));
    }

    public static ButtonClickHandler handlePuroPuroShopButtons = new ButtonClickHandler(540, e -> {
        switch(e.getComponentId()) {
            case 69 -> confirmPuroPuroSelection(e.getPlayer());
            case 71 -> ShopsHandler.openShop(e.getPlayer(), "elnocks_backup_supply");
            default -> e.getPlayer().getTempAttribs().setI("puropuro_selection", e.getComponentId());
        }
    });

    public static void confirmPuroPuroSelection(Player player) {
        if (player.getTempAttribs().getI("puropuro_selection") == -1)
            return;

        ElnocksShopSelections selected = ElnocksShopSelections.forComponent(player.getTempAttribs().getI("puropuro_selection"));

        if (selected == null) {
            player.sendMessage("You must select an option to exchange.");
            return;
        }

        if (selected.currency == null) {
            int currency = ImpDefender.getLowestImplingJar(player);
            if (currency > -1) {
                player.getInventory().deleteItem(currency, 1);
                player.getInventory().addItem(selected.purchased);
                player.closeInterfaces();
                player.sendMessage("You exchange the required item for: 3 x " + selected.purchased.getName().toLowerCase() + "s.");
                return;
            }
        }

        if (selected.currency == null || !player.getInventory().containsItems(selected.currency)) {
            player.sendMessage("You don't have the required items.");
            return;
        }

        player.getInventory().removeItems(selected.currency);
        player.getInventory().addItem(selected.purchased);
        player.closeInterfaces();
        player.sendMessage("You exchange the required items for: " + selected.purchased.getName().toLowerCase() + ".");
    }
}