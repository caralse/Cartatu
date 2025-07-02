package game.entities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class CardDex {
    private final static ArrayList<ShallowCard> CardDex = new ArrayList<>();

    public static void load() throws Exception {
        CardDex.clear();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        try (InputStream is = CardDex.class.getResource(("/gameResources/data/cards.xml")).openStream()) {
            Document doc = builder.parse(is);

            NodeList nodeList = doc.getElementsByTagName("card");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);

                String name = element.getAttribute("name");

                String type = element.getElementsByTagName("type").item(0) == null ? null :
                        element.getElementsByTagName("type").item(0).getTextContent();

                String description = element.getElementsByTagName("desc").item(0) == null ? null :
                        element.getElementsByTagName("desc").item(0).getTextContent();

                String path = element.getElementsByTagName("path").item(0).getTextContent();

                String title = element.getElementsByTagName("title").item(0) == null ? null :
                        element.getElementsByTagName("title").item(0).getTextContent();

                boolean rare = element.hasAttribute("rare");

                ArrayList<Integer> values = new ArrayList<>();
                if (type == null || !type.equals("boost")) {
                    for (int k = 0; k < element.getElementsByTagName("value").getLength(); k++) {
                        values.add(Integer.parseInt(element.getElementsByTagName("value").item(k).getTextContent()));
                    }
                }

                CardDex.add(new ShallowCard(name, type, description, path, title, rare, values));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ShallowCard> getCards() {
        return CardDex;
    }

    public static ShallowCard getCardByName(String name) {
        return CardDex.stream()
                .filter(card -> card.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static ShallowCard getRandom() {
        Random rnd = new Random();
        int i = rnd.nextInt(0, CardDex.size());
        while (!CardDex.get(i).getType().equals("base")) {
            i = rnd.nextInt(0, CardDex.size());
        }
        return CardDex.get(i).copy();
    }

    public static ShallowCard getRandomAce(boolean rare) {
        Random rnd = new Random();
        int i = rnd.nextInt(0, CardDex.size());
        while (!CardDex.get(i).getType().equals("ace") || CardDex.get(i).isRare() != rare) {
            i = rnd.nextInt(0, CardDex.size());
        }
        return CardDex.get(i).copy();
    }

    public static ShallowCard getRandomBoost() {
        Random rnd = new Random();
        int i = rnd.nextInt(0, CardDex.size());
        while (!CardDex.get(i).getType().equals("boost")) {
            i = rnd.nextInt(0, CardDex.size());
        }
        return CardDex.get(i).copy();
    }
}
