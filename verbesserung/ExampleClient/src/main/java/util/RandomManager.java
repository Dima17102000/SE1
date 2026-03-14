package util;


import java.util.Random;

public class RandomManager {

    private static Random random;
    private static Long currentSeed;
    private static boolean printSeed = true;

    static {
        randomizeSeed();
    }

    public static void randomizeSeed() {

        currentSeed = System.currentTimeMillis();
        random = new Random(currentSeed);
        if(printSeed){
            System.out.println("[RandomManager] New random seed: " + currentSeed);
        }
    }

    public static void setSeed(long seed) {
        currentSeed = seed;
        random = new Random(seed);
        if (printSeed) {
            System.out.println("[RandomManager] Seed fixed to: " + seed);
        }
    }


    public static Random getRandom() {

        if (random == null) {
            randomizeSeed();
        }
        return random;
    }

}
