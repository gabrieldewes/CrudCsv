import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Dewes on 09/11/2017.
 */
public class Crud {

    private static final String DATA_PATH = "data/";
    private static final String USERDATA = DATA_PATH + "userdata.csv";

    private static void init() {
        File file = new File(DATA_PATH);
        if ( !file.exists() && file.mkdirs()) {
            System.out.println("[init] Created path = " + file.getAbsolutePath());
            bufferedWrite("id,nome");
        }
    }

    private static List<String[]> listAll() {
        List<String[]> lines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(USERDATA))) {
            stream.forEach( (line) -> {
                System.out.println("[listAll] [stream] Line = " + line);
                lines.add(line.split(","));
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lines;
    }

    private static void write(String... lines) {
        write(Arrays.asList(lines));
    }

    private static void write(List<String> lines) {
        try {
            Files.write(Paths.get(USERDATA), lines, StandardOpenOption.APPEND);
            System.out.println("[write] Writing lines = " + lines.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void bufferedWrite(String... lines) {
        try (FileWriter fileWriter = new FileWriter(USERDATA, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)
        ) {
            for(String line : lines) {
                printWriter.println(line);
            }
            System.out.println("[bufferedWrite] Writing lines = " + Arrays.toString(lines));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void replaceWith(int id, String content) {
        Path path = Paths.get(USERDATA);
        boolean found = false;
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (int i=0; i<lines.size(); i++) {
                String[] old = lines.get(i).split(",");
                if ( !old[0].equals("id") && Integer.valueOf(old[0]).equals(id)) {
                    found = true;
                    String newContent = old[0].concat(",").concat(content);
                    lines.set(i, newContent);
                    Files.write(path, lines, StandardCharsets.UTF_8);
                    System.out.println("[replaceWith] Content changed from '" + old[1] + "' to '" + content + "'");
                    break;
                }
            }
            if (!found) {
                System.err.println("[replaceWith] Line not found with id = " + id);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void remove(int id) {
        Path path = Paths.get(USERDATA);
        boolean found = false;
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (int i=0; i<lines.size(); i++) {
                String[] old = lines.get(i).split(",");
                if ( !old[0].equals("id") && Integer.valueOf(old[0]).equals(id)) {
                    found = true;
                    lines.remove(i);
                    Files.write(path, lines, StandardCharsets.UTF_8);
                    System.out.println("[remove] Removed line = " + Arrays.toString(old));
                    break;
                }
            }
            if (!found) {
                System.err.println("[remove] Line not found with id = " + id);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void bubbleSort(String[][] lines) {
        for (int i=lines.length; i>1; i--) {
            for (int j=2; j<i; j++) {
                if (Integer.valueOf(lines[j - 1][0]) < Integer.valueOf(lines[j][0])) {
                    String[] aux = lines[j];
                    lines[j] = lines[j - 1];
                    lines[j - 1] = aux;
                }
            }
        }
    }

    private static void bubbleSort(List<String[]> lines) {
        String[][] list = new String[lines.size()][1];
        lines.forEach( (line) -> list[lines.indexOf(line)] = line);
        bubbleSort(list);
        lines.clear();
        lines.addAll(Arrays.asList(list));
    }


    public static void main(String[] args) {
        init();

        bufferedWrite(
            "0,Fernando",
            "1,Juliana",
            "2,Caju");

        write(
            "3,Dewes",
            "4,Dewes",
            "5,Dewes");

        replaceWith(1, "Dewes");
        replaceWith(0, "Dewes");
        replaceWith(2, "Dewes");
        replaceWith(6, "Err");

        List<String[]> list = listAll();

        bubbleSort(list);

        list.forEach( (line) -> System.out.println("[main] [bubbleSort desc] " + Arrays.toString(line)));

        remove(0);
        remove(1);
        remove(2);
        remove(5);
        remove(4);
        remove(3);
        remove(6);
    }

}
