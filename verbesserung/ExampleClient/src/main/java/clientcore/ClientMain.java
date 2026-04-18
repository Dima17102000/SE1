package clientcore;

import java.util.Set;

import logic.GameHelper;
import logic.IStrategy;
import logic.StrategyPlannedTour;
import map.ClientMap;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerMove;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import network.ClientNetwork;
import network.INetwork;
import view.ConsoleView;

public class ClientMain {
    private INetwork net;
    private IStrategy strategy;

    public ClientMain(INetwork network) {
        this.net = network;
        // this.strategy = new StrategyNearestNeighbour();
        this.strategy = new StrategyPlannedTour();
        // this.strategy = new StrategyAlwaysClosest();
        // this.strategy = new StrategyManual();

    }

    public void startGame(String studentId) {
        // ✅ Registrierung
        net.registerPlayer(studentId);

        if (net.getPlayerId() == null) {
            System.err.println("❌ Registrierung fehlgeschlagen, Spiel kann nicht gestartet werden.");
            return;
        }

        String myPlayerId = net.getPlayerId().getUniquePlayerID();

        ClientMap mapGen = new ClientMap(myPlayerId);
        boolean mapSent = false;

        // 🔄 Warten auf Erlaubnis zur HalfMap-Übertragung oder Move-Phase
        while (true) {
            GameState state = net.getGameState();
            boolean canSendMap = false;

            if (state != null) {
                Set<PlayerState> players = state.getPlayers();
                for (PlayerState ps : players) {
                    
                    if (ps.getUniquePlayerID().equals(myPlayerId)) {
                        String status = ps.getState().name();
                        System.out.println("🧍 Spieler-ID: " + myPlayerId);
                        System.out.println("📡 Aktueller Status vom Server: " + status);

                        if (status.equals("MustAct")) {
                            System.out.println("⚠️ Ich bin schon in der Move-Phase!");
                            canSendMap = true; // trotzdem senden, falls noch nicht gesendet
                        } else if (status.equals("Won") || status.equals("Lost")) {
                            // Kein Fehler ausgeben, falls das Spiel beendet wurde
                            System.out.println("🏁 Spiel wurde bereits beendet mit Status: " + status);
                            return;
                        }
                        break;
                    }
                }
            }

            if (canSendMap && !mapSent) {
                System.out.println("📤 Sende HalfMap jetzt an den Server...");
                PlayerHalfMap halfMap = mapGen.generate();
                net.sendHalfMap(halfMap);
                System.out.println("📨 HalfMap wurde an sendHalfMap() übergeben.");
                mapSent = true;
            }

            

            System.out.println("⏳ Warte auf meinen Zug zum Senden der HalfMap...");
            

            if (mapSent) {
                break;
            }
        }

        // 🔁 Danach: Move-Phase starten
        startMovePhase();
    }

    public void startMovePhase() {
        ConsoleView view = new ConsoleView();
        GameHelper gameHelper = new GameHelper(net.getPlayerId());
    
        while (true) {
            GameState state = net.getGameState();
            boolean myTurnToMove = false;

            if (state != null) {
                for (PlayerState ps : state.getPlayers()) {
                    String myPlayerId = net.getPlayerId().getUniquePlayerID();
                    if (ps.getUniquePlayerID().equals(myPlayerId)) {
                        switch (ps.getState()) {
                            case MustAct -> myTurnToMove = true;
                            //case MustWait -> myTurnToMove = false;
                            case Won -> {
                                view.printGameResult(true);
                                return;
                            }
                            case Lost -> {
                                view.printGameResult(false);
                                return;
                            }
                        }
                        break;
                    }
                }
            }
            //System.out.println("The value of variable myTurnTomove = " + myTurnToMove);
            if (myTurnToMove) {
                gameHelper.update(state);
                view.render(gameHelper);  // 🗺️ Konsolenkarte ausgeben
                long t0 = System.nanoTime();
                PlayerMove move = strategy.calculateNextMove(gameHelper);
                long t1 = System.nanoTime();
                double dt1_0 = (t1 - t0) / 1000000;
                System.out.println("Execution time of function calculateNextMove takes in ms = " + dt1_0);
                try {
                    net.sendMove(move);
                    long t2 = System.nanoTime();
                    double dt2_1 = (t2 - t1) / 1000000;
                    System.out.println("Execution time of function sendMove takes in ms = " + dt2_1);
                }
                catch (Exception e) {
                    long t2 = System.nanoTime();
                    double dt2_1 = (t2 - t1) / 1000000;
                    System.out.println("Execution time of function sendMove takes in ms = " + dt2_1);
                    throw e;
                }
            } else {
                System.out.println("⏳ Warte auf meinen Zug...");
            }
    
           
        }
    }
    

    public static void main(String[] args) {
        INetwork network;
        String studentId = "kostarievd00"; // 🧑‍🎓 Deinen u:account hier einsetzen


        if (args.length < 3) {
            System.err.println("❗ Missing arguments. Required: [mode] [serverURL] [gameId]");
            return;
        }
        else{
            String gamemode = args[0];
            String serverURL = args[1];
            String gameId = args[2];
            network = new ClientNetwork(serverURL,gameId);
        }
        ClientMain main = new ClientMain(network);
        main.startGame(studentId);
    }
}


// 1 м = 100 см

// 1 м = 1000 мм

// 1 м = 10^9 нм

// 5000 нм = 5000 * 10^-9 м = 5 * 10^-6 м


// 1 с = 10^3 мс


// 1 с = 10^9 нс

