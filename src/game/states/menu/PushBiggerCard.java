package game.states.menu;

import com.syndria.Syndria;
import com.syndria.state.State;
import com.syndria.ui.Alignment;
import com.syndria.ui.Picture;

import java.awt.event.KeyEvent;

public class PushBiggerCard extends State {

    private final Picture card;
    private final State state;

    public PushBiggerCard(String path, State state) {
        card = new Picture(path);
        card.scale(3, 3);
        this.state = state;
        ((Cartario)state).getDexContainer().setContentPlacement(((Cartario)state).getDexContainer().getPosition());
        ((Cartario)state).getDexContainer().add(card, Alignment.centerAbsolute(), false);
    }

    @Override
    public void enter() {}

    @Override
    public void draw(double alpha) {
        card.draw(alpha);
    }

    @Override
    public void update(double dt) {
        if (Syndria.input.MousePressed(1) || Syndria.input.KeyPressed(KeyEvent.VK_SPACE)) {
            ((Cartario)state).getDexContainer().remove(card);
            ((Cartario)state).getStates().pop();
        }
    }

    @Override
    public boolean blocksWhenPushed() {
        return true;
    }
}
