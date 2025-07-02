package game.states.menu;

import com.syndria.state.State;

public class ReloadLauncher extends State {

    private Menu menu;

    public ReloadLauncher(String label, Menu menu) {
        this.label = label;
        this.menu = menu;
    }

    @Override
    public void enter() {

    }

    @Override
    public void draw(double alpha) {
        menu.getStates().switchTo("launcher");
        menu.getGameStates().setCurrentState("menu");
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }
}
