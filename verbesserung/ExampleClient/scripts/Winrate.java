import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Winrate {

    enum GameResult {
        P1_WIN,P2_WIN,PARSE_ERROR;
    }
    private static GameResult process(Path filepath) {
    //   //body of the for loop
        try {   
            String res1 = Files.lines(filepath)
            .map(String::trim)
            .filter(s->s.contains("WON!"))
            .findFirst()
            .orElse("");

            if(res1.endsWith("Player 1 WON!"))
            {
                return GameResult.P1_WIN;
            } else if (res1.endsWith("Player 2 WON!")){
                return GameResult.P2_WIN;
            }
            else {
                return GameResult.PARSE_ERROR;
            }
        }
        catch(IOException e){
            e.printStackTrace();
            return GameResult.PARSE_ERROR;
        }
    }

   public static void main(String[] args) throws IOException 
   {
        String stringpath = "logs/logs_algo5";
        Path path = new File(stringpath).toPath();
        List<GameResult> results = Files.walk(path)
            .filter(Files::isRegularFile)
            .filter(f->f.toString().endsWith(".txt"))
            .map(Winrate::process)
            .collect(Collectors.toList());
        
        long P1_WIN = results.stream().filter(r->r==GameResult.P1_WIN).count();
        long P2_WIN = results.stream().filter(r->r==GameResult.P2_WIN).count();
        long PARSE_ERROR = results.stream().filter(r->r==GameResult.PARSE_ERROR).count();
        long total = P1_WIN  + P2_WIN;
        double WinRate = ((double)P1_WIN /total)*100;
        double LossRate = ((double)P2_WIN /total)*100;
        DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
        System.out.println("Wins: " + df.format(WinRate));
        System.out.println("Losses: " + df.format(LossRate));
        System.out.println("Parse Errors: " + PARSE_ERROR);    
   }
}