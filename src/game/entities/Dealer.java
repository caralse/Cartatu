package game.entities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class Dealer {

    private Deck deck;
    private String deckPath;
    private int reward;

    public Dealer(int level) {
        loadByLevel(level);
        deck = new Deck(deckPath, "dealer");
    }

    public Dealer(ArrayList<String> cards) {
        reward = 200;
        deck = new Deck("dealer", cards);
    }

    private void loadByLevel(int level) {
        try (InputStream is = this.getClass().getResource("/gameResources/data/dealers.xml").openStream()){
            InputStream src = is;
            InputStream bufferedIn = new BufferedInputStream(src);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(bufferedIn);
            doc.getDocumentElement().normalize();

            NodeList dealerList = doc.getElementsByTagName("dealer");

            for (int i = 0; i < dealerList.getLength(); i++) {
                Node node = dealerList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element dealer = (Element) node;
                    if (Integer.parseInt(dealer.getAttribute("level")) == level) {
                        deckPath = dealer.getElementsByTagName("deck").item(0).getTextContent() + ".xml";
                        reward = Integer.parseInt(dealer.getElementsByTagName("reward").item(0).getTextContent());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Deck getDeck() {
        return deck;
    }

    public int getReward() {
        return reward;
    }
}
