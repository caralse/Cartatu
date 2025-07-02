package game.states.menu;

import com.syndria.math.Vector;
import com.syndria.state.State;
import com.syndria.ui.*;
import game.Palette;
import game.entities.Button;
import game.entities.User;
import game.states.online.OnlineMatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Connect extends State {

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    private final Menu menu;

    private final FixedContainer baseContainer;

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    private String IP;
    private int PORT;

    public Connect(String label, Menu menu){
        this.label = label;
        this.menu = menu;

        IP = "95.239.208.131";
        PORT = 6510;

        baseContainer = new FixedContainer();
        baseContainer.setBgColor(Palette.BLUE);
        baseContainer.setBgVisible(true);

        Button setIPButton = new Button("SERVER", 12);
        setIPButton.setSize(120, 32);
        setIPButton.setMargin(new Spacing(0, 10));
        setIPButton.onClick(() -> {
            menu.getStates().push(new SetIP());
            menu.getAudioManager().play("clickButton");
        });

        baseContainer.add(setIPButton, Alignment.inLineVertical(), false);

        Button searchBattle = new Button("BATTLE", 12);
        searchBattle.setSize(120, 32);
        searchBattle.setColor(Palette.GOLD);
        searchBattle.setMargin(new Spacing(0, 10));
        searchBattle.onClick(() -> {
            try {
                // Wherever you're pushing the MatchMaking state
                menu.getStates().push(new MatchMaking());
            } catch (Throwable t) {
                System.err.println("Failed to create MatchMaking state:");
                t.printStackTrace();
            }
            menu.getAudioManager().play("clickButton");
        });

        baseContainer.add(searchBattle, Alignment.inLineVertical(), false);

        Button goBack = new Button("INDIETRO", 12);
        goBack.setSize(120, 32);
        goBack.setMargin(new Spacing(0, 10));
        goBack.onClick(() -> {
            menu.getStates().setCurrentState("launcher");
            menu.getAudioManager().play("clickButton");
        });

        baseContainer.add(goBack, Alignment.inLineVertical(), false);
    }

    @Override
    public void enter() {

    }

    @Override
    public void draw(double alpha) {
        baseContainer.draw(alpha);
    }

    @Override
    public void update(double dt) {
        baseContainer.update(dt);
    }

    @Override
    public boolean blocksWhenPushed() {
        return false;
    }

    class SetIP extends State {
        private final FixedContainer base;

        public SetIP() {
            base = new FixedContainer();
            FixedContainer inputContainer = new FixedContainer();
            inputContainer.setRelativeDimensions(.6, .6);
            inputContainer.setBgVisible(true);
            inputContainer.setBgColor(Palette.BRONZE);
            base.add(inputContainer, Alignment.centerAbsolute(), true);

            TextInput IPInput = new TextInput(18, new Vector(0, 0), Palette.LILLE);
            IPInput.setMaxLength(15);
            IPInput.setMargin(new Spacing(10));
            IPInput.setRelativeDimensions(.6, .1);

            inputContainer.add(IPInput, Alignment.inLineVertical(), true);

            Button confirmButton = new Button("CONFERMA", 12);
            confirmButton.setSize(120, 32);
            confirmButton.setMargin(new Spacing(10));
            confirmButton.onClick(() -> {
                if (IPInput.getText().matches("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$")) {
                    setIP(IPInput.getText());
                    menu.getAudioManager().play("clickButton");
                    menu.getStates().pop();
                } else {
                    menu.getAudioManager().play("impossible");
                }
            });

            inputContainer.add(confirmButton, Alignment.inLineVertical(), false);

            Button goBack = new Button("ANNULLA", 12);
            goBack.setSize(120, 32);
            goBack.setMargin(new Spacing(10));
            goBack.onClick(() -> {
                menu.getStates().pop();
                menu.getAudioManager().play("clickButton");
            });

            inputContainer.add(goBack, Alignment.inLineVertical(), false);
        }

        private void setIP(String ip) {
            IP = ip;
        }

        @Override
        public void enter() {}

        @Override
        public void draw(double alpha) {
            base.draw(alpha);
        }

        @Override
        public void update(double dt) {
            base.update(dt);
        }

        @Override
        public boolean blocksWhenPushed() {
            return true;
        }
    }

    class MatchMaking extends State {
        private final FixedContainer base;

        private Vector errorPosition;

        private Thread listeningThread;
        private volatile boolean listening = true;

        private final ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>();

        public MatchMaking() {
            base = new FixedContainer();
            FixedContainer container = new FixedContainer();
            container.setRelativeDimensions(.6, .6);
            container.setBgVisible(true);
            container.setBgColor(Palette.BRONZE);
            base.add(container, Alignment.centerAbsolute(), true);
            errorPosition = container.getPosition().copy();

            TextBox queue = new TextBox(".:In coda per un match:.", 24, "Pixellari", Palette.PEACH);
            queue.setMargin(new Spacing(0, 5));
            TextBox wait = new TextBox("...Attendere...", 24, "Pixellari", Palette.PEACH);

            container.add(queue, Alignment.inLineVertical(), false);
            container.add(wait, Alignment.inLineVertical(), false);

            Button goBack = new Button("ANNULLA", 12);
            goBack.setColor(Palette.MAGENTA);
            goBack.setSize(120, 32);
            goBack.setMargin(new Spacing(0, 20));
            goBack.onClick(() -> {
                menu.getStates().pop();
                menu.getAudioManager().play("clickButton");
                stopListening();
                try {
                    out.write("RESET");
                    out.close();
                    in.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            container.add(goBack, Alignment.inLineVertical(), false);
        }

        @Override
        public void enter() {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(IP, PORT), 5000);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                //out.println("SET_USERNAME " + User.getInstance().getUsername());
                // Wait for server's response
                out.println("SET_USERNAME test");
                out.flush();
                String response = in.readLine();

                // Only send LOOK_FOR_MATCH if username was accepted
                if (response != null && response.contains("WELCOME")) {
                    out.println("LOOK_FOR_MATCH");
                    out.flush();
                    startListening();
                }

            } catch (Throwable e) {
                FixedContainer container = new FixedContainer();
                container.setRelativeDimensions(.6, .3);
                container.setBgVisible(true);
                container.setBgColor(Palette.BRONZE);
                container.setPosition(errorPosition);
                base.add(container, Alignment.none(), true);

                TextBox error = new TextBox(".:Errore Server:.", 24, "Pixellari", Palette.MAGENTA);

                container.add(error, Alignment.centerAbsolute(), false);
                System.err.println("Connection error: " + e.getMessage());
            }
        }

        @Override
        public void draw(double alpha) {
            base.draw(alpha);
        }

        @Override
        public void update(double dt) {
            base.update(dt);

            // Process all messages in the queue
            String message;
            while ((message = messageQueue.poll()) != null) {
                if (message.startsWith("MATCH_FOUND")) {
                    out.println("MATCH_FOUND_ACK");
                    out.flush();
                    // Match found logic
                    OnlineMatch onMatch = new OnlineMatch("onlinebattle", menu.getGameStates(), menu, socket, in, out);
                    menu.getGameStates().add(onMatch);
                    menu.getStates().setCurrentState("launcher");
                    stopListening();
                    menu.getGameStates().switchTo("onlinebattle");
                    menu.getStates().pop();
                    break;
                }
            }
        }

        private void startListening() {
            listeningThread = new Thread(() -> {
                try {
                    while (listening) {
                        String message = in.readLine();
                        if (message != null) {
                            messageQueue.offer(message);  // Add to queue instead of single variable
                        }
                    }
                } catch (IOException e) {
                    if (listening) {
                        System.err.println("Error reading from server: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            listeningThread.start();
        }

        private void stopListening() {
            listening = false;
        }


        @Override
        public boolean blocksWhenPushed() {
            return true;
        }
    }
}
