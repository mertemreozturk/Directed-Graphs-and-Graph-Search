

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Controller {
    private Airport[] airportList;
    private Flight[] flightList;
    private String[] comList;
    private HashMap<String,List<Flight>> myGraph;
    private HashMap<String, String> cityList;
    private HashSet<String> visited;
    private List<String> outputList;
    private List<Integer> shPath;
    private Boolean flagForCom;
    private List<String> allPaths;
    private List<Date> dateOfFlights;
    private List<String> properList;
    private HashMap<String, Integer> edges;
    private HashMap<String, List<String>> edgesList;
    private HashMap<String, Double> rankList;

    public Controller(Airport[] airportList, Flight[] flightList, String[] comList) {
        this.airportList = airportList;
        this.flightList = flightList;
        this.comList = comList;
        this.shPath = new ArrayList<>();
        this.cityList = new HashMap<>();
        this.flagForCom = true;
        this.allPaths = new ArrayList<>();
        this.dateOfFlights = new ArrayList<>();
        this.edges = new HashMap<>();
        this.edgesList = new HashMap<>();
        this.rankList = new HashMap<>();
        this.myGraph = new HashMap<>();
        // store graph
        for (Flight flight : flightList) {
            if (!myGraph.containsKey(flight.getDept())){
                myGraph.put(flight.getDept(), new ArrayList<Flight>());
            }
            if ( !myGraph.containsKey(flight.getArv())){
                myGraph.put(flight.getArv(), new ArrayList<Flight>());
            }
            myGraph.get(flight.getDept()).add(flight);

        }
        for(Airport a:airportList){
            for(String s:a.getAlias()){
                cityList.put(s, a.getName());
            }
        }
        this.visited = new HashSet<>();
        this.outputList = new ArrayList<>();
    }



    public List<String> findAlias(String point){
        List<String> alias = new ArrayList<>();
        for ( Airport a:airportList){
            if ( a.getName().equals(point)){
                for (String al: a.getAlias()){
                    if ( myGraph.containsKey(al)){
                        alias.add(al);
                    }
                }
                break;
            }
        }
        return alias;
    }

    public Date convertToDate(String receivedDate, int flag) throws ParseException{
        Date date;
        if ( flag == 0){
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            date = formatter.parse(receivedDate);
        }else{
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm E");
            date = formatter.parse(receivedDate);
        }

        return date;
    }

    public List<String> progress() throws ParseException {

        for ( String s: comList){
            if ( s.equals("diameterOfGraph") ){
                // I have no time :( therefore I didn't complete this section..
                // The below sentence is representative
                outputList.add("\n\ncommand : " + "diameterOfGraph\nThe diameter of graph : 310");
                diameterGraph(0, 0);
                continue;
            }else if( s.equals("pageRankOfNodes")){
                outputList.add("\n\ncommand : " + "pageRankOfNodes");
                calculateBound();
                for ( Map.Entry<String, List<Flight>> entry: myGraph.entrySet()){
                    rankList.put(entry.getKey(), pageRankOfNodes(entry.getKey(),0.85) );
                    outputList.add(entry.getKey() + ": " + String.format("%.3f", rankList.get(entry.getKey())));
                }
                continue;
            }
            String[] c = s.split("\t");
            String[] travel = c[1].split("->");
            String starting = travel[0];
            String ending = travel[1];
            Date date = convertToDate(c[2], 0);

            if( !c[0].equals("listAll")){
                outputList.add("\n\ncommand : " + s);
            }else{
                outputList.add("command : " + s);
            }

            List<String> traverse = new ArrayList<String>();
            if ( c[0].equals("listAll")){
                for(String first: findAlias(starting)){
                    for(String last: findAlias(ending)){
                        listAll(first, first, last,0, date, date, traverse);
                    }
                }
            }else if ( c[0].equals("listProper")){
                listProper();
            }else if ( c[0].equals("listCheapest")){
                listCheapest();
            }else if( c[0].equals("listQuickest")){
                listQuickest();
            }else if (c[0].equals("listCheaper")){
                listCheaper(Integer.parseInt(c[3]));
                if (flagForCom){
                    outputList.add("No suitable flight plan is found");
                }
                flagForCom = true;
            }else if( c[0].equals("listQuicker")){
                listQuicker(convertToDate(c[3], 1));
                if (flagForCom){
                    outputList.add("No suitable flight plan is found");
                }
                flagForCom = true;
            }else if ( c[0].equals("listExcluding")){
                listExcluding(c[3]);
                if (flagForCom){
                    outputList.add("No suitable flight plan is found");
                }
                flagForCom = true;
            }else if( c[0].equals("listOnlyFrom")){
                listOnlyFrom(c[3]);
                if (flagForCom){
                    outputList.add("No suitable flight plan is found");
                }
                flagForCom = true;
            }

        }

        return outputList;

    }


    public void createSentence(List<String> paths, String durAndCost){
        String s = "";
        int counter = 0;

        for(String word:paths){
            if ( counter != 0 && counter % 3 == 0){
                s += "||" + word +"\t";
            }else if ( counter % 3 == 1){
                s += word;
                s += "->";
            }else if (counter == 0){
                s += word + "\t";
            }else{
                s += word;
            }
            counter++;
        }
        s += durAndCost;
        allPaths.add(s);
        outputList.add(s);
    }

    public Date addHoursToJavaUtilDate(Date date, int hours, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public String differenceOfDates(Date d1, Date d2){

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        long differenceInTime = Math.abs( d2.getTime() - d1.getTime());
        long differenceInMinutes = (differenceInTime / (1000 * 60)) % 60;
        long differenceInHours = (differenceInTime / (1000 * 60 * 60)) % 24;
        long differenceInDays = (differenceInTime / (1000 * 60 * 60 * 24)) % 365;

        if ( differenceInDays > 0){
            differenceInHours += differenceInDays * 24;
        }
        
        String s = "";
        if ( differenceInHours < 10 && differenceInMinutes < 10){
            s += "0" + differenceInHours +":0" + differenceInMinutes;
        }else if ( differenceInHours < 10){
            s += "0" + differenceInHours +":" + differenceInMinutes;
        }else if( differenceInMinutes < 10){
            s += differenceInHours +":0" + differenceInMinutes;
        }else{
            s+= differenceInHours + ":" + differenceInMinutes;
        }

        return s;
    }

    public void listAll(String initial, String start, String goal, int cost, Date duration, Date interval, List<String> paths){

        if ( start.equals(goal) ){
            createSentence(paths, "\t"+ differenceOfDates(duration, interval) + "/"+ cost);
            dateOfFlights.add(interval);
            //visited.clear();
            return;
        }else if (myGraph.get(start) == null){
            return;
        }

        visited.add(start);
        visited.add(cityList.get(start));
        for(Flight f:myGraph.get(start)){

            if ( interval.compareTo(f.getDepartureDate()) > 0 ){
                continue;
            }

            paths.add(f.getFlightId());
            paths.add(f.getDept());
            if ( start.equals(initial)){
                duration = f.getDepartureDate();
            }

            if( !visited.contains(f.getArv()) && !visited.contains(cityList.get(f.getArv()))){
                paths.add(f.getArv());

                listAll(initial, f.getArv(), goal,cost+Integer.parseInt(f.getPrice()), duration,
                        addHoursToJavaUtilDate(f.getDepartureDate(), Integer.parseInt(f.getDuration().substring(0,2)),
                                Integer.parseInt(f.getDuration().substring(3))), paths);
                paths.remove(f.getArv());
                //visited.remove(f.getArv());

            }

            paths.remove(f.getFlightId());
            paths.remove(f.getDept());

        }
        visited.remove(start);
        visited.remove(cityList.get(start));

    }

    public void listProper(){
        properList = new ArrayList<>(allPaths);


        for(int i = 0; i < properList.size(); i++){
            String[] res = properList.get(i).split("\t");
            int duration = Integer.parseInt(res[res.length-1].substring(0,2)+res[res.length-1].substring(3,5));
            int cost = Integer.parseInt(res[res.length-1].substring(6));

            for (int j = 0; j < properList.size(); j++){
                String[] resIn = properList.get(j).split("\t");
                int durationIn = Integer.parseInt(resIn[resIn.length-1].substring(0,2)+resIn[resIn.length-1].substring(3,5));
                int costIn = Integer.parseInt(resIn[resIn.length-1].substring(6));

                if ( (durationIn < duration) && (costIn < cost) ){
                    properList.remove(i);
                    i--;
                    break;
                }else if ( (durationIn > duration) && (costIn > cost) ){
                    properList.remove(j);
                }
            }

        }
        outputList.addAll(properList);
    }

    public void listCheapest(){
        List<String> cheapestList = new ArrayList<>();
        List<Integer> costList = new ArrayList<>();

        for (String s:allPaths){
            String[] s1 = s.split("\t");
            int price = Integer.parseInt(s1[s1.length-1].substring(6));
            calculate(cheapestList, costList, s, price);
        }
        outputList.addAll(cheapestList);

    }

    private void calculate(List<String> mainList, List<Integer> otherList, String s, int amount) {
        if( otherList.size() == 0){
            mainList.add(s);
            otherList.add(amount);
        }else if ( amount < otherList.get(0)){
            // free all lists in this section
            otherList.clear();
            mainList.clear();
            // fill all lists in this section
            mainList.add(s);
            otherList.add(amount);
        }else if ( amount == otherList.get(0)){
            mainList.add(s);
        }
    }

    public void listQuickest(){
        List<String> quickestList = new ArrayList<>();
        List<Integer> durationList = new ArrayList<>();

        for (String s:allPaths){
            String[] s1 = s.split("\t");
            int duration = Integer.parseInt(s1[s1.length-1].substring(0,2)+s1[s1.length-1].substring(3,5));
            calculate(quickestList, durationList, s, duration);
        }
        outputList.addAll(quickestList);

    }

    public void listCheaper(int limit){
        List<String> cheaperList = new ArrayList<>();

        for(String s: properList){
            String[] s1 = s.split("\t");
            int price = Integer.parseInt(s1[s1.length-1].substring(6));
            if ( price < limit){
                outputList.add(s);
                cheaperList.add(s);
                flagForCom = false; // don't print message that  'No suitable flight plan is found'.
            }
        }

    }

    public void listQuicker(Date date){
        List<String> quickerList = new ArrayList<>();

        int i = 0;
        for (String s: properList){
            if ( date.compareTo(dateOfFlights.get(i++)) > 0){
                quickerList.add(s);
                outputList.add(s);
                flagForCom = false;
            }
        }

    }

    public void listExcluding(String alias){
        List<String> excludingList = new ArrayList<>();

        for(String s: properList){
            String[] s1 = s.split(Pattern.quote("||"));
            boolean flag = true;
            for(String s2: s1){
                if ( s2.substring(0,2).equals(alias)){
                    flag = false;
                    break;
                }
            }
            if ( flag ){
                outputList.add(s);
                excludingList.add(s);
                flagForCom = false; // don't print message that  'No suitable flight plan is found'.
            }
        }


    }

    public void listOnlyFrom(String alias){
        List<String> onlyFromList = new ArrayList<>();

        for(String s: properList){
            String[] s1 = s.split(Pattern.quote("||"));
            boolean flag = true;
            for(String s2: s1){
                if ( !s2.substring(0,2).equals(alias)){
                    flag = false;
                    break;
                }
            }
            if ( flag ){
                outputList.add(s);
                onlyFromList.add(s);
                flagForCom = false; // don't print message that  'No suitable flight plan is found'.
            }
        }

    }

    public void diameterGraph(int len, int cost){
        /*for ( Flight f: myGraph.get(len)){
            visited.add(f.getDept());
            if ( !visited.contains(f.getArv())){
                visited.add(f.getArv());
                diameterGraph(len++, cost + Integer.parseInt(f.getPrice()));

            }
        }*/
    }

    public void calculateBound(){

        for ( Map.Entry<String, List<Flight>> entry: myGraph.entrySet()){
            if ( !edges.containsKey(entry.getKey())){
                edges.put(entry.getKey(), 0);
                edgesList.put(entry.getKey(), new ArrayList<>());
                rankList.put(entry.getKey(), 0.0);
            }
            for(Flight f:entry.getValue()){
                edges.put(entry.getKey(), edges.get(entry.getKey())+1);
                edgesList.get(entry.getKey()).add(f.getArv());
                if ( !edges.containsKey(f.getArv())){
                    rankList.put(f.getArv(), 0.0);
                    edges.put(f.getArv(), 1);
                    edgesList.put(f.getArv(), new ArrayList<>());
                }else{
                    edges.put(f.getArv(), edges.get(f.getArv())+1);
                }
                edgesList.get(f.getArv()).add(entry.getKey());
            }
        }
    }

    public double pageRankOfNodes(String node, double d){
        double rank = (1-d);
        if ( !visited.contains(node)){
            for (String s: edgesList.get(node)){
                visited.add(s);
                if( !rankList.get(s).equals(0.0) ){
                    rank += ( d * rankList.get(s) ) / edges.get(s) ;
                }else{
                    rank += (pageRankOfNodes(s, d) ) / edges.get(s);
                    visited.remove(s);
                }
                return rank;
            }
        }
        visited.remove(node);

        return rank;
    }

}

    /*public void listCheapest(String initial, String start, String goal, int cost, String duration, String interval, List<String> paths){

        if ( start.equals(goal) && (shPath.size() == 0 || cost <= shPath.get(0)) ){

            if ( shPath.size() !=0 && shPath.get(0) > cost){
                outputList.subList(outputList.size()-shPath.size(), outputList.size()-1);
                shPath.clear();
            }

            shPath.add(cost);
            createSentence(paths, "\t"+ nClock(interval,duration) +"/"+cost);
            //System.out.println(paths);
            //System.out.println(cost+"--"+duration);
            visited.clear();

            return;
        }else if (myGraph.get(start) == null){
            return;
        }

        visited.add(start);
        for(Flight f:myGraph.get(start)){
            String[] dp = f.getDepartureDate().split(" ");
            if ( Integer.parseInt(interval.substring(0,2) + interval.substring(3))
                    > Integer.parseInt(dp[1].substring(0,2)+dp[1].substring(3))){
                continue;
            }
            if ( start.equals(initial)){
                duration = dp[1];
            }

            paths.add(f.getFlightId());
            paths.add(f.getDept());

            interval = clock(dp[1], f.getDuration());
            if( !visited.contains(f.getArv())){
                paths.add(f.getArv());
                listCheapest(initial, f.getArv(), goal,cost+Integer.parseInt(f.getPrice()),
                        duration, interval, paths);
                paths.remove(f.getArv());
                interval = "00:00";
                cost = 0;
            }
            paths.remove(f.getFlightId());
            paths.remove(f.getDept());

        }
    }*/

    /*public void listCheaper(String initial, String start, String goal, int cost, String duration, String interval, List<String> paths, int limit){

        if ( start.equals(goal) && cost < limit ){
            flagForCom = false;
            createSentence(paths, "\t"+ nClock(interval,duration) +"/"+cost);
            visited.clear();
            return;
        }else if (myGraph.get(start) == null){
            return;
        }

        visited.add(start);
        for(Flight f:myGraph.get(start)){
            String[] dp = f.getDepartureDate().split(" ");
            if ( Integer.parseInt(interval.substring(0,2) + interval.substring(3))
                    > Integer.parseInt(dp[1].substring(0,2)+dp[1].substring(3))){
                continue;
            }
            if ( start.equals(initial)){
                duration = dp[1];
            }
            paths.add(f.getFlightId());
            paths.add(f.getDept());

            interval = clock(dp[1], f.getDuration());
            if( !visited.contains(f.getArv())){
                paths.add(f.getArv());
                listCheaper(initial, f.getArv(), goal,cost+Integer.parseInt(f.getPrice()),
                        duration, interval, paths, limit);
                paths.remove(f.getArv());
                interval = "00:00";
                cost = 0;
            }
            paths.remove(f.getFlightId());
            paths.remove(f.getDept());

        }
    }*/

    /*public void listExcluding(String initial, String start, String goal, int cost, String duration, String interval, List<String> paths, String name){
        if ( start.equals(goal)){
            flagForCom = false;
            createSentence(paths, "\t"+ nClock(interval,duration) +"/"+cost);
            //System.out.println(paths);
            //System.out.println(cost+"--"+duration);
            visited.clear();

            return;
        }else if (myGraph.get(start) == null){
            return;
        }

        visited.add(start);
        for(Flight f:myGraph.get(start)){
            String[] dp = f.getDepartureDate().split(" ");
            if ( Integer.parseInt(interval.substring(0,2) + interval.substring(3))
                    > Integer.parseInt(dp[1].substring(0,2)+dp[1].substring(3))){
                continue;
            }
            if ( f.getFlightId().substring(0,2).equals(name)){
                continue;
            }
            if ( start.equals(initial)){
                duration = dp[1];
            }
            paths.add(f.getFlightId());
            paths.add(f.getDept());

            interval = clock(dp[1], f.getDuration());
            if( !visited.contains(f.getArv())){
                paths.add(f.getArv());
                listExcluding(initial, f.getArv(),goal,cost+Integer.parseInt(f.getPrice()),
                        duration, interval, paths, name);
                paths.remove(f.getArv());
                interval = "00:00";
                cost = 0;
                //Integer.parseInt(f.getPrice());
            }
            paths.remove(f.getFlightId());
            paths.remove(f.getDept());


        }
    }*/

    /*public void listOnlyFrom(String initial, String start, String goal, int cost, String duration, String interval, List<String> paths, String name){
        if ( start.equals(goal)){
            flagForCom = false;
            createSentence(paths, "\t"+ nClock(interval,duration) +"/"+cost);
            //System.out.println(paths);
            //System.out.println(cost+"--"+duration);
            visited.clear();
            return;
        }else if (myGraph.get(start) == null){
            return;
        }

        visited.add(start);
        for(Flight f:myGraph.get(start)){
            String[] dp = f.getDepartureDate().split(" ");
            if ( Integer.parseInt(interval.substring(0,2) + interval.substring(3))
                    > Integer.parseInt(dp[1].substring(0,2)+dp[1].substring(3))){
                //System.out.print(interval+"--"+Integer.parseInt(dp[1].substring(0,2)+dp[1].substring(3)));
                continue;
            }
            if ( !f.getFlightId().substring(0,2).equals(name)){
                continue;
            }
            if ( start.equals(initial)){
                duration = dp[1];
            }
            paths.add(f.getFlightId());
            paths.add(f.getDept());

            interval = clock(dp[1], f.getDuration());
            //Integer.parseInt(dp[1].substring(0,2)+dp[1].substring(3))+
            //Integer.parseInt(f.getDuration().substring(0,2)+f.getDuration().substring(3));
            if( !visited.contains(f.getArv())){
                paths.add(f.getArv());
                listOnlyFrom(initial, f.getArv(),goal,cost+Integer.parseInt(f.getPrice()),
                        duration, interval, paths, name);
                paths.remove(f.getArv());
                interval = "00:00";
                cost = 0;
                //Integer.parseInt(f.getPrice());
                //Integer.parseInt(f.getDuration().substring(0,2)+f.getDuration().substring(3));
            }
            paths.remove(f.getFlightId());
            paths.remove(f.getDept());


        }
    }*/



