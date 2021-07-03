

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws ParseException, FileNotFoundException {
	    String[] aList = readFile(args[0]);
	    Airport[] airports = storeAirport(aList);
        String[] fList = readFile(args[1]);
        Flight[] flights = storeFlight(fList);
        String[] cList = readFile(args[2]);
        Controller controller = new Controller(airports, flights, cList);
        List<String> output = controller.progress();
        writeToFile(output);

    }

    private static String[] readFile(String path) {
        try {
            int i = 0;
            int length = Files.readAllLines(Paths.get(path)).size();
            String[] results = new String[length];
            for(String line: Files.readAllLines(Paths.get(path))) {
                results[i++] = line;
            }
            return results;
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private static Airport[] storeAirport(String[] l){
        Airport[] airports = new Airport[l.length];
        int i = 0;
        for ( String s:l){
            String[] temp = s.split("\t");
            airports[i++] = new Airport(temp[0], Arrays.stream(temp).skip(1).collect(Collectors.toList()));
        }
        return airports;
    }

    private static Flight[] storeFlight(String[] f) throws ParseException {
        Flight[] flights = new Flight[f.length];
        int i = 0;
        for ( String s:f){
            String[] temp = s.split("\t");
            flights[i++] = new Flight(temp[0], temp[1].substring(0,3), temp[1].substring(5), convertToDate(temp[2]), temp[3], temp[4]);
        }
        return  flights;
    }

    private static Date convertToDate(String receivedDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm E");
        Date date = formatter.parse(receivedDate);
        return date;
    }

    private static void writeToFile(List<String> output) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("output.txt");
        for(String o:output){
            writer.println(o);
        }

        writer.close();
    }
}
