package com.rs.game.player.quests.handlers.demonslayer;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class EncantationOptionsD  extends Conversation {
    Player p;
    DelrithBoss boss;
    int chantCount;
    String[] chantOrder = new String[] {
        "Aber",
        "Gabindo",
        "Purchai",
        "Camerinthum",
        "Carlem"
    };

    public EncantationOptionsD(Player p, DelrithBoss boss) {
        super(p);
        this.p = p;
        this.boss = boss;
        chantCount = 0;
        addPlayer(HeadE.SKEPTICAL_THINKING, "Now what was that incantation again?");
        addNext(()-> {p.startConversation(new EncantationOptionsD(p, boss, chantCount, 0).getStart());});
    }

    public EncantationOptionsD(Player p, DelrithBoss boss, int chantCount, int convoID) {
        super(p);
        this.p = p;
        this.boss = boss;
        this.chantCount = chantCount;
        switch(convoID) {
            case 0:
                chant();
                break;
            default:
                break;
        }


    }

    private void chant() {
        addOptions("Choose an option:", new Options() {
            @Override
            public void create() {
                option("Carlem...", new Dialogue()
                .addPlayer(HeadE.FRUSTRATED, "Carlem...", () -> {
                    p.forceTalk("Carlem...");
                    if(chantOrder[chantCount].contains("Carlem"))
                        chantCount++;
                    else {
                        chantCount = 0;
                        boss.forceTalk("Wrong! Ha ha!");
                    }
                })
                .addNext(()->{
                    if(chantCount == 5)
                        boss.die();
                    else
                        p.startConversation(new EncantationOptionsD(p, boss, chantCount, 0).getStart());
                }));
                option("Aber...", new Dialogue()
                .addPlayer(HeadE.FRUSTRATED, "Aber...", () -> {
                    p.forceTalk("Aber...");
                    if(chantOrder[chantCount].contains("Aber"))
                        chantCount++;
                    else {
                        chantCount = 0;
                        boss.forceTalk("Wrong! Ha ha!");
                        }
                })
                .addNext(()->{
                    if(chantCount == 5)
                        boss.die();
                    else
                        p.startConversation(new EncantationOptionsD(p, boss, chantCount, 0).getStart());
                }));
                option("Camerinthum...", new Dialogue()
                .addPlayer(HeadE.FRUSTRATED, "Camerinthum...", () -> {
                    p.forceTalk("Camerinthum...");
                    if(chantOrder[chantCount].contains("Camerinthum"))
                        chantCount++;
                    else {
                        chantCount = 0;
                        boss.forceTalk("Wrong! Ha ha!");
                    }
                })
                .addNext(()->{
                    if(chantCount == 5)
                        boss.die();
                    else
                        p.startConversation(new EncantationOptionsD(p, boss, chantCount, 0).getStart());
                }));
                option("Purchai...", new Dialogue()
                .addPlayer(HeadE.FRUSTRATED, "Purchai...", () -> {
                    p.forceTalk("Purchai...");
                    if(chantOrder[chantCount].contains("Purchai"))
                        chantCount++;
                    else {
                        chantCount = 0;
                        boss.forceTalk("Wrong! Ha ha!");
                    }
                })
                .addNext(()->{
                    if(chantCount == 5)
                        boss.die();
                    else
                        p.startConversation(new EncantationOptionsD(p, boss, chantCount, 0).getStart());
                }));
                option("Gabindo...", new Dialogue()
                .addPlayer(HeadE.FRUSTRATED, "Gabindo...", () -> {
                    p.forceTalk("Gabindo...");
                    if(chantOrder[chantCount].contains("Gabindo"))
                        chantCount++;
                    else {
                        chantCount = 0;
                        boss.forceTalk("Wrong! Ha ha!");
                    }
                })
                .addNext(()->{
                    if(chantCount == 5)
                        boss.die();
                    else
                        p.startConversation(new EncantationOptionsD(p, boss, chantCount, 0).getStart());
                }));
            }
        });
    }
}
