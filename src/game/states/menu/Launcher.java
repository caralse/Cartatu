package game.states.menu;

import com.syndria.math.Vector;
import com.syndria.state.State;
import com.syndria.ui.Alignment;
import com.syndria.ui.FixedContainer;
import com.syndria.ui.Spacing;
import com.syndria.ui.TextBox;
import game.Palette;
import game.entities.Button;
import game.entities.CardDex;
import game.entities.User;

public class Launcher extends State {

    private FixedContainer baseContainer;
    private FixedContainer userInfoContainer;
    private FixedContainer onlineUIContainer;

    private final static int BASICCOLOR = 0xFFFFF1E8;
    private final static int COINSCOLOR = 0xFFFFA300;

    private final Menu menu;

    public Launcher(String label, Menu menu){
        this.label = label;
        this.menu = menu;

        menu.getStates().add(new Shop("shop", menu));
        menu.getStates().add(new CreateDeck("createDeck", menu));
        menu.getStates().add(new Rules("rules", menu));
        menu.getStates().add(new DealerSelection("dealers", menu));
        menu.getStates().add(new Connect("connect", menu));

        try {
            CardDex.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        menu.getStates().add(new Cartario("cartario", menu));

        baseContainer = new FixedContainer();
        baseContainer.setBgColor(0xFF83769C);
        baseContainer.setBgVisible(true);
        baseContainer.setPadding(new Spacing(0, 20));

        userInfoContainer = new FixedContainer(0, 0, 100, 100);

        onlineUIContainer = new FixedContainer(420, 0, 300, 480);

        Vector relativeButton = new Vector(0.2, 0.1);

        Button cardDexButton = new Button("cartario", 12);
        cardDexButton.setRelativeDimensions(relativeButton);
        cardDexButton.setSize(120, 32);
        cardDexButton.setMargin(new Spacing(20));
        cardDexButton.onClick(() -> {
            menu.getStates().switchTo("cartario");
            menu.getAudioManager().play("clickButton");
        });

        Button matchButton = new Button("match", 12);
        matchButton.setRelativeDimensions(relativeButton);
        matchButton.setSize(120, 32);
        matchButton.setMargin(new Spacing(20));
        matchButton.onClick(() -> {
            menu.getStates().switchTo("dealers");
            menu.getAudioManager().play("clickButton");
        });

        Button createDeck = new Button("deck", 12);
        createDeck.setRelativeDimensions(relativeButton);
        createDeck.setSize(120, 32);
        createDeck.setMargin(new Spacing(20));
        createDeck.onClick(() -> {
            menu.getStates().switchTo("createDeck");
            menu.getAudioManager().play("clickButton");
        });

        Button shopButton = new Button("shop", 12);
        shopButton.setRelativeDimensions(relativeButton);
        shopButton.setSize(120, 32);
        shopButton.setMargin(new Spacing(20));
        shopButton.onClick(() -> {
            menu.getStates().switchTo("shop");
            menu.getAudioManager().play("clickButton");
        });

        Button rulesButton = new Button("info", 12);
        rulesButton.setRelativeDimensions(relativeButton);
        rulesButton.setSize(120, 32);
        rulesButton.setMargin(new Spacing(20));
        rulesButton.onClick(() -> {
            menu.getStates().switchTo("rules");
            menu.getAudioManager().play("clickButton");
        });

        baseContainer.add(cardDexButton, Alignment.centerAlign(), true);
        baseContainer.add(matchButton, Alignment.centerAlign(), true);
        baseContainer.add(createDeck, Alignment.centerAlign(), true);
        baseContainer.add(shopButton, Alignment.centerAlign(), true);
        baseContainer.add(rulesButton, Alignment.centerAlign(), true);

        baseContainer.add(userInfoContainer, Alignment.none(), false);
        baseContainer.add(onlineUIContainer, Alignment.none(), false);

        Button findMatchButton = new Button("Online Match", 12);
        findMatchButton.setSize(140, 42);
        findMatchButton.setColor(Palette.BRONZE);
        findMatchButton.setMargin(new Spacing(80));
        findMatchButton.onClick(() -> {
            menu.getStates().switchTo("connect");
            menu.getAudioManager().play("clickButton");
        });

        onlineUIContainer.add(findMatchButton, Alignment.centerAbsolute(), false);
    }

    @Override
    public void enter() {
        if (!menu.getAudioManager().isPlaying("menu")) {
            menu.getAudioManager().play("menu", true);
        }

        userInfoContainer.getChildren().clear();
        userInfoContainer.setContentPlacement(userInfoContainer.getPosition());

        TextBox username = new TextBox(User.getInstance().getUsername(), 12, BASICCOLOR);
        username.setMargin(new Spacing(10));
        TextBox userCoins = new TextBox(String.format("%d$", User.getInstance().getCoins()), 12, COINSCOLOR);
        userCoins.setMargin(new Spacing(10, 0));

        userInfoContainer.add(username, Alignment.leftAlign(), false);
        userInfoContainer.add(userCoins, Alignment.leftAlign(), false);
    }

    @Override
    public void draw(double alpha) {
        baseContainer.draw(alpha);
    }

    @Override
    public void update(double dt) {
        baseContainer.update(dt);
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }
}
