package game.states.menu;

import com.syndria.math.Vector;
import com.syndria.state.State;
import com.syndria.ui.Alignment;
import com.syndria.ui.FixedContainer;
import com.syndria.ui.Spacing;
import game.Palette;
import game.entities.Button;
import game.entities.SelectedDealer;
import game.entities.User;

public class DealerSelection extends State {

    private final Menu menu;

    private final FixedContainer baseContainer;
    private Button goBack;

    public DealerSelection(String label, Menu menu) {
        this.label = label;
        this.menu = menu;
        baseContainer = new FixedContainer();
        baseContainer.setBgVisible(true);
        baseContainer.setBgColor(Palette.LILLE);

        goBack = new Button("INDIETRO", 12);
        goBack.setMargin(new Spacing(10, 200));
        goBack.setSize(120, 32);
        goBack.setPosition(new Vector(50, 225));
        goBack.onClick(() -> {
            menu.getStates().setCurrentState("launcher");
            menu.getAudioManager().play("clickButton");
        });
    }

    @Override
    public void enter() {
        baseContainer.clear();

        Button standard = new Button("STANDARD", 18);
        standard.setSize(150, 40);
        standard.setColor(Palette.BLUE);
        standard.setMargin(new Spacing(20));
        standard.onClick(() -> {
            SelectedDealer.getInstance().setDealer(SelectedDealer.STANDARD);
            menu.getGameStates().switchTo("match");
            menu.getStates().setCurrentState("launcher");
        });

        baseContainer.add(standard, Alignment.inLineVertical(), false);
        for (int i = 2; i <= User.getInstance().getLevel(); i++) {
            Button dealerButton = new Button(String.format("Dealer %d", i), 18);
            dealerButton.setSize(150, 40);
            dealerButton.setColor(Palette.BLUE);
            dealerButton.setMargin(new Spacing(20));
            int finalI = i;
            dealerButton.onClick(() -> {
                SelectedDealer.getInstance().setDealer(finalI);
                menu.getGameStates().switchTo("match");
                menu.getStates().setCurrentState("launcher");
            });
            baseContainer.add(dealerButton, Alignment.inLineVertical(), false);
        }
    }

    @Override
    public void draw(double alpha) {
        baseContainer.draw(alpha);
        goBack.draw(alpha);
    }

    @Override
    public void update(double dt) {
        goBack.update(dt);
        baseContainer.update(dt);
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }
}
