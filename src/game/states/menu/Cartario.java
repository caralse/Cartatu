package game.states.menu;

import com.syndria.Syndria;
import com.syndria.gfx.Image;
import com.syndria.math.Vector;
import com.syndria.state.State;
import com.syndria.state.StateManager;
import com.syndria.ui.Alignment;
import com.syndria.ui.FixedContainer;
import com.syndria.ui.Spacing;
import com.syndria.ui.UIComponent;
import game.entities.*;

import java.awt.event.KeyEvent;

public class Cartario extends State {

    private Menu menu;
    private FixedContainer baseContainer;
    private FixedContainer dexContainer;
    private FixedContainer cardInfoContainer;

    private Button goBack;

    private ShallowCard selectedCard;

    private Image selectedCardImage;
    private Vector selectedCardPosition;

    private int i;
    private int bgColor;

    public Cartario (String label, Menu menu) {
        this.label = label;
        this.menu = menu;

        bgColor = 0xFF83769C;

        selectedCardImage = new Image("/gameResources/gfx/menu/selectedCard.png");

        baseContainer = new FixedContainer();
        baseContainer.setBgColor(bgColor);
        baseContainer.setBgVisible(true);

        dexContainer = new FixedContainer(100, 20, 325, 440);
        dexContainer.setMargin(new Spacing(10, 20));
        dexContainer.setBgVisible(true);
        dexContainer.setBgColor(0x60FFF1E8);
        dexContainer.setLineBreak(4);

        goBack = new Button("INDIETRO", 12);
        goBack.setMargin(new Spacing(10, 200));
        goBack.setSize(120, 32);
        goBack.setPosition(new Vector(50, 50));
        goBack.onClick(() -> {
            menu.getStates().setCurrentState("launcher");
            menu.getAudioManager().play("clickButton");
        });

        cardInfoContainer = new FixedContainer(400, 20, 200, 440);
        cardInfoContainer.setMargin(new Spacing(25, 20));

        baseContainer.add(goBack, Alignment.lineBreakLeft(), false);
        baseContainer.add(dexContainer, Alignment.lineBreakLeft(), false);
        baseContainer.add(cardInfoContainer, Alignment.lineBreakLeft(), false);
    }

    @Override
    public void enter() {
        fillDex();
    }

    @Override
    public void draw(double alpha) {
        baseContainer.draw(alpha);
        goBack.draw(alpha);
        selectedCardImage.draw(selectedCardPosition);
        drawBars();
    }

    @Override
    public void update(double dt) {
        selectCard();
        baseContainer.update(dt);
        goBack.update(dt);
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }

    private void fillDex() {
        dexContainer.clear();
        dexContainer.setContentPlacement(dexContainer.getPosition());
        i = 0;
        int K = 0;
        for (ShallowCard card : CardDex.getCards()) {
            card.setMargin(new Spacing(5));
            if (!User.getInstance().getObtainedCards().containsKey(card.getName())) {
                card.setOwned(false);
                card.setDraw(() -> {
                    Syndria.gfx.drawimage(card.getImg(), card.getPosition(), 0xFF999999);
                });
            } else {
                card.setOwned(true);
                card.setOwnedCopies((Integer) User.getInstance().getObtainedCards().get(card.getName()));
                int finalK = K;
                card.onClick(() -> {
                    i = finalK;
                });
                card.setDraw(() -> {
                    Syndria.gfx.drawimage(card.getImg(), card.getPosition(), -1);
                });
            }
            K++;
            dexContainer.add(card, Alignment.lineBreakLeft(), false);
        }
        selectedCardPosition = dexContainer.getChildren().get(i).getPosition();
    }

    private void selectCard() {
        cardInfoContainer.clear();
        selectedCard = (ShallowCard)dexContainer.getChildren().get(i);
        if (Syndria.input.KeyPressed(KeyEvent.VK_DOWN) || Syndria.input.KeyPressed(KeyEvent.VK_RIGHT)) {
            i = Math.min(i + 1, dexContainer.getChildren().size()-1);
        } else if (Syndria.input.KeyPressed(KeyEvent.VK_UP) || Syndria.input.KeyPressed(KeyEvent.VK_LEFT)) {
            i = Math.max(i - 1, 0);
        } else if (Syndria.input.KeyPressed(KeyEvent.VK_SPACE)) {
            if (selectedCard.isOwned()){
                menu.getStates().push(new PushBiggerCard(selectedCard.getPath(), this));
            }
        }
        cardInfoContainer.add(selectedCard.getInfo(), Alignment.centerAbsolute(), true);
        selectedCardPosition = dexContainer.getChildren().get(i).getPosition();
        scrollCards();
    }

    private void scrollCards() {
        if (selectedCardPosition.getY() < dexContainer.getPosition().getY()) {
            scrollDown();
        } else if (selectedCardPosition.getY() + 113 > dexContainer.getPosition().getY() + dexContainer.getSize().getY()) {
            scrollUp();
        }
    }

    private void scrollDown() {
        for (UIComponent card : dexContainer.getChildren()) {
            card.setPosition(Vector.sum(card.getPosition(), new Vector(0, card.getSize().getY() + card.getMargin().getTop())));
        }
    }

    private void scrollUp() {
        for (UIComponent card : dexContainer.getChildren()) {
            card.setPosition(Vector.sub(card.getPosition(), new Vector(0, card.getSize().getY() + card.getMargin().getTop())));
        }
    }

    private void drawBars() {
        Syndria.gfx.drawRect(0, 0, 720, 20, bgColor);
        Syndria.gfx.drawRect(0, 460, 720, 20, bgColor);
    }

    public FixedContainer getDexContainer() {
        return dexContainer;
    }

    public StateManager getStates() {
        return menu.getStates();
    }
}
