
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileReader;

public class Main {
    static ArrayList<Travel> list = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static ArrayList<String> comandQueue = new ArrayList<>();


    public static void main(String[] args) {
        String fileName = "db.csv";
        list = loadData(fileName);
        String choise;
        System.out.println("Enter command:");
        loop:
        while (true) {
            choise = sc.nextLine();
            String comand = choise;
            String inf = "";
            if (choise.contains(" ")) {
                String[] choises = choise.split(" ", 2);
                comand = choises[0];
                inf = choises[1];
            }
            switch (comand) {
                case "print":
                    print();
                    break;
                case "edit":
                    edit(inf);
                    break;
                case "add":
                    add(inf);
                    break;
                case "del":
                    del(inf);
                    break;
                case "sort":
                    sort();
                    break;
                case "find":
                    find(inf);
                    break;
                case "avg":
                    avg();
                    break;
                case "exit":
                    break loop;
                default:
                    System.out.println("wrong comand");
            }


        }
        sc.close();


    }

    public static void print() {

        System.out.println("------------------------------------------------------------");
        System.out.printf("%-4s%-21s%-11s%6s%10s %-7s%n", "ID", "City", "Date", "Days", "Price", "Vehicle");
        System.out.println("------------------------------------------------------------");

        try (BufferedReader br = new BufferedReader(new FileReader("db.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                String id = parts[0];
                String city = parts[1];
                String date = parts[2];
                String days = parts[3];
                String price = parts[4];
                String vehicle = parts[5];
                System.out.printf("%-4s%-21s%-11s%6s%10s %-7s%n", id, city, date, days, price, vehicle);

            }
            System.out.println("------------------------------------------------------------");
        } catch (IOException e) {
            System.out.println("Error reading file");
        }

    }

    public static void add(String inf) {
        String[] parts = inf.split(";");
        if (parts.length != 6) {
            System.out.println("wrong field count");
            return;
        }
        String id1 = parts[0].trim();
        String city = parts[1].trim();
        String date = parts[2].trim();
        String daysStr = parts[3].trim();
        String priceStr = parts[4].trim();
        String vehicle = parts[5].trim().toUpperCase();

        // ID

        if (!id1.matches("[0-9]+") || id1.length() != 3) {

            System.out.println("wrong id");
            return;
        }


        for (Travel item : list) {
            if (id1.equals(item.id)) {
                System.out.println("wrong id");
                return;
            }

        }
        // CITY
        String[] cityToUpp = city.split(" ");
        city = "";
        for (int i = 0; i < cityToUpp.length; i++) {
            String output = cityToUpp[i].substring(0, 1).toUpperCase() + cityToUpp[i].substring(1);
            city += output + " ";
        }
        city = city.trim();


        // DATE
        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            System.out.println("wrong date");
            return;
        }
        int day = Integer.parseInt(date.substring(0, 2));
        int month = Integer.parseInt(date.substring(3, 5));

        if (day < 1 || day > 31 || month < 1 || month > 12) {
            System.out.println("wrong date");
            return;
        }

        //DAYS
        int days;
        try {
            days = Integer.parseInt(daysStr);
        } catch (Exception e) {
            System.out.println("wrong day count");
            return;
        }

        //PRICE
        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (Exception e) {
            System.out.println("wrong price");
            return;
        }

        //VEHICLE

        if (!(vehicle.equals("BUS") || vehicle.equals("PLANE") || vehicle.equals("TRAIN") || vehicle.equals("BOAT"))) {
            System.out.println("wrong vehicle");
            return;
        }


        Travel newT = new Travel();
        newT.id = id1;
        newT.city = city;
        newT.date = date;
        newT.days = days;
        newT.price = price;
        newT.vehicle = vehicle;
        list.add(newT);
        saveToFile(list);
        System.out.println("added");


    }

    public static void del(String inf) {
        String id = inf.trim();

        if (!id.matches("[0-9]+") || id.length() != 3) {
            System.out.println("wrong id");
            return;
        }
        int count = 0;
        for (Travel item : list) {
            if (id.equals(item.id)) {
                list.remove(item);
                saveToFile(list);
                System.out.println("deleted");
                count++;
                return;
            }

        }
        if (count == 0) {
            System.out.println("wrong id");
        }


    }

    public static void sort() {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - 1 - i; j++) {
                Travel item = list.get(j);
                Travel item2 = list.get(j + 1);

                String d1 = item.date.substring(6) + item.date.substring(3, 5) + item.date.substring(0, 2);
                String d2 = item2.date.substring(6) + item2.date.substring(3, 5) + item2.date.substring(0, 2);
                if (Integer.parseInt(d1) > Integer.parseInt(d2)) {
                    list.set(j, item2);
                    list.set(j + 1, item);
                }

            }

        }
        System.out.println("sorted");
        saveToFile(list);
    }

    public static void avg() {
        double average = 0;
        for (int i = 0; i < list.size(); i++) {
            Travel item = list.get(i);
            average += item.price;
        }
        System.out.printf("average=%.2f%n", average / list.size());

    }

    public static void find(String inf) {
        String priceStr = inf.trim();
        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (Exception e) {
            System.out.println("wrong price");
            return;

        }
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-4s%-21s%-11s%6s%10s %-7s%n", "ID", "City", "Date", "Days", "Price", "Vehicle");
        System.out.println("------------------------------------------------------------");

        for (Travel item : list) {
            if (item.price < price) {
                System.out.printf("%-4s%-21s%-11s%6s%10s %-7s%n", item.id, item.city, item.date, item.days, item.price, item.vehicle);

            }

        }
        System.out.println("------------------------------------------------------------");


    }

    public static void edit(String inf) {
        String[] parts = inf.split(";");
        if (parts.length != 6) {
            System.out.println("wrong field count");
            return;
        }
        String id1 = parts[0].trim();
        String city = parts[1].trim();
        String date = parts[2].trim();
        String daysStr = parts[3].trim();
        String priceStr = parts[4].trim();
        String vehicle = parts[5].trim().toUpperCase();

        // ID

        if (!id1.matches("[0-9]+") || id1.length() != 3) {
            System.out.println("wrong id");
            return;
        }

        Travel changeT = null;

        for (Travel item : list) {
            if (id1.equals(item.id)) {
                changeT = item;
                break;
            }

        }
        if (changeT == null) {
            System.out.println("wrong id");
            return;
        }
        // CITY
        if (!city.isEmpty()) {
            String[] cityToUpp = city.split(" ");
            city = "";
            for (int i = 0; i < cityToUpp.length; i++) {
                String output = cityToUpp[i].substring(0, 1).toUpperCase() + cityToUpp[i].substring(1);
                city += output + " ";
            }
            changeT.city = city;
        }

        // DATE
        if (!date.isEmpty()) {
            if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) {
                System.out.println("wrong date");
                return;
            }
            int day = Integer.parseInt(date.substring(0, 2));
            int month = Integer.parseInt(date.substring(3, 5));

            if (day < 1 || day > 31 || month < 1 || month > 12) {
                System.out.println("wrong date");
                return;
            }
            changeT.date = date;
        }


        //DAYS
        int days;
        if (!daysStr.isEmpty()) {
            try {
                days = Integer.parseInt(daysStr);
            } catch (Exception e) {
                System.out.println("wrong day count");
                return;
            }
            changeT.days = days;
        }


        //PRICE
        double price;
        if (!priceStr.isEmpty()) {
            try {
                price = Double.parseDouble(priceStr);
            } catch (Exception e) {
                System.out.println("wrong price");
                return;
            }
            changeT.price = price;
        }


        //VEHICLE
        if (!vehicle.isEmpty()) {
            if (!(vehicle.equals("BUS") || vehicle.equals("PLANE") || vehicle.equals("TRAIN") || vehicle.equals("BOAT"))) {
                System.out.println("wrong vehicle");
                return;
            }
            changeT.vehicle = vehicle;
        }
        System.out.println("changed");
        saveToFile(list);

    }

    public static void saveToFile(ArrayList<Travel> list) {
        try (FileWriter writer = new FileWriter("db.csv")) {
            for (Travel item : list) {
                String strToSave = item.toCSVString();
                writer.append(strToSave);
            }

            writer.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Travel> loadData(String fileName) {

        ArrayList<Travel> result = new ArrayList<>();


        try {
            Scanner sc = new Scanner(new FileReader(fileName));
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split(";");
                Travel t1 = new Travel();
                t1.id = parts[0];
                t1.city = parts[1];
                t1.date = parts[2];
                t1.days = Integer.parseInt(parts[3]);
                t1.price = Double.parseDouble(parts[4]);
                t1.vehicle = parts[5];
                result.add(t1);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        }


        return result;
    }


    static class Travel {
        String id;
        String city;
        String date;
        Integer days;
        Double price;
        String vehicle;

        String toCSVString() {
            String rv = "";
            rv = id + ";" + city + ";" + date + ";" + days + ";" + price + ";" + vehicle + "\n";

            return rv;
        }
    }
}