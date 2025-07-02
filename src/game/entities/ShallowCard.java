package game.entities;

import com.syndria.Syndria;
import com.syndria.gfx.Image;
import com.syndria.ui.UIComponent;

import java.util.ArrayList;

public class ShallowCard extends UIComponent {
    private String name;
    private String type;
    private String description;
    private String path;
    private Image img;
    private String title;
    private ArrayList<Integer> values;

    private Runnable draw;
    private Runnable click;

    private boolean owned;
    private int ownedCopies;

    private boolean rare;

    public ShallowCard(String name, String type, String description, String path, String title, boolean rare, ArrayList<Integer> values) {
        super();
        this.name = name;
        this.type = type == null ? "base" : type;
        this.description = description == null ? "" : description;
        this.path = path.contains("/gameResources/gfx/cards/") ? path : "/gameResources/gfx/cards/" + path;
        this.title = title == null ? "" : title;
        this.rare = rare;
        this.values = values;
        img = new Image(this.path);
        size = img.getSize();
    }

    @Override
    public void draw(double alpha) {
        if (draw != null) {
            draw.run();
        } else {
            img.draw(position);
        }
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (isHover() && Syndria.input.MousePressed(1)) {
            if (click != null) {
                click.run();
            }
        }
    }

    @Override
    public void onPositionChange() {

    }

    @Override
    public void onSizeChange() {

    }

    public void setDraw(Runnable draw) {
        this.draw = draw;
    }

    public void onClick(Runnable click) {
        this.click = click;
    }

    public Runnable getOnClick() {
        return click;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Image getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public void setOwnedCopies(int value) {
        ownedCopies = value;
    }

    public int getOwnedCopies() {
        return ownedCopies;
    }

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    public boolean isRare() {
        return rare;
    }

    public void setRare(boolean rare) {
        this.rare = rare;
    }

    public ArrayList<Integer> getValues() {
        return values;
    }

    public CardInfo getInfo() {
        return new CardInfo(this);
    }

    public ShallowCard copy() {
        return new ShallowCard(this.name, this.type, this.description, this.path, this.title, this.rare, this.values);
    }

}
