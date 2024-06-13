//please consider ParkingTicketSystemV2
//ParkingTicketSystem considers all the cars(including those cars that left the parkinglot)
//ParkingTicketSystemV2 consider only those cars that are currently present in the parkinglot

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
class Car{
    private String registrationNo;
    private String colour;
    private Integer slot;

    public Car(String registrationNo, String colour, Integer slot){
        this.registrationNo = registrationNo;
        this.colour = colour;
        this.slot = slot;
    }

    public Integer getSlot(){
        return this.slot;
    }

    public void setSlot(Integer slot){
        this.slot = slot;
    }
}
class ParkingLotV2{
    private final int capacity;
    private TreeSet<Integer> slots;
    private HashMap<Integer, String> status;
    private HashMap<String, Car> cars;
    private HashMap<String, HashSet<String>> carsOfColor;  //stores all cars register no of a given color
    private HashMap<String, HashSet<Integer>> slotsOfColor;  //stores all slots againsr a given color

    private final ReadWriteLock statusLock = new ReentrantReadWriteLock();
    private final ReadWriteLock slotsLock = new ReentrantReadWriteLock();
    private final ReadWriteLock carsLock = new ReentrantReadWriteLock();
    private final ReadWriteLock carsOfColorLock = new ReentrantReadWriteLock();
    private final ReadWriteLock slotsOfColorLock = new ReentrantReadWriteLock();
    private final int MAXRETRY = 5;
    private final int SLEEPTIME = 100;

    public ParkingLotV2(int capacity){
        this.capacity = capacity;
        this.slots = IntStream.rangeClosed(1, capacity+1)
                .boxed()
                .collect(Collectors.toCollection(TreeSet::new));
        this.cars = new HashMap<>();
        this.carsOfColor = new HashMap<>();
        this.slotsOfColor = new HashMap<>();
        this.status = new HashMap<>();
    }

    private Integer generateslot(){
        Integer slot = slots.first();
        if(slot > capacity) return null;
        return slot;
    }

    public  void parkACar(String registerPlateNo, String color) throws InterruptedException {
        boolean lockAcquired = acquireAllWriteLocks();
        if(!lockAcquired){
            System.out.println("we are facing heavy laod, please try again later");
            return;
        }
        try{
            if(printSlotOfCar(registerPlateNo)){
                System.out.println("this car is already parked in the parkingLot, The request is invalid");
                return;
            }
            Integer slot = generateslot();
            if(slot==null) {
                System.out.println("Sorry, parking lot is full");
                return;
            }
            color = color.toUpperCase();
            slots.remove(slot);
            cars.put(registerPlateNo, new Car(registerPlateNo, color, slot));
            HashSet<String> carsOfCurColor = carsOfColor.getOrDefault(color, new HashSet<>());
            carsOfCurColor.add(registerPlateNo);
            carsOfColor.put(color, carsOfCurColor);
            HashSet<Integer> slotsOfCurColor = slotsOfColor.getOrDefault(color, new HashSet<>());
            slotsOfCurColor.add(slot);
            slotsOfColor.put(color, slotsOfCurColor);
            status.put(slot, registerPlateNo+"         "+color);
            System.out.println("Allocated slot number: "+slot);
        }finally {
            unlockAllWriteLocks();
        }
    }

    public  void unParkAcar(Integer slot) throws InterruptedException {
        boolean lockAcquired = acquireAllWriteLocks();
        if(!lockAcquired){
            System.out.println("we are facing heavy laod, please try again later");
            return;
        }
        try {
            if(!status.containsKey(slot)) {
                System.out.println("the slot is empty one, leaving from empty slot is invalid");
                return;
            }
            List<String> registrationNoAndColor = ParkingTicketSystemV2.trimAndSplitCommand(status.get(slot));
            String registrationNo = registrationNoAndColor.get(0);
            String color = registrationNoAndColor.get(1);

            Car leavingCar = cars.get(registrationNo);
            leavingCar.setSlot(null);

            HashSet<String> carsOfCurColor = carsOfColor.get(color);
            carsOfCurColor.remove(registrationNo);
            carsOfColor.put(color, carsOfCurColor);

            HashSet<Integer> slotsOfCurColor = slotsOfColor.get(color);
            slotsOfCurColor.remove(slot);
            slotsOfColor.put(color, slotsOfCurColor);

            status.remove(slot);
            slots.add(slot);

            System.out.println("Slot number "+ slot + " is free");
        }finally {
            unlockAllWriteLocks();
        }
    }

    private boolean acquireAllWriteLocks() throws InterruptedException {
        int totalLocks = 5;
        int acquiredlocks = 0;
        int retryCnt=0;
        boolean slotsLockAcq = false;
        boolean statusLockAcq = false;
        boolean carsLockAcq = false;
        boolean carsOfColorLockAcq = false;
        boolean slotsOfColorLockAcq = false;
        while((acquiredlocks<5)&&(retryCnt<MAXRETRY)){
            if(!slotsLockAcq){
                slotsLockAcq = slotsLock.writeLock().tryLock();
                if(slotsLockAcq) acquiredlocks++;
            }

            if(!statusLockAcq){
                statusLockAcq = statusLock.writeLock().tryLock();
                if(statusLockAcq) acquiredlocks++;
            }

            if(!carsLockAcq){
                carsLockAcq = carsLock.writeLock().tryLock();
                if(carsLockAcq) acquiredlocks++;
            }

            if(!carsOfColorLockAcq){
                carsOfColorLockAcq = carsOfColorLock.writeLock().tryLock();
                if(carsOfColorLockAcq) acquiredlocks++;
            }

            if(!slotsOfColorLockAcq){
                slotsOfColorLockAcq = slotsOfColorLock.writeLock().tryLock();
                if(slotsOfColorLockAcq) acquiredlocks++;
            }

            Thread.sleep(SLEEPTIME);
        }
        if(acquiredlocks==5) return true;
        if(slotsLockAcq) slotsLock.writeLock().unlock();
       if (statusLockAcq) statusLock.writeLock().unlock()
        if(carsLockAcq) carsLock.writeLock().unlock();
        if(carsOfColorLockAcq) carsOfColorLock.writeLock().unlock();
        if(slotsOfColorLockAcq) slotsOfColorLock.writeLock().unlock();

        return false;
    }

    private void unlockAllWriteLocks(){
        slotsLock.writeLock().unlock();
        statusLock.writeLock().unlock();
        carsLock.writeLock().unlock();
        carsOfColorLock.writeLock().unlock();
        slotsOfColorLock.writeLock().unlock();
    }

    public void printStatus(){
        statusLock.readLock().lock();
        try{
            if(status.isEmpty()) {
                System.out.println("All Slots Are Free");
                return;
            }
            System.out.println("Slot     Registration No   Colour");
            for(Integer slot=1; slot<=capacity; slot++){
                if(status.containsKey(slot)) System.out.println(slot+ "   "+status.get(slot));
            }
        }finally {
            statusLock.readLock().unlock();
        }
    }

    public void printNumberPlatesWithColor(String color){
        carsOfColorLock.readLock().lock();
        try{
            color = color.toUpperCase();

            if(!carsOfColor.containsKey(color)){
                System.out.println("No Car of This Color is Present in Our Parkinglot");
                return;
            }
            for(String registrationNo : carsOfColor.get(color)) System.out.println(registrationNo);
        }finally {
            carsOfColorLock.readLock().unlock();
        }
    }

    public boolean printSlotOfCar(String registrationNo){
        carsLock.readLock().lock();
        try {
            if(!cars.containsKey(registrationNo)){
                System.out.println("This Car With registrationNo: "+registrationNo+" is never parked at our parkinglot");
                return false;
            }
            if(cars.get(registrationNo).getSlot()==null){
                System.out.println("This Car With registrationNo: "+registrationNo+" is currently not parked at our parkinglot");
                return false;
            }
            System.out.println("current slot of car with registrationNo: "+registrationNo+ " is: "+cars.get(registrationNo).getSlot());
            return true;
        }finally {
            carsLock.readLock().unlock();
        }
    }

    public void printSlotsWithCarColor(String color){
        slotsOfColorLock.readLock().lock();
        try {
            color = color.toUpperCase();
            if(!slotsOfColor.containsKey(color)){
                System.out.println("No Car of this Color: " + color +" is parked with us");
                return;
            }
            System.out.println(slotsOfColor.get(color));
        }finally {
            slotsOfColorLock.readLock().unlock();
        }
    }

    public int getCapacity() {
        return this.capacity;
    }

}


public class ParkingTicketSystemV2{
    private static final String STATUS = "STATUS";
    private static final String EXIT = "EXIT";
    private static final String PARK = "PARK";
    private static final String INVALID = "INVALID";
    private static final String LEAVE = "LEAVE";
    private static final String REGISTRATION_NUMBERS_FOR_CARS_WITH_COLOUR = "REGISTRATION_NUMBERS_FOR_CARS_WITH_COLOUR";
    private static final String SLOTS_ALLOTED_TO_CARS_WITH_COLOUR = "SLOTS_ALLOTED_TO_CARS_WITH_COLOUR";
    private static final String SLOT_ALLOTED_TO_CAR_WITH_REGISTRATIONNO = "SLOT_ALLOTED_TO_CAR_WITH_REGISTRATIONNO";




    public static void main(String[] args) throws InterruptedException {
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
        ParkingLotV2 parkingLot = new ParkingLotV2(Integer.valueOf(cmdParts.get(1)));

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
                case SLOT_ALLOTED_TO_CAR_WITH_REGISTRATIONNO:
                    parkingLot.printSlotOfCar(cmdParts.get(1));
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
        System.out.println(SLOT_ALLOTED_TO_CAR_WITH_REGISTRATIONNO+" registrationNo");
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
            case SLOT_ALLOTED_TO_CAR_WITH_REGISTRATIONNO: return SLOT_ALLOTED_TO_CAR_WITH_REGISTRATIONNO;
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
// park DL-12-AA-9999 White
// Sorry, parking lot is full
// PLEASE ENTER THE COMMAND
// registration_numbers_for_cars_with_colour White
// KA-01-HH-1234
// KA-01-HH-9999
// KA-01-P-333
// PLEASE ENTER THE COMMAND
// exit


// ...Program finished with exit code 0
// Press ENTER to exit console.

