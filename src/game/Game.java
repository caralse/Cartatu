package game;

import com.syndria.Syndria;
import com.syndria.gfx.Image;
import com.syndria.state.StateManager;
import game.states.Splash;
import game.states.match.Match;
import game.states.menu.Menu;

import java.awt.*;

public class Game extends Syndria {

    private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final static double HEIGHT = screenSize.getHeight()/520;
    private final static float SCALE = Math.floor(HEIGHT) + 0.5f > HEIGHT ? (float)Math.floor(HEIGHT) : (float)Math.floor(HEIGHT) + 0.5f ;

    private final Image lines = new Image("/gameResources/gfx/match/lines.png");

    public final StateManager stateManager;

    public Game(){
        super("CARTATÙ", 720, 480, 1);
        this.stateManager = new StateManager();
    }

    @Override
    public void load(){
        Splash splash = new Splash("splash", stateManager);
        stateManager.add(splash);
        Menu menu = new Menu("menu", stateManager);
        stateManager.add(menu);
        stateManager.add(new Match("match", stateManager, menu));
        Syndria.SyndriaSplashState.setStateManager(stateManager);
        Syndria.SyndriaSplashState.setExitState("splash");
        stateManager.add(Syndria.SyndriaSplashState);
        stateManager.switchTo("SyndriaSplashState");
    }

    public void draw(double alpha) {
        stateManager.draw(alpha);
        //Syndria.gfx.getAWT().drawString(String.format("%d", Syndria.getFPS()) + "\n TEST", 10, 10);
    }

    public void update(double dt) {
        stateManager.update(dt);
    }
}
