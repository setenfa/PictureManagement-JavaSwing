import ui.FilePane;
import ui.MainWindow;

public class Main {
    public static void main(String[] args) {
        FilePane filePane = new FilePane();
        MainWindow mainWindow = new MainWindow(filePane, 800, 500);
    }
}