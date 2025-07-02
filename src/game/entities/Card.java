package game.entities;

import com.syndria.core.SoundClip;
import com.syndria.gfx.Image;
import com.syndria.math.Vector;
import com.syndria.ui.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

interface CardValue {
    double getValue();
    CardValue copy();
}

class SingleValue implements CardValue {
    private double value;

    public SingleValue(int value) {
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }

    public void changeValue(double value) {
        this.value = value;
    }

    @Override
    public CardValue copy() {
        return new SingleValue((int)value);
    }
}

class MultiValue implements CardValue {
    private final ArrayList<Integer> values;
    private double value;
    private int index;

    public MultiValue(ArrayList<Integer> values) {
        this.values = new ArrayList<>(values);
        value = values.getFirst();
        index = 0;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public CardValue copy() {
        return new MultiValue((ArrayList<Integer>) values.clone());
    }

    public ArrayList<Integer> getList() {
        return values;
    }

    public void changeValuesBy(double d) {
        for (int i = 0; i < values.size(); i++) {
            values.set(i, (int)(values.get(i) * d));
        }
        value = (int) (value * d);
    }

    public void setValue(int i) {
        index = i;
        value = values.get(i);
    }

    public int getIndex() {
        return index;
    }
}

public class Card extends UIComponent {

    private CardValue value;
    protected Image img;
    protected String type = "normal";
    private String path;
    protected String name;
    protected TextBox[] options;
    private FixedContainer fc;
    public int priority;
    private boolean oneshot;
    private boolean activeEffect;

    private String side;

    public Card(String name, CardValue value, String path, String type, int priority, boolean oneshot, boolean activeEffect) {
        super();
        this.name = name;
        this.size = new Vector(75, 108);
        this.path = path;
        this.img = path.isEmpty() ? null : new Image(path);
        this.value = value;
        this.oneshot = oneshot;
        this.priority = priority;
        this.activeEffect = activeEffect;
        super.setMargin(new Spacing(0, 0, 2, 3));
        if (type != null) {
            this.type = type;
        }
        if ( value instanceof MultiValue ) {
            int n = ((MultiValue) value).getList().size();
            options = new TextBox[n];
            int i = 0;
            for (int v : ((MultiValue) value).getList()) {
                TextBox t = new TextBox(v, 12, 0xFFFFF1E8, new Vector(1, 1d/n));
                t.setHoverColor(0x889F9188);
                t.setHoverEffects(true);
                int finalI = i;
                t.onClick(() -> {
                    ((MultiValue) value).setValue(finalI);
                });
                options[i] = t;
                i++;
            }
        } else if (!activeEffect) {
            options = new TextBox[1];
            options[0] = new TextBox("Attiva", 12, 0xFFFFF1E8, new Vector(1, 0.5));
            options[0].setHoverColor(0x889F9188);
            options[0].setHoverEffects(true);
            options[0].onClick(() -> {
                setActiveEffect(true);
                setOneShot(true);
                SoundClip activateEffect = new SoundClip("audio/sounds/activateEffect.wav");
                activateEffect.play();
            });
        }
    }

    public Card(String name, CardValue value, String path, String type) {
        this(name, value, path, type, 5, false, true);
    }

    private Card(String name, CardValue value, Image image, String type, int priority, boolean oneshot, boolean activeEffect){
        this(name, value, "", type, priority, oneshot, activeEffect);
        this.img = image;
    }

    public void setFixedContainer() {
        if (type.equals("ace")) {
            fc = new FixedContainer(position.copy(), size);
            for (TextBox t : options) {
                fc.add(t, Alignment.centerAlign(), true);
            }
        } else if(type.equals("boost") && !activeEffect) {
            fc = new FixedContainer(position, size);
            for (TextBox t : options) {
                fc.add(t, Alignment.centerAbsolute(), true);
            }
        } else {
            fc = null;
        }
    }

    @Override
    public void draw(double alpha) {
        this.img.draw(this.position);
        if (hover && fc != null) {
            fc.draw(alpha);
        }
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if (fc != null) {
            fc.update(dt);
        }
    }

    @Override
    public void onPositionChange() {
        setFixedContainer();
    }

    @Override
    public void onSizeChange() {

    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public double getValue() {
        return value.getValue();
    }

    public CardValue getValueObj() {
        return value;
    }

    public int getIndex() {
        if (value instanceof MultiValue) {
            return ((MultiValue)value).getIndex();
        } else {
            System.err.println("Instance of type CardValue is not MultiValue");
            return -1;
        }
    }

    public void pickValue(int i) {
        if (value instanceof MultiValue) {
            ((MultiValue)value).setValue(i);
        }
    }

    public void setActiveEffect(boolean activeEffect) {
        this.activeEffect = activeEffect;
    }

    public boolean getActiveEffect() {
        return activeEffect;
    }

    public ArrayList<Integer> getValuesList() {
        return ((MultiValue)value).getList();
    }

    public static void populateHashMap(HashMap<String, Card> hm, Deck d) {
        try (InputStream is = Card.class.getResource(("/gameResources/data/cards.xml")).openStream()){
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            // Normalize the XML structure
            doc.getDocumentElement().normalize();

            // Get all <card> elements
            NodeList cardList = doc.getElementsByTagName("card");

            // Loop through each <card> element
            for (int i = 0; i < cardList.getLength(); i++) {
                Node cardNode = cardList.item(i);

                if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element cardElement = (Element) cardNode;

                    // Extract the 'name' attribute
                    String name = cardElement.getAttribute("name");

                    if (d.getCards().contains(name)) {
                        String path = cardElement.getElementsByTagName("path").item(0).getTextContent();
                        path = "/gameResources/gfx/cards/" + path;
                        String type = null;

                        try { //Combo with predefined type "normal", if there's no type attribute in xml, then it's normal
                            type = cardElement.getElementsByTagName("type").item(0).getTextContent();
                        } catch (Exception _) {}

                        NodeList valueNodes = cardElement.getElementsByTagName("value");
                        CardValue value;
                        int p = 5;
                        if (valueNodes.getLength() > 1) {
                            ArrayList<Integer> values = new ArrayList<>();
                            for (int j = 0; j < valueNodes.getLength(); j++) {
                                values.add(Integer.parseInt(valueNodes.item(j).getTextContent()));
                            }
                            value = new MultiValue(values);
                        } else if (type != null && type.equals("boost")) {
                            value = new SingleValue(0);
                            p = Integer.parseInt(cardElement.getElementsByTagName("priority").item(0).getTextContent());
                        } else {
                            value = new SingleValue(Integer.parseInt(valueNodes.item(0).getTextContent()));
                        }
                        Card card = new Card(name, value, path, type);
                        card.priority = p;
                        if (cardElement.hasAttribute("oneshot")) {
                            card.oneshot = Boolean.parseBoolean(cardElement.getAttribute("oneshot"));
                        }
                        if (cardElement.hasAttribute("active")) {
                            card.setActiveEffect(Boolean.parseBoolean(cardElement.getAttribute("active")));
                        }
                        hm.put(name, card);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCardType() {
        return type;
    }

    public String getCardName() {
        return name;
    }

    public Card copy() {
        return new Card(this.name, this.value.copy(), this.img, this.type, this.priority, this.oneshot, this.activeEffect);
    }

    public static CardEffect getEffect(String name) {
        switch (name) {
            case "ape": return CardEffect.ape();
            case "ziska": return CardEffect.ziska();
            case "lara": return CardEffect.lara();
            case "matti": return CardEffect.matti();
            case "nina": return CardEffect.nina();
            case "kevin": return CardEffect.kevin();
            case "giovi": return CardEffect.giovi();
            case "gek": return CardEffect.gek();
            case "nico": return CardEffect.nico();
            default: return null;
        }
    }

    public void setOneShot(boolean oneshot) {
        this.oneshot = oneshot;
    }

    public boolean isOneShot() {
        return oneshot;
    }
}