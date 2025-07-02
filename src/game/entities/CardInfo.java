package game.entities;

import com.syndria.ui.*;

public class CardInfo extends UIComponent {

    private ShallowCard card;

    private final static int TITLECOLOR = 0xFFFFA300;
    private final static int DESCCOLOR = 0xFFFFCCAA;
    private final static int BGCOLOR = 0x50FFF1E8;
    private final static int TYPECOLOR = 0xFFC2C3C7;
    private final static int OWNEDCOLOR = 0xFF837695;

    private Container container;

    public CardInfo(ShallowCard card) {
        super();
        setRelativeDimensions(1, 1);
        container = new Container(0, 0, 0, 0);
        container.setBgVisible(true);
        container.setBgColor(BGCOLOR);
        this.card = card;

        int totalVertical = 0;

        MultiLineText title = new MultiLineText(card.getTitle(), 12, TITLECOLOR);
        title.setMargin(new Spacing(10, 6));

        totalVertical += (int)title.getSize().getY() + title.getMargin().getTop();

        MultiLineText type = new MultiLineText("Carta " + card.getType(), 10, TYPECOLOR);
        type.setMargin(new Spacing(10, totalVertical));

        totalVertical += (int)type.getSize().getY() + 5;

        String stringValues = card.getValues().toString();
        TextBox values = new TextBox(stringValues.substring(1, stringValues.length()-1), 10, OWNEDCOLOR);
        values.setMargin(new Spacing(10, totalVertical));

        MultiLineText description = new MultiLineText(card.getDescription(), 10, DESCCOLOR);
        description.setMargin(new Spacing(10, 64));

        MultiLineText ownedCopies = new MultiLineText(String.format("Copie: %d", card.getOwnedCopies()), 10, OWNEDCOLOR);
        ownedCopies.setMargin(new Spacing(10, 170));

        Picture zoomedCard = new Picture(card.getPath());
        zoomedCard.scale(2, 2);
        zoomedCard.setMargin(new Spacing(25, 200));

        container.add(title);
        if (card.isOwned()) {
            container.add(type);
            container.add(values);
            container.add(description);
            container.add(ownedCopies);
            container.add(zoomedCard);
        }
    }

    @Override
    public void onSizeChange() {
        container.setSize(size);
    }

    @Override
    public void draw(double alpha) {
        container.draw(alpha);
    }

    @Override
    public void onPositionChange() {
        container.setPosition(position);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        container.update(dt);
    }
}
