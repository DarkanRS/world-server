package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Donie extends Conversation {

    //Identify NPC by ID
    private static final int npcId = 2238;

    public static NPCClickHandler Donie = new NPCClickHandler(new Object[]{npcId}, e -> {
    	 switch (e.getOption()) {
         
         case "Talk-to" -> e.getPlayer().startConversation(new Donie(e.getPlayer()));
    	 }
    });


    public Donie(Player player) {
        super(player);
        addOptions(new Options() {
            @Override
            public void create() {
				//Give player options
                addOptions(new Options() {
                    @Override
                    public void create() {
						//Simple Reply
                        option("Where am I?", new Dialogue()
                                .addNPC(npcId, HeadE.CALM_TALK, "This is the town of Lumbridge my friend."));

						//Conversation
						option("How are you today?", new Dialogue()
                                .addNPC(npcId, HeadE.CALM_TALK, "Aye, not too bad thank you. Lovely weather in RuneScape this fine day.")
                                .addPlayer(HeadE.CALM_TALK, "Weather?")
                                .addNPC(npcId, HeadE.CHUCKLE, "Yes weather, you know.")
								.addNPC(npcId, HeadE.CALM_TALK, "The state or condition of the atmosphere at a time and place, with respect to variables such as temperature, moisture, wind velocity, and barometric pressure.")
                                .addPlayer(HeadE.ROLL_EYES, "...")
                                .addNPC(npcId, HeadE.LAUGH, "Not just a pretty face eh? Ha ha ha.")
                        );

                        //Nested player options with responses
						option("Are there any quests I can do here?", new Dialogue()
								//NPC to respond first
								.addNPC(npcId, HeadE.CALM_TALK, "What kind of quest are you looking for?")
								//Show next set of options
                                .addOptions(new Options() {
                                    @Override
                                    public void create() {
                                        option("I fancy a bit of a fight, anything dangerous?", new Dialogue()
												.addNPC(npcId, HeadE.SKEPTICAL, "Hmm.. dangerous you say? What sort of creatures are you looking to fight?")
												.addOptions(new Options() {
                                                    @Override
                                                    public void create() {
                                                        option("Big scary demons!", new Dialogue()
                                                                .addNPC(npcId, HeadE.AMAZED_MILD, "You are a brave soul indeed.")
																.addNPC(npcId, HeadE.CALM_TALK, "Now that you mention it, I heard a rumour about a Saradominist in the Varrock church who is rambling about some kind of greater evil..")
																.addNPC(npcId, HeadE.CALM_TALK, "sounds demon-like if you ask me...")
																.addNPC(npcId, HeadE.CALM_TALK, "Perhaps you should check it out if you are as brave as you say?")
                                                        );

                                                        option("Vampyres!", new Dialogue()
                                                                .addNPC(npcId, HeadE.SKEPTICAL, "Ha ha. I personally don't believe in such things.")
																.addNPC(npcId, HeadE.CALM_TALK, "However, there is a man in Draynor Village who has been scaring the village folk with stories of vampyres.")
																.addNPC(npcId, HeadE.CALM_TALK, "He's named Morgan and can be found in one of the village houses. Perhaps you could see what the matter is?")
                                                        );

                                                        option("Small.. something small would be good.", new Dialogue()
                                                                .addNPC(npcId, HeadE.LAUGH, "Small? Small isn't really that dangerous though is it?")
                                                                .addPlayer(HeadE.FRUSTRATED, "Yes it can be! There could be anything from an evil chicken to a poisonous spider. They attack in numbers you know!")
                                                                .addNPC(npcId, HeadE.CALM_TALK, "Yes ok, point taken. Speaking of small monsters,")
																.addNPC(npcId, HeadE.CALM_TALK,	"I hear old Wizard Mizgog in the wizards' tower has just had all his beads taken by a gang of mischievous imps.")
																.addNPC(npcId, HeadE.CALM_TALK,"Sounds like it could be a quest for you?")
                                                        );

                                                        option("That's all for now.");
                                                    }
                                                }));

                                        option("Something easy please, I'm new here.", new Dialogue()
												.addNPC(npcId, HeadE.LAUGH, "I can tell you about plenty of small easy tasks.")
												.addNPC(npcId, HeadE.CALM_TALK, "The Lumbridge cook has been having problems and the Duke is confused over some strange rocks.")
												.addOptions(new Options() {
                                                    @Override
                                                    public void create() {
                                                        option("The Lumbridge cook.", new Dialogue()
                                                                .addNPC(npcId, HeadE.LOSING_IT_LAUGHING, "It's funny really, the cook would forget his head if it wasn't screwed on.")
																.addNPC(npcId, HeadE.CALM_TALK,"This time he forgot to get ingredients for the Duke's birthday cake.")
																.addNPC(npcId, HeadE.CALM_TALK, "Perhaps you could help him? You will probably find him in the Lumbridge Castle kitchen.")
                                                        );

                                                        option("The Duke's strange stones.", new Dialogue()
                                                                .addNPC(npcId, HeadE.CALM_TALK, "Well the Duke of Lumbridge has found a strange stone that no one seems to understand.")
																.addNPC(npcId, HeadE.CALM_TALK,"Perhaps you could help him? You can probably find him upstairs in Lumbridge Castle.")
                                                        );

                                                        option("That's all for now.");
                                                    }
                                                }));

										option("I'm a thinker rather than a fighter, anything skill oriented?", new Dialogue()
												.addNPC(npcId, HeadE.CALM_TALK,"Skills play a big part when you want to progress in knowledge throughout RuneScape. " )
												.addNPC(npcId, HeadE.CALM_TALK,"I know of a few skill-related quests that can get you started.")
												.addNPC(npcId, HeadE.CALM_TALK, "You may be able to help out Fred the farmer who is in need of someones crafting expertise.")
												.addNPC(npcId, HeadE.CALM_TALK, "Or, there's always Doric the dwarf who needs an errand running for him?")
												.addOptions(new Options() {
                                            @Override
                                            public void create() {

                                                option("Farmer Fred.", new Dialogue()
                                                        .addNPC(npcId, HeadE.CALM_TALK, "You can find Fred next to the field of sheep in Lumbridge.")
														.addNPC(npcId, HeadE.CALM_TALK, "Perhaps you should go and speak with him. He always needs help shearing the sheep!")
                                                );

                                                option("Doric the dwarf.", new Dialogue()
                                                        .addNPC(npcId, HeadE.CALM_TALK, "Doric the dwarf is located north of Falador. ")
														.addNPC(npcId, HeadE.CALM_TALK,"He might be able to help you with smithing. You should speak to him. He may let you use his anvils.")
                                                );

                                                option("That's all for now.");
                                            }}
										));
										option("I want to do all kinds of things, do you know of anything like that", new Dialogue()
												.addNPC(npcId, HeadE.CALM_TALK,"of course I do. RuneScape is a huge place you know, now let me think... ")
												.addNPC(npcId, HeadE.CALM_TALK,"Hetty the witch in Rimmington might be able to offer help in the ways of magical abilities. ")
												.addNPC(npcId, HeadE.CALM_TALK, "Also, pirates are currently docked in Port Sarim. Where pirates are, treasure is never far away...")
												.addNPC(npcId, HeadE.CALM_TALK, "Or you could go and help out Ernest who got lost in Draynor Manor, spooky place that.")
												.addOptions(new Options() {
													@Override
													public void create() {

														option("Hetty the Witch.", new Dialogue()
																.addNPC(npcId, HeadE.CALM_TALK, "Hetty the witch can be found in Rimmington, south of Falador. " )
																.addNPC(npcId, HeadE.CALM_TALK, "She's currently working on some new potions. Perhaps you could give her a hand? ")
																.addNPC(npcId, HeadE.CALM_TALK, "She might be able to offer help with your magical abilities.")
														);

														option("Pirate's treasure.", new Dialogue()
																.addNPC(npcId, HeadE.CALM_TALK, "RedBeard Frank in Port Sarim's bar,")
																.addNPC(npcId, HeadE.CALM_TALK, "the Rusty Anchor might be able to tell you about the rumoured treasure that is buried somewhere in RuneScape.")
														);

														option("Ernest and Draynor Manor.", new Dialogue()
																.addNPC(npcId,HeadE.CALM_TALK, "The best place to start would be at the gate to Draynor Manor. ")
																.addNPC(npcId, HeadE.CALM_TALK, "There you will find Veronica who will be able to tell you more.")
																.addNPC(npcId, HeadE.CALM_TALK, "I suggest you tread carefully in that place;")
																.addNPC(npcId, HeadE.SCARED, "it's haunted.")
														);
														option("That's all for now.");
													}}
												));

                                        option("That's all for now.");
                                    }
                                })
						);
                    }
                });
            }
        });

		//TODO add mission checks and alternative responses
    }
}