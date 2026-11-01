package main;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import controller.GameController;
import model.EGameMode;
import model.GameSettings;
import model.Model;
import model.StrategyType;
import model.ViewType;
import util.RandomManager;
import view.ConsoleView;
import view.IView;
import view.SwingView;

public class Main {

  // ADDITIONAL TIPS ON THIS MATTER ARE GIVEN THROUGHOUT THE TUTORIAL SESSION!

  /*
   * Below, you can find an example of how to use both required HTTP operations,
   * i.e., POST and GET to communicate with the server.
   *
   * Note, this is only an example. Hence, your own implementation should NOT
   * place all the logic in a single main method!
   *
   * Further, I would recommend that you check out: a) The JavaDoc of the network
   * message library, which describes all messages, and their CTORs/methods. You
   * can find it here http://swe1.wst.univie.ac.at/ b) The informal network
   * documentation is given in Moodle, which describes which messages must be used
   * when and how.
   */
  public static void main(String[] args) {
    // RandomManager.setSeed(1773484627773L);
    RandomManager.randomizeSeed();

    setupLogging();

    // if (args.length < 3) {
    // GameSimulator.main(args);
    // } else {
    // ClientMain.main(args);
    // }

    GameSettings settings = parseArguments(args);

    Model model = new Model();
    model.setSettings(settings);

    IView view = switch (settings.getViewType()) {
      case SWING -> new SwingView();
      case CONSOLE -> new ConsoleView();
    };

    GameController controller = new GameController(model, view);
    if (view instanceof SwingView swingView) {
      swingView.setController(controller);
    }
    controller.startSession();
    // LockSupport.park();
  }

  private static GameSettings parseArguments(String[] args) {
    if (args.length < 3) {
      return GameSettings.getDefaultSettings();
    }
    System.out.println(args[0]);
    System.out.println(args[1]);
    System.out.println(args[2]);
    return new GameSettings("kostarievd00", EGameMode.ONLINE, StrategyType.PLANNED_TOUR, null, args[1], args[2], 400,
        null,
        ViewType.CONSOLE);
  }

  private static void setupLogging() {
    Logger root = Logger.getLogger("");
    root.setLevel(Level.INFO);

    for (var h : root.getHandlers()) {
      h.setLevel(Level.FINE);

      h.setFormatter(
          new Formatter() {
            @Override
            public String format(LogRecord record) {
              String className = record.getSourceClassName();
              String methodName = record.getSourceMethodName();

              return String.format(
                  "%1$tH:%1$tM:%1$tS.%1$tL %2$s [%3$s.%4$s] %5$s%n",
                  record.getMillis(),
                  record.getLevel().getName(),
                  className,
                  methodName,
                  formatMessage(record));
            }
          });
    }

    Logger.getLogger("controller").setLevel(Level.FINE);
    Logger.getLogger("network").setLevel(Level.FINE);
    Logger.getLogger("logic").setLevel(Level.FINE);
    Logger.getLogger("map").setLevel(Level.FINE);
    Logger.getLogger("model").setLevel(Level.FINE);
    Logger.getLogger("view").setLevel(Level.FINE);

    // Swing/AWT — только INFO
    Logger.getLogger("javax.swing").setLevel(Level.INFO);
    Logger.getLogger("java.awt").setLevel(Level.INFO);
  }
}
