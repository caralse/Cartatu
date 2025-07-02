package game.states.menu;

import com.syndria.Syndria;
import com.syndria.math.Vector;
import com.syndria.state.State;
import com.syndria.ui.*;
import game.Palette;
import game.entities.Button;
import game.entities.CardDex;
import game.entities.ShallowCard;
import game.entities.User;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public class CreateDeck extends State {

    private final Menu menu;

    FixedContainer baseContainer;
    FixedContainer baseContainer2;
    FixedContainer ownedContainer;
    FixedContainer deckContainer;

    HashMap<String, Integer> ownedCopy;
    HashMap<String, Integer> deckCopy;

    private boolean updateContainers;

    private int DECKSIZE;
    private final TextBox cardsInDeck;

    public CreateDeck(String label, Menu menu) {
        this.label = label;
        this.menu = menu;
        baseContainer = new FixedContainer();
        baseContainer.setBgColor(Palette.LILLE);
        baseContainer.setBgVisible(true);

        DECKSIZE = 0;

        // SCRITTE //

        baseContainer2 = new FixedContainer();

        FixedContainer CARDSCONTAINER = new FixedContainer(0, 0, 200, 40);
        CARDSCONTAINER.setMargin(new Spacing(80, 0));
        FixedContainer DECKCONTAINER = new FixedContainer(0, 0, 200, 40);
        DECKCONTAINER.setMargin(new Spacing(80, 0));

        baseContainer2.add(CARDSCONTAINER, Alignment.inLine(), false);
        baseContainer2.add(DECKCONTAINER, Alignment.inLine(), false);

        TextBox CARDS = new TextBox("CARTE", 18, Palette.PEACH);
        CARDS.setMargin(new Spacing(10));

        TextBox DECK = new TextBox("MAZZO:", 18, Palette.PEACH);
        DECK.setMargin(new Spacing(0, 10));
        cardsInDeck = new TextBox(DECKSIZE, 18, Palette.LIGHT_GREEN);
        cardsInDeck.setMargin(new Spacing(0, 10));

        CARDSCONTAINER.setContentPlacement(CARDSCONTAINER.getPosition());
        CARDSCONTAINER.add(CARDS, Alignment.centerAlign(), false);
        DECKCONTAINER.setContentPlacement(DECKCONTAINER.getPosition());
        DECKCONTAINER.add(DECK, Alignment.straightAlign(), false);
        DECKCONTAINER.add(cardsInDeck, Alignment.straightAlign(), false);

        // CARTE //

        ownedContainer = new FixedContainer(0, 0, 200, 400);
        ownedContainer.setMargin(new Spacing(20, 40));
        ownedContainer.setBgVisible(true);
        deckContainer = new FixedContainer(0, 0, 200, 400);
        deckContainer.setMargin(new Spacing(10, 40));
        deckContainer.setBgVisible(true);

        FixedContainer leftButtonsContainer = new FixedContainer(0, 0, 80, 400);
        leftButtonsContainer.setMargin(new Spacing(10, 40));

        FixedContainer rightButtonsContainer = new FixedContainer(0, 0, 80, 400);
        rightButtonsContainer.setMargin(new Spacing(10, 40));

        baseContainer.add(leftButtonsContainer, Alignment.inLine(), false);
        baseContainer.add(ownedContainer, Alignment.inLine(), false);
        baseContainer.add(deckContainer, Alignment.inLine(), false);
        baseContainer.add(rightButtonsContainer, Alignment.inLine(), false);

        leftButtonsContainer.setContentPlacement(leftButtonsContainer.getPosition());

        Button goUp = new Button("SALI", 10);
        goUp.setSize(80, 30);
        goUp.setMargin(new Spacing(80, 0, 0, 0));
        goUp.onClick(() -> {
            scrollDown(ownedContainer);
        });

        Button goDown = new Button("SCENDI", 10);
        goDown.setSize(80, 30);
        goDown.setMargin(new Spacing(0, 30));
        goDown.onClick(() -> {
            scrollUp(ownedContainer);
        });

        Button goBack = new Button("INDIETRO", 10);
        goBack.setSize(80, 30);
        goBack.setMargin(new Spacing(0, 40));
        goBack.onClick(() -> {
            menu.getStates().switchTo("launcher");
            menu.getAudioManager().play("clickButton");
        });

        leftButtonsContainer.add(goUp, Alignment.centerAlign(), false);
        leftButtonsContainer.add(goDown, Alignment.centerAlign(), false);
        leftButtonsContainer.add(goBack, Alignment.centerAlign(), false);

        // RIGHT BUTTONS //

        rightButtonsContainer.setContentPlacement(rightButtonsContainer.getPosition());

        Button saveButton = new Button("SALVA", 10);
        saveButton.setSize(80, 30);
        saveButton.setMargin(new Spacing(10, 0, 0, 0));
        saveButton.setColor(Palette.GREEN);
        saveButton.onClick(() -> {
            if (DECKSIZE >= 20) {
                menu.getAudioManager().play("clickButton");
                User.getInstance().setDeck(deckCopy);
                User.getInstance().saveData();
            } else {
                menu.getAudioManager().play("impossible");
            }
        });

        Button upDeck = new Button("SALI", 10);
        upDeck.setSize(80, 30);
        upDeck.setMargin(new Spacing(40, 0, 0, 0));
        upDeck.onClick(() -> {
            scrollDown(deckContainer);
        });

        Button downDeck = new Button("SCENDI", 10);
        downDeck.setSize(80, 30);
        downDeck.setMargin(new Spacing(0, 30));
        downDeck.onClick(() -> {
            scrollUp(deckContainer);
        });

        Button clearDeck = new Button("RESET", 10);
        clearDeck.setSize(80, 30);
        clearDeck.setColor(Palette.MAGENTA);
        clearDeck.setMargin(new Spacing(0, 40));
        clearDeck.onClick(() -> {
            deckContainer.clear();
            DECKSIZE = 0;
            cardsInDeck.setLabel(DECKSIZE);
            deckCopy.clear();
            deckContainer.setContentPlacement(deckContainer.getPosition());
            menu.getAudioManager().play("clickButton");
        });

        rightButtonsContainer.add(saveButton, Alignment.centerAlign(), false);
        rightButtonsContainer.add(upDeck, Alignment.centerAlign(), false);
        rightButtonsContainer.add(downDeck, Alignment.centerAlign(), false);
        rightButtonsContainer.add(clearDeck, Alignment.centerAlign(), false);

        ownedContainer.setPadding(new Spacing(0, 5));
    }

    @Override
    public void enter() {
        DECKSIZE = 0;
        ownedCopy = (HashMap<String, Integer>)User.getInstance().getObtainedCards().clone();
        deckCopy = (HashMap<String, Integer>)User.getInstance().getDeckMap().clone();
        loadCardsInContainer(ownedContainer, ownedCopy);
        loadCardsInContainer(deckContainer, deckCopy);
        setDeckBehaviour();
        setOwnedBehaviour();
        deckCopy.forEach((key, value) -> {
            DECKSIZE += value;
        });
        cardsInDeck.setLabel(DECKSIZE);
    }

    @Override
    public void draw(double alpha) {
        baseContainer.draw(alpha);
        drawBars();
        baseContainer2.draw(alpha);
    }

    @Override
    public void update(double dt) {
        if (Syndria.input.KeyPressed(KeyEvent.VK_SPACE)) {
            menu.getStates().switchTo("launcher");
        }
        baseContainer.update(dt);
        baseContainer2.update(dt);
        if (updateContainers) {
            updateContainer(deckContainer, deckCopy);
            updateContainers = false;
        }
        if (DECKSIZE < 20) {
            cardsInDeck.setLabelColor(Palette.BORDEAUX);
        }
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }

    private void loadCardsInContainer(FixedContainer container, HashMap<String, Integer> map) {
        container.clear();
        container.setContentPlacement(container.getPosition());
        map.forEach((card, copies) -> {
            addEntry(container, card, copies);
        });
    }

    private void updateContainer(FixedContainer container, HashMap<String, Integer> deckCopy) {
        boolean removed = false;
        int index = 0;
        for (UIComponent entry : container.getChildren()) {
            if (!deckCopy.containsKey(entry.getLabel())) {
                container.getChildren().remove(entry);
                removed = true;
                break;
            }
            index++;
        }
        for (int i = index; i < container.getChildren().size(); i++) {
            if (removed) {
                FixedContainer entry = (FixedContainer) container.getChildren().get(i);
                Vector delta = new Vector(0, entry.getSize().getY() + entry.getMargin().getVertical());
                entry.setPosition(Vector.sub(entry.getPosition(), delta));
                for (UIComponent element : entry.getChildren()) {
                    element.setPosition(Vector.sub(element.getPosition(), delta));
                }
            }
        }
        try {
            container.setContentPlacement(new Vector(container.getPosition().getX(),
                    container.getChildren().getLast().getSize().getY() +
                            container.getChildren().getLast().getMargin().getBottom() +
                            container.getChildren().getLast().getPosition().getY()));
        } catch (Exception _) {
            container.setContentPlacement(container.getPosition());
        }
    }

    private void setDeckBehaviour() {
        for (UIComponent entry : deckContainer.getChildren()) {
            ((FixedContainer)entry).onClick(() -> {
                String card = entry.getLabel();
                int copies = deckCopy.get(card);
                TextBox t = (TextBox) ((FixedContainer)entry).getChildren().get(2);
                if (copies > 1) {
                    deckCopy.put(card, copies - 1);
                    t.setLabel(copies - 1);
                } else {
                    deckCopy.remove(card);
                    updateContainers = true;
                }
                menu.getAudioManager().play("clickButton");
                DECKSIZE--;
                cardsInDeck.setLabel(DECKSIZE);
            });
        }
    }

    private void setOwnedBehaviour() {
        for (UIComponent entry : ownedContainer.getChildren()) {
            ((FixedContainer)entry).onClick(() -> {
                String card = entry.getLabel();
                int copies = deckCopy.get(card) == null ? 0 : deckCopy.get(card);
                int ownedCopies = ownedCopy.get(card);
                if (copies == 0) {
                    FixedContainer e = addEntry(deckContainer, card, 1);
                    e.onClick(() -> {
                        String c = e.getLabel();
                        int cps = deckCopy.get(c);
                        TextBox t = (TextBox) e.getChildren().get(2);
                        if (cps > 1) {
                            deckCopy.put(c, cps - 1);
                            t.setLabel(cps - 1);
                        } else {
                            deckCopy.remove(c);
                            updateContainers = true;
                        }
                        menu.getAudioManager().play("clickButton");
                        DECKSIZE--;
                        cardsInDeck.setLabel(DECKSIZE);
                    });
                } else if(copies < 4 && copies < ownedCopies) {
                    deckContainer.getChildren().forEach((e) -> {
                        if (e.getLabel().equals(card)) {
                            ((TextBox)((FixedContainer)e).getChildren().getLast()).setLabel(copies + 1);
                        }
                    });
                } else {
                    menu.getAudioManager().play("impossible");
                    return;
                }
                deckCopy.merge(card, 1, Integer::sum);
                menu.getAudioManager().play("clickButton");
                DECKSIZE++;
                cardsInDeck.setLabel(DECKSIZE);
                updateContainers = true;
            });
        }
    }

    private void scrollDown(FixedContainer container) {
        if (container.getChildren().isEmpty()) return;
        if (container.getChildren().getFirst().getPosition().getY() > container.getPosition().getY()) {
            menu.getAudioManager().play("impossible");
            return;
        }
        menu.getAudioManager().play("clickButton");
        for (UIComponent card : container.getChildren()) {
            card.setPosition(Vector.sum(card.getPosition(), new Vector(0, card.getSize().getY() + card.getMargin().getTop())));
            for (UIComponent c : ((FixedContainer)card).getChildren()) {
                c.setPosition(Vector.sum(c.getPosition(), new Vector(0, card.getSize().getY() + card.getMargin().getTop())));
            }
        }
        container.setContentPlacement(Vector.sum(container.getContentPlacement(),
                new Vector(0, container.getChildren().getLast().getSize().getY() +
                        container.getChildren().getLast().getMargin().getBottom())));
    }

    private void scrollUp(FixedContainer container) {
        if (container.getChildren().isEmpty()) return;
        if (container.getChildren().getLast().getPosition().getY() +
                container.getChildren().getLast().getSize().getY() < container.getPosition().getY() + container.getSize().getY()) {
            menu.getAudioManager().play("impossible");
            return;
        }
        menu.getAudioManager().play("clickButton");
        for (UIComponent card : container.getChildren()) {
            card.setPosition(Vector.sub(card.getPosition(), new Vector(0, card.getSize().getY() + card.getMargin().getTop())));
            for (UIComponent c : ((FixedContainer)card).getChildren()) {
                c.setPosition(Vector.sub(c.getPosition(), new Vector(0, card.getSize().getY() + card.getMargin().getTop())));
            }
        }
        container.setContentPlacement(Vector.sub(container.getContentPlacement(),
                new Vector(0, container.getChildren().getLast().getSize().getY() +
                        container.getChildren().getLast().getMargin().getBottom())));
    }

    private FixedContainer addEntry(FixedContainer container, String card, int copies) {
        FixedContainer entry = new FixedContainer();
        entry.setLabel(card);
        entry.setRelativeDimensions(.9, .1);
        entry.setMargin(new Spacing(5));
        entry.setBgVisible(true);
        container.add(entry, Alignment.centerAlign(), true);

        ShallowCard SC = CardDex.getCardByName(card);
        Picture zoomedCard = new Picture(SC.getPath());
        zoomedCard.scale(0.3, 0.3);
        zoomedCard.setMargin(new Spacing(4, 4, 4, 5));

        MultiLineText t = new MultiLineText(SC.getTitle(), 10, "Visitor TT1 BRK", Palette.WHITE);
        t.setMargin(new Spacing(5, 10));

        TextBox cps = new TextBox(copies, 10, "Visitor TT1 BRK", Palette.WHITE);
        cps.setMargin(new Spacing(5, 10));

        entry.setContentPlacement(entry.getPosition());
        entry.add(zoomedCard, Alignment.straightAlign(), false);
        entry.add(t, Alignment.straightAlign(), false);
        entry.setContentPlacement(entry.getPosition());
        entry.add(cps, Alignment.rightAlign(), false);

        return entry;
    }

    private void drawBars() {
        Syndria.gfx.drawRect(0, 0, 720, 40, Palette.LILLE);
        Syndria.gfx.drawRect(0, 440, 720, 40, Palette.LILLE);
    }
}
