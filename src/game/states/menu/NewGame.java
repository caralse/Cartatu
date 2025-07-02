package game.states.menu;

import com.syndria.Syndria;
import com.syndria.math.Vector;
import com.syndria.state.State;
import com.syndria.ui.*;
import game.entities.Button;
import game.entities.User;

import java.awt.event.KeyEvent;

public class NewGame extends State {
    private Menu menu;
    private FixedContainer fc;
    private FixedContainer inputBox;

    private TextInput t;
    private Button start;
    private Button goBack;

    public NewGame(String label, Menu menu) {
        this.label = label;
        this.menu = menu;
        inputBox = new FixedContainer(0, 0, 300, 300);

        fc = new FixedContainer();
        Vector relative = new Vector(0.5, 0.5);

        inputBox.setRelativeDimensions(relative);
        inputBox.setBgVisible(true);
        fc.setBgColor(0xFF83769C);
        fc.setBgVisible(true);
        fc.clear();
        fc.add(inputBox, Alignment.centerAbsolute(), true);

        TextBox username = new TextBox("username", 18, 0xFFFFF1E8);
        username.setMargin(new Spacing(150, 0, 0, 0));
        fc.add(username, Alignment.centerAlign(), false);

        relative = new Vector(0.2, 0.10);

        t = new TextInput(24, new Vector(200, 32), 0xFF666666);
        t.setMargin(new Spacing(10));
        fc.add(t, Alignment.centerAlign(), false);

        start = new Button("CONFERMA", 18);
        start.setRelativeDimensions(relative);
        start.onClick(this::confirmUsername);

        start.setMargin(new Spacing(10));
        fc.add(start, Alignment.centerAlign(), true);

        goBack = new Button("INDIETRO", 18);
        goBack.setRelativeDimensions(relative);
        goBack.onClick(() -> {
            menu.getStates().setCurrentState("start");
            menu.getAudioManager().play("clickButton");
        });

        fc.add(goBack, Alignment.centerAlign(), true);

    }

    @Override
    public void enter() {

    }

    @Override
    public void draw(double alpha) {
        fc.draw(alpha);
    }

    @Override
    public void update(double dt) {
        start.update(dt);
        goBack.update(dt);
        fc.update(dt);
        if (Syndria.input.KeyPressed(KeyEvent.VK_ENTER)) {
            confirmUsername();
        }
    }

    private void confirmUsername() {
        if(!t.getText().isEmpty()) {
            User.getInstance().init();
            User.getInstance().setUsername(t.getText().toLowerCase());
            User.getInstance().saveData();
            menu.getAudioManager().play("clickButton");
            menu.getStates().add(new Launcher("launcher", menu));
            menu.getStates().switchTo("launcher");
        }
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }
}
