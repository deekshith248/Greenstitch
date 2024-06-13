//please consider ParkingTicketSystemV2
//ParkingTicketSystem considers all the cars(including those cars that left the parkinglot)
//ParkingTicketSystemV2 consider only those cars that are currently present in the parkinglot
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ParkingLot{
    private final int capacity;
    private TreeSet<Integer> slots;
    private HashMap<Integer, String> status;
    private HashMap<String, HashSet<Integer>> slotsOfCar; //stores all the slots where a given car is parked based on register number plate
    private HashMap<String, HashSet<String>> carsOfColor;  //stores all cars register no of a given color
    private HashMap<String, HashSet<Integer>> slotsOfColor;  //stores all slots againsr a given color

    public ParkingLot(int capacity){
        this.capacity = capacity;
        this.slots = IntStream.rangeClosed(1, capacity+1)
                .boxed()
                .collect(Collectors.toCollection(TreeSet::new));
        this.slotsOfCar = new HashMap<>();
        this.carsOfColor = new HashMap<>();
        this.slotsOfColor = new HashMap<>();
        this.status = new HashMap<>();
    }

    private Integer generateslot(){
        Integer slot = slots.first();
        if(slot > capacity) return null;
        return slot;
    }

    public void parkACar(String registerPlateNo, String color){
        Integer slot = generateslot();
        if(slot==null) {
            System.out.println("Sorry, parking lot is full");
            return;
        }
        color = color.toUpperCase();
        slots.remove(slot);
        HashSet<Integer> slotsOfCurCar = slotsOfCar.getOrDefault(registerPlateNo, new HashSet<>());
        slotsOfCurCar.add(slot);
        slotsOfCar.put(registerPlateNo, slotsOfCurCar);
        HashSet<String> carsOfCurColor = carsOfColor.getOrDefault(color, new HashSet<>());
        carsOfCurColor.add(registerPlateNo);
        carsOfColor.put(color, carsOfCurColor);
        HashSet<Integer> slotsOfCurColor = slotsOfColor.getOrDefault(color, new HashSet<>());
        slotsOfCurColor.add(slot);
        slotsOfColor.put(color, slotsOfCurColor);
        status.put(slot, registerPlateNo+"         "+color);
        System.out.println("Allocated slot number: "+slot);
    }

    public void unParkAcar(Integer slot){
        if(!status.containsKey(slot)) {
            System.out.println("the slot is empty one, leaving from empty slot is invalid");
            return;
        }

        status.remove(slot);
        slots.add(slot);
        System.out.println("Slot number "+ slot + " is free");
    }

    public void printStatus(){
        if(status.isEmpty()) {
            System.out.println("All Slots Are Free");
            return;
        }
        System.out.println("Slot     Registration No   Colour");
        for(Integer slot=1; slot<=capacity; slot++){
            if(status.containsKey(slot)) System.out.println(slot+ "   "+status.get(slot));
        }
    }

    public void printNumberPlatesWithColor(String color){
        color = color.toUpperCase();

        if(!carsOfColor.containsKey(color)){
            System.out.println("No Car of This Color is Present in Our Parkinglot");
            return;
        }
        for(String registrationNo : carsOfColor.get(color)) System.out.println(registrationNo);
    }

    public void printSlotsOfCar(String registrationNo){
        if(!slotsOfCar.containsKey(registrationNo)){
            System.out.println("This Car With registrationNo: "+registrationNo+" is never parked at our parkinglot");
            return;
        }
        System.out.println(slotsOfCar.get(registrationNo));
    }

    public void printSlotsWithCarColor(String color){
        color = color.toUpperCase();

        if(!slotsOfColor.containsKey(color)){
            System.out.println("No Car of this Color: " + color +" is parked with us");
            return;
        }
        System.out.println(slotsOfColor.get(color));
    }

    public int getCapacity() {
        return this.capacity;
    }

}


public class ParkingTicketSystem{
    private static final String STATUS = "STATUS";
    private static final String EXIT = "EXIT";
    private static final String PARK = "PARK";
    private static final String INVALID = "INVALID";
    private static final String LEAVE = "LEAVE";
    private static final String REGISTRATION_NUMBERS_FOR_CARS_WITH_COLOUR = "REGISTRATION_NUMBERS_FOR_CARS_WITH_COLOUR";
    private static final String SLOTS_ALLOTED_TO_CARS_WITH_COLOUR = "SLOTS_ALLOTED_TO_CARS_WITH_COLOUR";
    private static final String SLOTS_ALLOTED_TO_CAR_WITH_REGISTRATIONNO = "SLOTS_ALLOTED_TO_CAR_WITH_REGISTRATIONNO";




    public static void main(String[] args){
        System.out.println("Welcome Admin");
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        if(command.toUpperCase().equals(EXIT)) return;
        List<String> cmdParts = trimAndSplitCommand(command);
        while(!isValidCreateParkinglotCmd(cmdParts)){
            System.out.println("Invalid Command Entered: To Create a Parkinglot send a command of pattern: create_parking_lot capacity");
            command = scanner.nextLine();
            if(command.toUpperCase().equals(EXIT)) return;
            cmdParts = trimAndSplitCommand(command);
        }
        ParkingLot parkingLot = new ParkingLot(Integer.valueOf(cmdParts.get(1)));

        boolean expectCommand = true;
        System.out.println("You are in parkinglot of capacity: "+parkingLot.getCapacity());
        while(expectCommand){
            System.out.println("PLEASE ENTER THE COMMAND::");
            command = scanner.nextLine();
            if(command.toUpperCase().equals(EXIT)) return;

            cmdParts = trimAndSplitCommand(command);
            String commandType = getCommandType(cmdParts);
            switch(commandType){
                case PARK:
                    parkingLot.parkACar(cmdParts.get(1), cmdParts.get(2));
                    break;
                case LEAVE:
                    parkingLot.unParkAcar(Integer.valueOf(cmdParts.get(1)));
                    break;
                case STATUS:
                    parkingLot.printStatus();
                    break;
                case REGISTRATION_NUMBERS_FOR_CARS_WITH_COLOUR:
                    parkingLot.printNumberPlatesWithColor(cmdParts.get(1));
                    break;
                case SLOTS_ALLOTED_TO_CARS_WITH_COLOUR:
                    parkingLot.printSlotsWithCarColor(cmdParts.get(1));
                    break;
                case SLOTS_ALLOTED_TO_CAR_WITH_REGISTRATIONNO:
                    parkingLot.printSlotsOfCar(cmdParts.get(1));
                    break;
                case INVALID:
                    System.out.println("INVALID COMMAND ENTERED.YOU ARE IN THE PARKINGLOT,BELOW ARE THE VALID COMMANDS");
                    printValidCommands();
                    break;
            }

        }
    }

    public static void printValidCommands(){
        System.out.println(PARK+" "+"registrationNo "+"colour");
        System.out.println(LEAVE+" slotNo");
        System.out.println(STATUS);
        System.out.println(REGISTRATION_NUMBERS_FOR_CARS_WITH_COLOUR+" colour");
        System.out.println(SLOTS_ALLOTED_TO_CARS_WITH_COLOUR+" colour");
        System.out.println(SLOTS_ALLOTED_TO_CAR_WITH_REGISTRATIONNO+" registrationNo");
    }

    public static String getCommandType(List<String> cmdParts) {
        if (cmdParts.size() == 1) {
            //it could be status
            if (cmdParts.get(0).toUpperCase().equals(STATUS)) return STATUS;
            return INVALID;
        }
        if (cmdParts.size() == 3) {
            //it could be park
            if (cmdParts.get(0).toUpperCase().equals(PARK)) return PARK;
            return INVALID;
        }

        if (cmdParts.size() != 2) return INVALID;
        //if (cmdParts.get(0).toUpperCase().equals(LEAVE) && isValidCapacity(cmdParts.get(1))) return LEAVE;
        if(cmdParts.get(0).toUpperCase().equals(LEAVE)){
            if(isValidCapacity(cmdParts.get(1))) return LEAVE;
            return INVALID;
        }
        switch(cmdParts.get(0).toUpperCase()){
            case REGISTRATION_NUMBERS_FOR_CARS_WITH_COLOUR: return REGISTRATION_NUMBERS_FOR_CARS_WITH_COLOUR;
            case SLOTS_ALLOTED_TO_CARS_WITH_COLOUR: return SLOTS_ALLOTED_TO_CARS_WITH_COLOUR;
            case SLOTS_ALLOTED_TO_CAR_WITH_REGISTRATIONNO: return SLOTS_ALLOTED_TO_CAR_WITH_REGISTRATIONNO;
            default: return INVALID;
        }
    }

    public static boolean isValidCreateParkinglotCmd(List<String> cmdParts){
        if((cmdParts.size()==2)&&(cmdParts.get(0).equals("create_parking_lot")&&(isValidCapacity(cmdParts.get(1))))) return true;
        return false;
    }

    public static boolean isValidCapacity(String capacity){
        if((capacity.charAt(0)<'1')&&(capacity.charAt(0)>'9')) return false;
        int len = capacity.length();
        if(len>8) return false;
        for(int idx=1; idx<len; idx++)
            if((capacity.charAt(idx)<'0')&&(capacity.charAt(idx)>'9')) return false;
        return true;
    }

    public static List<String> trimAndSplitCommand(String cmd){
        List<String> cmdParts = new ArrayList<>();
        String cmdPart = "";
        int len = cmd.length();
        for(int idx=0; idx<len; idx++){
            if(cmd.charAt(idx)==' '){
                if(!cmdPart.isEmpty()) cmdParts.add(cmdPart);
                cmdPart = "";
            }else{
                cmdPart += cmd.charAt(idx);
            }
        }
        if(!cmdPart.isEmpty()) cmdParts.add(cmdPart);
        return cmdParts;
    }
}








//below is the demo output


// Welcome Admin
// create_parking_lot 6
// You are in parkinglot of capacity: 6
// PLEASE ENTER THE COMMAND
// park KA-01-HH-1234 White
// Allocated slot number: 1
// PLEASE ENTER THE COMMAND
// park KA-01-HH-9999 White
// Allocated slot number: 2
// PLEASE ENTER THE COMMAND
// park KA-01-BB-0001 Black
// Allocated slot number: 3
// PLEASE ENTER THE COMMAND
// park KA-01-HH-7777 Red
// Allocated slot number: 4
// PLEASE ENTER THE COMMAND
// park KA-01-HH-2701 Blue
// Allocated slot number: 5
// PLEASE ENTER THE COMMAND
// park KA-01-HH-3141 Black
// Allocated slot number: 6
// PLEASE ENTER THE COMMAND
// leave 4
// Slot number 4 is free
// PLEASE ENTER THE COMMAND
// status
// Slot     Registration No   Colour
// 1   KA-01-HH-1234         WHITE
// 2   KA-01-HH-9999         WHITE
// 3   KA-01-BB-0001         BLACK
// 5   KA-01-HH-2701         BLUE
// 6   KA-01-HH-3141         BLACK
// PLEASE ENTER THE COMMAND
// park KA-01-P-333 White
// Allocated slot number: 4
// PLEASE ENTER THE COMMAND
//  park DL-12-AA-9999 White
// Sorry, parking lot is full
// PLEASE ENTER THE COMMAND
// registration_numbers_for_cars_with_colour White
// KA-01-HH-1234
// KA-01-HH-9999
// KA-01-P-333
// PLEASE ENTER THE COMMAND
// registration_numbers_for_cars_with_colour Whitesds
// No Car of This Color is Present in Our Parkinglot
// PLEASE ENTER THE COMMAND
// SLOTS_ALLOTED_TO_CARS_WITH_COLOUR white
// [1, 2, 4]
// PLEASE ENTER THE COMMAND
// SLOTS_ALLOTED_TO_CAR_WITH_REGISTRATIONNO KA-01-P-333
// [4]
// PLEASE ENTER THE COMMAND
// exit
