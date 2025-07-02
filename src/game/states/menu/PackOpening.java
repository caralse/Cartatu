package game.states.menu;

import com.syndria.Syndria;
import com.syndria.math.Vector;
import com.syndria.state.State;
import com.syndria.time.Timer;
import com.syndria.ui.Alignment;
import com.syndria.ui.FixedContainer;
import com.syndria.ui.Spacing;
import game.Palette;
import game.entities.ShallowCard;
import game.entities.User;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class PackOpening extends State {

    private Packet packet;
    private Menu menu;
    private ArrayList<ShallowCard> cards;

    private int index;

    private Timer t;

    private FixedContainer fc;

    public PackOpening(String label, Packet packet, Menu menu) {
        this.label = label;
        this.packet = packet;
        this.menu = menu;
        fc = new FixedContainer();
        fc.setBgColor(Palette.PEACH);
        fc.setBgVisible(true);
    }

    @Override
    public void enter() {
        menu.getAudioManager().stop();
        menu.getAudioManager().play("packOpening", true);
        t = new Timer(1);
        cards = packet.open();
        for (ShallowCard card : cards) {
            User.getInstance().addCard(card.getName());
        }
        index = 0;
        PackOpeningAnimation POA = new PackOpeningAnimation(cards.get(index), menu);
        POA.setOnComplete(() -> {
            menu.getStates().pop();
        });
        menu.getStates().push(POA);

        FixedContainer cardContainer = new FixedContainer(0, 0, 485, 108);

        fc.add(cardContainer, Alignment.centerAbsolute(), false);

        for (ShallowCard card : cards) {
            card.setMargin(new Spacing(10, 0));
            cardContainer.add(card, Alignment.inLine(), false);
        }

        t.atEndTime(() -> {
            cardContainer.clear();
            cardContainer.setPosition(Vector.sub(cardContainer.getPosition(), new Vector(38, 54)));
            cards.forEach((card -> {
                card.getImg().scale(2, 2);
                cardContainer.add(card, Alignment.inLine(), false);
            }));
        });

    }

    @Override
    public void draw(double alpha) {
        fc.draw(alpha);
        if (!t.isComplete()) {
            Syndria.gfx.drawRect(0, 0, 720, 480, 0xF9FFCCAA);
        }
    }

    @Override
    public void update(double dt) {
        index++;
        if (index < cards.size()) {
            PackOpeningAnimation POA = new PackOpeningAnimation(cards.get(index), menu);
            POA.setOnComplete(() -> {
                menu.getStates().pop();
            });
            menu.getStates().push(POA);
        } else {
            t.wait(dt);
        }
        if ((Syndria.input.KeyPressed(KeyEvent.VK_SPACE) || Syndria.input.MousePressed(1)) && t.isComplete()) {
            menu.getStates().switchTo("launcher");
            menu.getStates().remove("packOpening");
            cards.clear();
            cards = null;
            menu.getAudioManager().stop("packOpening");
            User.getInstance().saveData();
        }
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }
}
