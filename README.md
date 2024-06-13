# greenstitch
ParkingLotAssignment

I have recieved this assignment from Greenstitch.io.The Deadline is 10  FEB 2024 12PM.

The code responds to 7 different commands
  createParkingLot,
  park,
  un-park,
  status,
  carsOfGivenColor,
  SlotsOfGivenColor,
  slot/slots of a given Car




Ambiguity in Assignment:(Consider ParkingTicketSystemV2.java for EVALUATION)
   the Assignment didnt mention if we have to consider all the cars that are currently present in parking lot
       OR
   all the cars(including those which left the parking Lot)

so i have coded:
             ParkingTicketSystem.java(Assuming i have to consider all cars, including those which left the parkinglot)
             ParkingTicketSystemV2.java (Assuming i have to cinsider only cars that are currently present in the parkinglot)






How to run:
    1)please clone the git folder using:  
          git clone https://github.com/ManojReddy5233/greenstitch.git
    2)now in the folder of greenstitch.io , you can see
                                                      a)ParkingTicketSystem.java
                                                      b)ParkingTicketSystemV2.java
    3)now in your terminal navigate to greenstitch folder, now by doing ls, You must be able to see  ParkingTicketSystem.java and ParkingTicketSystemV2.java

    4)now complile and execute  the java file you want to run by below command
         javac ParkingTicketSystem.java
         java ParkingTicketSystem

      demoOutput:
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

    5)similarly for ParkingTicketSystemV2
        javac ParkingTicketSystemV2.java
        java ParkingTicketSystemV2
        demo output:
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


        6)if u face difficulty in doing through terminal, you can simply copy paste the code into a online compiler and see results
          please refer demoOutput for available commands



Update from Arpit Samdani:
        i have enquired with arpit about ambiguity and got to know that we should consider only currentlyParkedCars,so i request the evaluation team to consider my
         ParkingTicketSystemV2.java file
                     