import Backend.Databases.ResourceDatabase;
import Backend.Databases.TransactionDatabase;
import Backend.Databases.UserDatabase;
import Frontend.UIManager;

import java.text.ParseException;

public class Main {

    public static void main(String[] args) throws ParseException {

        System.out.println("Loading databases");
        TransactionDatabase.init();
        ResourceDatabase.init();
        UserDatabase.init();

        System.out.println("Starting GUI");
        try {
            UIManager.main();
        } finally {
            System.out.println("Saving databases");
            UserDatabase.close();
            ResourceDatabase.close();
            TransactionDatabase.close();
        }
    }
}
