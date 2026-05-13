import util.DBConnection;
import view.AppView;

public class Main {
    public static void main(String[] args) {
        DBConnection.getConnection();
        AppView app = new AppView();
        AppView.menu();
    }
}
