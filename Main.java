import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

class OnBoard {
    public void help() {
        System.out.println("Available commands:");
        System.out.println("\tadd       - Add a material to a course");
        System.out.println("\tview      - View materials for a course");
        System.out.println("\topen      - Open a material (PDF/book)");
        System.out.println("\tmark      - Mark material as completed");
        System.out.println("\tprogress  - Show progress for a course");
        System.out.println("\tlist      - List all courses");
        System.out.println("\texit      - Exit application");

    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("======================================================================================");
        System.out.println("                  REVISION MANAGER CLI (JAVA)");
        System.out.println("======================================================================================");

        System.out.println("Manage your course materials and track revision progress.");

        System.out.println("Type 'help' to see available commands.");
        System.out.println("Type 'exit' to quit.");
        System.out.print("?: ");
        String option = input.readLine().toLowerCase();

        OnBoard board = new OnBoard();
        if (option.equals("help")) {
            board.help();
        } else if (option.equals("quit")) {
            System.out.println("Exiting application. Goodbye!");
            System.exit(0);
        }

    }
}