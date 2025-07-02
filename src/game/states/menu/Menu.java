package game.states.menu;

import com.syndria.core.AudioManager;
import com.syndria.gfx.Image;
import com.syndria.state.State;
import com.syndria.state.StateManager;

public class Menu extends State {
    private StateManager gameStates;
    private StateManager menuStates;
    private AudioManager menuAudio;

    private Image background;

    public Menu(String label, StateManager gameStates) {
        super(label);
        this.gameStates = gameStates;
        this.menuStates = new StateManager();
        menuAudio = new AudioManager();
        menuAudio.loadSound("clickButton", "audio/sounds/clickButton.wav");
        menuAudio.loadSound("menu", "audio/music/menu.wav");
        menuAudio.loadSound("impossible", "audio/sounds/not_possible.wav");
        menuStates.add(new Start("start", this));
        gameStates.add(new ReloadLauncher("reloadLauncher", this));
    }

    @Override
    public void enter() {
        menuStates.switchTo("start");
    }

    @Override
    public void draw(double alpha) {
        menuStates.draw(alpha);
    }

    @Override
    public void update(double dt) {
        menuStates.update(dt);
    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }

    public StateManager getStates() {
        return menuStates;
    }

    public StateManager getGameStates() {
        return gameStates;
    }

    public AudioManager getAudioManager() {
        return menuAudio;
    }
}
