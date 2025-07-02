package game.states.menu;

import com.syndria.state.State;
import com.syndria.ui.*;
import game.entities.Button;
import game.entities.User;

import static game.Palette.*;

public class Shop extends State {

    private Menu menu;

    private FixedContainer screenContainer;

    private FixedContainer packetContainer;
    private FixedContainer buttonsContainer;

    private TextBox coins;

    public Shop (String label, Menu menu) {
        this.label = label;
        this.menu = menu;

        menu.getAudioManager().loadSound("packOpening", "audio/music/packOpening.wav");

        screenContainer = new FixedContainer();
        screenContainer.setBgColor(0xFF83769C);
        screenContainer.setBgVisible(true);

        packetContainer = new FixedContainer();
        buttonsContainer = new FixedContainer();

        packetContainer.setRelativeDimensions(.8, .55);
        packetContainer.setBgVisible(true);
        buttonsContainer.setRelativeDimensions(.8, .1);
        buttonsContainer.setBgVisible(true);
        buttonsContainer.setMargin(new Spacing(10));

        Button goBack = new Button("INDIETRO", 12);
        goBack.setSize(120, 32);
        goBack.onClick(() -> {
            menu.getStates().setCurrentState("launcher");
            menu.getAudioManager().play("clickButton");
        });

        coins = new TextBox(String.format("%d$", User.getInstance().getCoins()), 24, WHITE);
        coins.setMargin(new Spacing(0, 5, 0, 0));

        screenContainer.add(coins, Alignment.inLineVertical(), false);
        screenContainer.add(packetContainer, Alignment.inLineVertical(), true);
        screenContainer.add(buttonsContainer, Alignment.inLineVertical(), true);
        screenContainer.add(goBack, Alignment.inLineVertical(), false);

        //ADD PACKETS
        Picture bronzePacket = new Picture("/gameResources/gfx/menu/bronzePacket.png");
        bronzePacket.scale(2, 2);
        bronzePacket.setMargin(new Spacing(5));
        packetContainer.add(bronzePacket, Alignment.inLine(), false);

        Picture silverPacket = new Picture("/gameResources/gfx/menu/silverPacket.png");
        silverPacket.scale(2, 2);
        silverPacket.setMargin(new Spacing(5));
        packetContainer.add(silverPacket, Alignment.inLine(), false);

        Picture goldPacket = new Picture("/gameResources/gfx/menu/goldPacket.png");
        goldPacket.scale(2, 2);
        goldPacket.setMargin(new Spacing(5));
        packetContainer.add(goldPacket, Alignment.inLine(), false);

        //ADD BUTTONS
        Button bronzeButton = new Button("100$", 18);
        bronzeButton.getText().setColor(BRONZE);
        bronzeButton.setRelativeDimensions(.2, .9);
        bronzeButton.setMargin(new Spacing(38, 0));
        bronzeButton.onClick(() -> {
            if (User.getInstance().getCoins() >= 100) {
                menu.getAudioManager().play("clickButton");
                PackOpening PO = new PackOpening("packOpening", Packet.BRONZE(), menu);
                menu.getStates().add(PO);
                menu.getStates().switchTo("packOpening");
                User.getInstance().incrementCoins(-100);
            } else {
                menu.getAudioManager().play("impossible");
            }
        });

        Button silverButton = new Button("250$", 18);
        silverButton.getText().setColor(SILVER);
        silverButton.setRelativeDimensions(.2, .9);
        silverButton.setMargin(new Spacing(38, 0));
        silverButton.onClick(() -> {
            if (User.getInstance().getCoins() >= 250) {
                menu.getAudioManager().play("clickButton");
                PackOpening PO = new PackOpening("packOpening", Packet.SILVER(), menu);
                menu.getStates().add(PO);
                menu.getStates().switchTo("packOpening");
                User.getInstance().incrementCoins(-250);
            } else {
                menu.getAudioManager().play("impossible");
            }
        });

        Button goldButton = new Button("450$", 18);
        goldButton.getText().setColor(GOLD);
        goldButton.setRelativeDimensions(.2, .9);
        goldButton.setMargin(new Spacing(38, 0));
        goldButton.onClick(() -> {
            if (User.getInstance().getCoins() >= 450) {
                menu.getAudioManager().play("clickButton");
                PackOpening PO = new PackOpening("packOpening", Packet.GOLD(), menu);
                menu.getStates().add(PO);
                menu.getStates().switchTo("packOpening");
                User.getInstance().incrementCoins(-450);
            } else {
                menu.getAudioManager().play("impossible");
            }
        });

        buttonsContainer.add(bronzeButton, Alignment.inLine(), true);
        buttonsContainer.add(silverButton, Alignment.inLine(), true);
        buttonsContainer.add(goldButton, Alignment.inLine(), true);

    }

    @Override
    public void enter() {
        coins.setLabel(String.format("%d$", User.getInstance().getCoins()));
    }

    @Override
    public void draw(double alpha) {
        screenContainer.draw(alpha);
    }

    @Override
    public void update(double dt) {
        screenContainer.update(dt);
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }
}
