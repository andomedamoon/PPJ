import java.io.*;
import java.util.Scanner;
import java.util.Random;

public class S22811 {

    public static void main(String[] args) {

        startLoading();
    }

    public static void startLoading() {
        createTxtFile1();
        try {
            createList("Containers.txt", 15000);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static void createTxtFile1() {
        String[] containers = new String[15000];
        String fileName = "Containers.txt";
        int containerNumber = 1;
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            for (int i = 0; i < containers.length; i++) {
                containers[i] = containerNumber + "," + ContainerGenerator.getContainer().toString();
                writer.write(containers[i]);
                writer.newLine();
                writer.flush();
                containerNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Container createContainerFromTxt(String txtLine) {
        String[] draft = txtLine.split(",");
        int containerNumber = Integer.parseInt(draft[0]);
        String containerType = draft[1];
        int weight = Integer.parseInt(draft[2]);
        String cargoType = draft[3];


        return new Container(containerNumber, containerType, weight, cargoType);
    }


    private static void createList(String fileName, int containersNumber) throws FileNotFoundException {
        Container[] list = new Container[containersNumber];

        try (Scanner scanner = new Scanner(new File(fileName))) {
            for (int i = 0; i < containersNumber; i++) {
                String txtLine = scanner.nextLine();
                list[i] = createContainerFromTxt(txtLine);
            }
        }
        Descend(list);

        createTxtFile2(list);
    }


    private static void Descend(Container[] container) {

        for (int unsorted = 1; unsorted < container.length; ++unsorted) {
            Container temp = container[unsorted];
            int loc = unsorted;
            while ((loc > 0) && (container[loc-1].compareWeight(temp) < 0)) {
                container[loc] = container[loc-1];
                loc--;
            }
            container[loc] = temp;
        }
    }


    private static void createTxtFile2(Container[] container) {


        int h = 0;
        int k = 0;

        int weight1 = 0;
        int weight2 = 0;

        /*
        Dalsza część kodu ma umożliwić posortowanie każdego z kontenerów tak, aby ich załadnek
        odbywał się według wartości wagi, jednak nie w sposób całkowicie liniowy.
        Kod ma za zadanie umożliwienie zapełnienia dwóch tablic. Jedną obiektami o indeksach parzystych,
        a drugą obiektami o indeksach nieparzystych. Biorąc pod uwagę, że wcześniej obiekty zostały
        posortowane według malejącej masy, umożliwi to nam równomierne rozłożenie kontenerów po obu
        stronach osi pionowej statku, od dziobu aż po rufę.
        Jak widać w pliku Manifest.txt kontenery są posortowane zgodnie z ciągiem "cięższy, lżejszy, cięższy".

        Program ma za zadanie ładować kontenery od strony dzioba, od jego najniższego poziomu zgodie z malejącą
        wagą (najcięższe kontenery idą na samo dno ładowni). Zakładamy przy tym, że przód statku musi być
        obciążony mocniej ze względu na to, że mniej więcej na długośći 2/3 całego statku znajduje się mostek
        dowodzący, a tuż za nim system napędowy, który znacznie obciąża tył kontenerowca MS Emma Maersk.

        Posłużenie się w tym przypadku dwoma tablicami miało też na celu niejako podzielenie kontenerowca na pół
        wzdłuż osi pionowej w celu zweryfikowania czy statek po załadowaniu nie będzie nadto obciążony po jednej ze
        stron. Jeśli różnica między dwoma połowami okrętu wynosi mniej niż 3 tony (czyli 3000 kg) to masa kontenerowca
        jest rozłożona w sposób prawidłowy.

        Kontenery są ładowane poziomami od dołu (od 1 do 15). 15 poziomów razy 40 rzędów razy 25 kolumn daje łącznie
        15000 kontenerów.
        */

        Container[] temp1 = new Container[7500];
        Container[] temp2 = new Container[7500];
        for (int i = 0; i < container.length; i++) {
            if (i % 2 == 0) {
                temp1[h] = container[i];
                weight1 += temp1[h].getWeight();
                h++;
            } else {
                temp2[k] = container[i];
                weight2 += temp2[k].getWeight();
                k++;
            }
        }

        boolean isWeightRight = true;
        boolean isWeightProportionRight = true;


        if ((weight1 + weight2) <= MSEmmaMaersk.getDWT()) {

            if ((weight1 - weight2) < 3000) {


                Container[] containers2 = new Container[15000];
                int m = 0;
                int l = 0;
                for (int i = 0; i < containers2.length; i++) {
                    if (i % 2 == 0) {
                        containers2[i] = temp2[m];
                        m++;
                    } else {
                        containers2[i] = temp1[l];
                        l++;
                    }
                }

                try {
                    int floor = 1;
                    int row = 1;
                    int column = 1;


                    FileWriter fileWriter = new FileWriter("Manifest.txt");
                    BufferedWriter writer = new BufferedWriter(fileWriter);
                    for (Container value : containers2) {

                        if (row > 40) {
                            row = 1;
                            ++floor;
                        }

                        writer.write(value + "\t" + "Floor: " + floor + "\t" + "Row: " + row + "\t" +
                                "Column: " + column);
                        writer.newLine();
                        writer.flush();
                        column++;

                        if (column > 25) {
                            column = 1;
                            row++;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                isWeightProportionRight = false;
            }
        } else {
            isWeightRight = false;
        }

        if (isWeightProportionRight && !isWeightRight) {
            System.out.println("Ladunek jest za cieżki!\n" +
                    "Załaduj program jeszcze raz.");
        } else if (!isWeightProportionRight && isWeightRight) {
            System.out.println("Rozłożenie wagi w osi pionowej statku jest niepoprawne!\n" +
                    "Załaduj program jeszcze raz.");
        } else if ((!isWeightProportionRight && !isWeightRight)) {
            System.out.println("Masa ładunku jest za duża," +
                    " a jego rozłożene względem osi pionowej statku niepoprawne!\n" +
                    "Załaduj program jeszcze raz.");
        }

        if (isWeightProportionRight && isWeightRight) {
            System.out.println("Masa ładunku jest prawidłowa.\n" +
                    "Rozkład ładunku jest prawidłowy.\n" +
                    "Plik o nazwie Manifest.txt został wygenerowany.");
        }
    }
}

class MSEmmaMaersk {

    private final double LENGTH = 397.7;
    private final double WIDTH = 56.6;
    private final double HEIGHT = 30.0;
    private static final int DWT = 36_000_000;

    public static int getDWT() {
        return DWT;
    }


    /*
    Program został stworzony na bazie kontenerowca MS Emma Mærsk.
    Jego pojemność wynosi około 15000 kontenerów 20 stopowych.
     */

}

class Container extends MSEmmaMaersk {

    private int containerID;
    private final String containerType;
    private String cargoType;
    private double lenght;
    private double width;
    private double height;
    private int weight;
    private Container[] containers;

    public Container(String containerType, String cargoType, double lenght, double width, double height) {
        this.containerType = containerType;
        this.cargoType = cargoType;
        this.lenght = lenght;
        this.width = width;
        this.height = height;
    }

    public Container(int containerNumber ,String containerType, int weight, String cargoType) {
        this.containerID = containerNumber;
        this.containerType = containerType;
        this.weight = weight;
        this.cargoType = cargoType;
    }


    public String getContainerType() {
        return containerType;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public int getWeight() {
        return weight;
    }


    public int compareWeight(Container c) {
        int weight = c.getWeight();
        if(this.weight < weight) {
            return -1;
        } else if(this.weight == weight) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {

        return String.format("Container ID: %5d\tType: %15s\tWeight(kg): %4d\tCargo: %12s",
                containerID, containerType, weight, cargoType);
    }
}

class ContainerGenerator {

    public static Container getContainer() {
        Random rand = new Random();
        int value = rand.nextInt(6);
        Container container;

        switch (value) {
            case 0:
                container = new Standard20();
                break;
            case 1:
                container = new Reefer20();
                break;
            case 2:
                container = new FuelContainer20();
                break;
            case 3:
                container = new GasContainer20();
                break;
            case 4:
                container = new OilContainer20();
                break;
            case 5:
                container = new OpenSide20();
                break;
            default:
                container = null;
        }
        return container;
    }

}

class Standard20 extends Container {

    private int weight;

    Random rand = new Random();


    public Standard20() {
        super("Standard20,", setCargoType(),5.900,
                2.350, 2.393);
        this.weight = rand.nextInt(2230) + 1;
    }


    public int getWeight() {
        return weight;
    }

    public static String setCargoType() {
        int temp = (int) (Math.random() * 17);
        if (temp == 1) {
            return "Wood";
        } else if (temp == 2) {
            return "Grout";
        } else if (temp == 3) {
            return "Glass";
        } else if (temp == 4) {
            return "Iron";
        } else if (temp == 5) {
            return "Plastic";
        } else if (temp == 6) {
            return "Lignite";
        } else if (temp == 7) {
            return "Coal";
        } else if (temp == 8){
            return "Peat";
        } else if (temp == 9) {
            return "Bikes";
        } else if (temp == 10) {
            return "Engines";
        } else if (temp == 11) {
            return "Toys";
        } else if (temp == 12) {
            return "Computers";
        } else if (temp == 13) {
            return "Clothes";
        } else if (temp == 14) {
            return "Furniture";
        } else if (temp == 15) {
            return "Building";
        } else if (temp == 16) {
            return "Garden";
        } else {
            return "Electronics";
        }
    }

    @Override
    public String toString() {
        return getContainerType() + weight + "," + getCargoType();
    }

}

class TankContainer20 extends Container {

    private int weight;

    Random rand = new Random();

    public TankContainer20() {
        super("TankContainer20,", null, 5.864,
                2.311, 2.354);
        this.weight = rand.nextInt(2400) + 1;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return getContainerType() + weight + "," + getCargoType();
    }
}

class FuelContainer20 extends TankContainer20 {

    private int weight;

    Random rand = new Random();

    public FuelContainer20() {
        super();
        this.setCargoType("Fuel,");
        this.weight = rand.nextInt(2400) + 1;
    }

    @Override
    public String toString() {
        return getContainerType() + weight + "," + getCargoType();
    }
}

class GasContainer20 extends TankContainer20 {

    private int weight;

    Random rand = new Random();

    public GasContainer20() {
        super();
        this.setCargoType("Gas,");
        this.weight = rand.nextInt(2400) + 1;
    }

    @Override
    public String toString() {
        return getContainerType() + weight + "," + getCargoType();
    }
}

class OilContainer20 extends TankContainer20 {

    private int weight;

    Random rand = new Random();

    public OilContainer20() {
        super();
        this.setCargoType("Oil,");
        this.weight = rand.nextInt(2400) + 1;
    }

    @Override
    public String toString() {
        return getContainerType() + weight + "," + getCargoType();
    }

}

class Reefer20 extends Container {

    private int weight;

    Random rand = new Random();

    public Reefer20() {
        super("Reefer20,", setCargoType(), 5.452,
                2.275, 2.260);
        this.weight = rand.nextInt(3200) + 961;
    }

    public static String setCargoType() {
        int temp = (int) (Math.random() * 8);

        if (temp == 1) {
            return "Flowers";
        } else if (temp == 2) {
            return "Medicines";
        } else if (temp == 3) {
            return "Fruits";
        } else if (temp == 4) {
            return "Fish-Food";
        } else if (temp == 5) {
            return "Animals";
        } else if (temp == 6) {
            return "Liquids";
        } else if (temp == 7) {
            return "Preparations";
        } else {
            return "Caustic";
        }
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return getContainerType() + weight + "," + getCargoType();
    }
}

class OpenSide20 extends Container {

    private int weight;

    Random rand = new Random();


    public OpenSide20() {
        super("OpenSide20,", "Nonstandard,",
                5.620, 2.200, 0);
        this.weight = rand.nextInt(2300) + 1;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return getContainerType() + weight + "," + getCargoType();
    }
}

