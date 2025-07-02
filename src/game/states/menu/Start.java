package game.states.menu;

import com.syndria.gfx.Image;
import com.syndria.math.Vector;
import com.syndria.state.State;
import com.syndria.state.StateManager;
import com.syndria.ui.Alignment;
import com.syndria.ui.FixedContainer;
import com.syndria.ui.Spacing;
import game.entities.Button;
import game.entities.User;
import game.states.match.Match;

public class Start extends State {

    private StateManager menuStates;
    private Menu menu;

    private FixedContainer container;
    private Image background;

    public Start(String label, Menu menu) {
        this.label = label;
        this.menu = menu;
        container = new FixedContainer(0, 200, 720, 280);
        menuStates = menu.getStates();
        background = new Image("/gameResources/gfx/initialmenu.png");
    }

    @Override
    public void enter() {
        Vector relative = new Vector(0.4, 0.3);
        menuStates.add(new NewGame("newGame", menu));
        container.clear();

        Button start = new Button("INIZIA");
        start.setRelativeDimensions(relative);
        start.onClick(() -> {
            menuStates.switchTo("newGame");
            menu.getAudioManager().play("clickButton");
        });
        container.add(start, Alignment.centerAlign(), true);

        if (User.getInstance().loadUserData()) {
            Button continueGame = new Button("CONTINUA");
            continueGame.setMargin(new Spacing(0, 10));
            continueGame.setRelativeDimensions(relative);
            continueGame.onClick(() -> {
                menuStates.add(new Launcher("launcher", menu));
                menuStates.switchTo("launcher");
                menu.getAudioManager().play("clickButton");
            });
            container.add(continueGame, Alignment.centerAlign(), true);
        }
    }

    @Override
    public void draw(double alpha) {
        background.draw(0, 0);
        container.draw(alpha);
    }

    @Override
    public void update(double dt) {
        container.update(dt);
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }
}
