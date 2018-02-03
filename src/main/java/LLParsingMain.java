import java.io.*;

public class LLParsingMain {
    public static void main(String[] args) {

        if (args.length < 1 || args.length > 2) {
            System.out.println("Bitte geben Sie zwei Parameter (Eingabe- und Ausgabedatei) an.");
            return;
        }
        File sourceFile = new File(args[0]);
        if (!(sourceFile.isFile() || sourceFile.exists())) {
            System.out.println("Keine gültige Eingabedatei.");
            return;
        }

        File targetFile = null;

        if (args.length == 2) {
            targetFile = new File(args[1]);
            if (!(targetFile.isFile() || targetFile.exists())) {
                System.out.println("Keine gültige Ausgabedatei.");
                return;
            }

        }
        Operator operator = Operator.create(sourceFile.getAbsolutePath());
        String parsingTable = operator.createParsingTable();
        if (targetFile == null) {
            targetFile = new File("out.txt");
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        PrintWriter pWriter = null;
        try {
            pWriter = new PrintWriter(new BufferedWriter(new FileWriter(targetFile.getAbsolutePath())));
            pWriter.println(parsingTable);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (pWriter != null){
                pWriter.flush();
                pWriter.close();
            }
        }
        System.out.println("Fertig.");
    }

}
