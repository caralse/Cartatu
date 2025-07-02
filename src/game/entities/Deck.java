package game.entities;

import com.syndria.gfx.Image;
import com.syndria.ui.UIComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Deck extends UIComponent {
    private final ArrayList<String> cards = new ArrayList<>();
    private final ArrayList<String> discarded = new ArrayList<>();
    private String path = "Deck.png";
    private Image img;

    public Deck(String path, String side) {
        super();
        this.path = "/gameResources/gfx/match/" + side + this.path;
        img = new Image(this.path);
        loadFromXML(path);
        this.setSize(img.getSize());
    }

    public Deck(String side,  HashMap<String, Integer> cards) {
        super();
        path = "/gameResources/gfx/match/" + side + path;
        img = new Image(this.path);
        this.setSize(img.getSize());
        cards.forEach((card, value) -> {
            for (int i = 0; i < value; i++) {
                this.cards.add(card);
            }
        });
    }

    public Deck(String side,  ArrayList<String> cards) {
        super();
        path = "/gameResources/gfx/match/" + side + path;
        img = new Image(this.path);
        this.setSize(img.getSize());
        this.cards.addAll(cards);
    }

    private void loadFromXML(String path) {
        try (InputStream src = this.getClass().getResource("/gameResources/data/decks/" + path).openStream()) {
            InputStream bufferedIn = new BufferedInputStream(src);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(bufferedIn);

            // Normalize the XML structure
            doc.getDocumentElement().normalize();

            // Get all <card> elements
            NodeList cardList = doc.getElementsByTagName("card");

            for (int i = 0; i < cardList.getLength(); i++) {
                Node cardNode = cardList.item(i);

                if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element cardElement = (Element) cardNode;

                    String name = cardElement.getElementsByTagName("name").item(0).getTextContent();
                    int quantity = 1;

                    if (cardElement.getElementsByTagName("quantity").getLength() > 0) {
                        quantity = Integer.parseInt(cardElement.getElementsByTagName("quantity").item(0).getTextContent());
                    }

                    for (int k = 0; k < quantity; k++) {
                        cards.add(name);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(double alpha) {
        img.draw(position);
    }

    @Override
    public void onPositionChange() {

    }

    @Override
    public void onSizeChange() {

    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public String drawCard() {
        try {
            String draw = cards.removeLast();
            return draw;
        } catch (Exception e) {
            return null;
        }
    }

    public void restore() {
        cards.addAll(discarded);
        discarded.clear();
    }

    public void discard(String card) {
        discarded.add(card);
    }

    public int getRemainingCards() {
        return cards.size();
    }

    public ArrayList<String> getCards() {
        return cards;
    }

    public void addCard(String cardname) {
        cards.add(cardname);
    }
}
