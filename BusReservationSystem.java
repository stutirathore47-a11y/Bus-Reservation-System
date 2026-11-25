import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Collections;
import java.util.Comparator;

/**
 * Bus Reservation System
 * DSA concepts: ArrayList, Queue, Searching, Sorting
 *
 * Compile: javac BusReservationSystem.java
 * Run:     java BusReservationSystem
 */
public class BusReservationSystem {
    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Bus> buses = new ArrayList<>();
    private static final ArrayList<Booking> bookings = new ArrayList<>();
    private static final Queue<BookingRequest> waitingQueue = new LinkedList<>();
    private static int nextBookingId = 1001;

    static class Bus {
        int busNo;
        String busName;
        String source;
        String destination;
        int totalSeats;
        int availableSeats;
        double fare;

        Bus(int busNo, String busName, String source, String destination,
            int totalSeats, double fare) {
            this.busNo = busNo;
            this.busName = busName;
            this.source = source;
            this.destination = destination;
            this.totalSeats = totalSeats;
            this.availableSeats = totalSeats;
            this.fare = fare;
        }

        void display() {
            System.out.printf("%-8d %-18s %-14s %-14s %-8d %-8d Rs. %.2f%n",
                    busNo, busName, source, destination, totalSeats, availableSeats, fare);
        }
    }

    static class Booking {
        int bookingId;
        String passengerName;
        int age;
        int busNo;
        int seats;
        double totalFare;

        Booking(int bookingId, String passengerName, int age, int busNo,
                int seats, double totalFare) {
            this.bookingId = bookingId;
            this.passengerName = passengerName;
            this.age = age;
            this.busNo = busNo;
            this.seats = seats;
            this.totalFare = totalFare;
        }

        void display() {
            System.out.printf("%-8d %-20s %-6d %-8d %-7d Rs. %.2f%n",
                    bookingId, passengerName, age, busNo, seats, totalFare);
        }
    }

    static class BookingRequest {
        String passengerName;
        int age;
        int busNo;
        int seats;

        BookingRequest(String passengerName, int age, int busNo, int seats) {
            this.passengerName = passengerName;
            this.age = age;
            this.busNo = busNo;
            this.seats = seats;
        }
    }

    public static void main(String[] args) {
        addSampleBuses();

        while (true) {
            System.out.println("\n========== BUS RESERVATION SYSTEM ==========");
            System.out.println("1. View all buses");
            System.out.println("2. Search bus by route");
            System.out.println("3. Sort buses by fare");
            System.out.println("4. Book tickets");
            System.out.println("5. View all bookings");
            System.out.println("6. Cancel booking");
            System.out.println("7. View waiting queue");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            int choice = readInt();
            switch (choice) {
                case 1: displayBuses(); break;
                case 2: searchBusByRoute(); break;
                case 3: sortBusesByFare(); break;
                case 4: bookTickets(); break;
                case 5: displayBookings(); break;
                case 6: cancelBooking(); break;
                case 7: displayWaitingQueue(); break;
                case 8:
                    System.out.println("Thank you for using Bus Reservation System!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 8.");
            }
        }
    }

    private static void addSampleBuses() {
        buses.add(new Bus(101, "City Express", "Bhilai", "Raipur", 40, 120.00));
        buses.add(new Bus(102, "Morning Star", "Raipur", "Durg", 35, 90.00));
        buses.add(new Bus(103, "Highway Rider", "Bhilai", "Nagpur", 45, 550.00));
        buses.add(new Bus(104, "Green Line", "Durg", "Raipur", 30, 100.00));
        buses.add(new Bus(105, "Night Rider", "Raipur", "Bilaspur", 40, 250.00));
    }

    private static int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            sc.next();
        }
        int value = sc.nextInt();
        sc.nextLine();
        return value;
    }

    private static void printBusHeader() {
        System.out.printf("%-8s %-18s %-14s %-14s %-8s %-8s %s%n",
                "Bus No.", "Bus Name", "Source", "Destination",
                "Total", "Available", "Fare");
        System.out.println("--------------------------------------------------------------------------------");
    }

    private static void displayBuses() {
        System.out.println("\n--- Available Buses ---");
        printBusHeader();
        for (Bus bus : buses) {
            bus.display();
        }
    }

    // Linear searching through ArrayList
    private static void searchBusByRoute() {
        System.out.print("Enter source: ");
        String source = sc.nextLine().trim();
        System.out.print("Enter destination: ");
        String destination = sc.nextLine().trim();

        boolean found = false;
        for (Bus bus : buses) {
            if (bus.source.equalsIgnoreCase(source)
                    && bus.destination.equalsIgnoreCase(destination)) {
                if (!found) {
                    printBusHeader();
                }
                bus.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No bus found for this route.");
        }
    }

    // Sorting ArrayList using Comparator
    private static void sortBusesByFare() {
        Collections.sort(buses, Comparator.comparingDouble(bus -> bus.fare));
        System.out.println("\nBuses sorted by fare (low to high):");
        displayBuses();
    }

    private static void bookTickets() {
        System.out.print("Enter bus number: ");
        int busNo = readInt();
        Bus bus = findBusByNumber(busNo);

        if (bus == null) {
            System.out.println("Bus not found.");
            return;
        }

        System.out.print("Enter passenger name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Passenger name cannot be empty.");
            return;
        }

        System.out.print("Enter passenger age: ");
        int age = readInt();
        System.out.print("Enter number of seats: ");
        int seats = readInt();

        if (age <= 0 || seats <= 0) {
            System.out.println("Age and number of seats must be positive.");
            return;
        }

        if (bus.availableSeats >= seats) {
            confirmBooking(name, age, bus, seats);
        } else {
            System.out.println("Sorry, only " + bus.availableSeats + " seats are available.");
            System.out.print("Add this request to the waiting queue? (yes/no): ");
            String answer = sc.nextLine().trim();
            if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
                waitingQueue.offer(new BookingRequest(name, age, busNo, seats));
                System.out.println("Request added to the waiting queue.");
            } else {
                System.out.println("Booking request not saved.");
            }
        }
    }

    private static void confirmBooking(String name, int age, Bus bus, int seats) {
        int id = nextBookingId++;
        double total = bus.fare * seats;
        Booking booking = new Booking(id, name, age, bus.busNo, seats, total);
        bookings.add(booking);
        bus.availableSeats -= seats;

        System.out.println("\nBooking successful!");
        System.out.println("Booking ID: " + id);
        System.out.println("Passenger: " + name);
        System.out.println("Bus: " + bus.busName);
        System.out.println("Seats: " + seats);
        System.out.printf("Total fare: Rs. %.2f%n", total);
    }

    private static Bus findBusByNumber(int busNo) {
        for (Bus bus : buses) {
            if (bus.busNo == busNo) {
                return bus;
            }
        }
        return null;
    }

    private static void displayBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings have been made yet.");
            return;
        }

        System.out.println("\n--- Booking Records ---");
        System.out.printf("%-8s %-20s %-6s %-8s %-7s %s%n",
                "ID", "Passenger", "Age", "Bus No.", "Seats", "Total Fare");
        System.out.println("----------------------------------------------------------------");
        for (Booking booking : bookings) {
            booking.display();
        }
    }

    private static void cancelBooking() {
        System.out.print("Enter booking ID to cancel: ");
        int bookingId = readInt();

        Booking target = null;
        for (Booking booking : bookings) {
            if (booking.bookingId == bookingId) {
                target = booking;
                break;
            }
        }

        if (target == null) {
            System.out.println("Booking ID not found.");
            return;
        }

        Bus bus = findBusByNumber(target.busNo);
        if (bus != null) {
            bus.availableSeats += target.seats;
        }
        bookings.remove(target);
        System.out.println("Booking cancelled successfully.");

        // Process the first waiting request if seats are now available.
        processWaitingQueue();
    }

    private static void processWaitingQueue() {
        if (waitingQueue.isEmpty()) {
            return;
        }

        int requestsToCheck = waitingQueue.size();
        for (int i = 0; i < requestsToCheck; i++) {
            BookingRequest request = waitingQueue.poll();
            Bus bus = findBusByNumber(request.busNo);

            if (bus != null && bus.availableSeats >= request.seats) {
                System.out.println("A waiting request can now be booked for "
                        + request.passengerName + ".");
                confirmBooking(request.passengerName, request.age, bus, request.seats);
            } else {
                waitingQueue.offer(request);
            }
        }
    }

    private static void displayWaitingQueue() {
        if (waitingQueue.isEmpty()) {
            System.out.println("The waiting queue is empty.");
            return;
        }

        System.out.println("\n--- Waiting Queue (first request shown first) ---");
        int position = 1;
        for (BookingRequest request : waitingQueue) {
            System.out.println(position + ". Passenger: " + request.passengerName
                    + ", Bus No.: " + request.busNo
                    + ", Seats requested: " + request.seats);
            position++;
        }
    }
}