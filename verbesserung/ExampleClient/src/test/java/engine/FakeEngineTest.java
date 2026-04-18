package engine;

import org.junit.jupiter.api.Test;

import util.RandomManager;

public class FakeEngineTest {

    @Test
    public void GameStateShowsBothPlayers() {
        RandomManager.setSeed(1774785431801L);
        GameSimulator simulator = new GameSimulator();
        simulator.multiPlayer(null);
    }
    
}


//index = 26, gold position(7,0)
