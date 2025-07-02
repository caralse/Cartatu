package game.states.menu;

import com.syndria.Syndria;
import com.syndria.state.State;
import com.syndria.ui.Alignment;
import com.syndria.ui.FixedContainer;
import com.syndria.ui.Spacing;
import com.syndria.ui.TextBox;
import game.Palette;

import java.awt.event.KeyEvent;
import java.io.*;

public class Rules extends State {

    private Menu menu;
    private int index;

    FixedContainer rulesContainer;
    FixedContainer rulesContainer2;

    public Rules(String label, Menu menu) {
        this.label = label;
        this.menu = menu;
        rulesContainer = new FixedContainer();
        rulesContainer.setPadding(new Spacing(75, 50));
        rulesContainer2 = new FixedContainer();
        rulesContainer2.setPadding(new Spacing(50, 50));

        try (InputStream is = this.getClass().getResourceAsStream("/gameResources/data/rules2.txt")){
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while((line = br.readLine()) != null){
                if (line.equals("--break--")) break;
                TextBox t = new TextBox(line, 16, "Pixellari", Palette.PEACH);
                t.setMargin(new Spacing(20, 2));
                rulesContainer.add(t, Alignment.leftAlign(), false);
            }
            while((line = br.readLine()) != null){
                if (line.equals("--break--")) break;
                TextBox t = new TextBox(line, 16, "Pixellari", Palette.PEACH);
                t.setMargin(new Spacing(20, 2));
                rulesContainer2.add(t, Alignment.leftAlign(), false);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void enter() {
        index = 0;
    }

    @Override
    public void draw(double alpha) {
        Syndria.gfx.drawRect(0, 0, 720, 480, Palette.BLUE);
        if (index == 0) {
            rulesContainer.draw(alpha);
        } else if (index == 1) {
            rulesContainer2.draw(alpha);
        }

    }

    @Override
    public void update(double dt) {

        if (Syndria.input.MousePressed(1) || Syndria.input.KeyPressed(KeyEvent.VK_SPACE)) {
            index++;
        }

        if (index == 2) {
            menu.getStates().setCurrentState("launcher");
        }

        rulesContainer.update(dt);
        rulesContainer2.update(dt);
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }
}
