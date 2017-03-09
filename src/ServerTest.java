import javax.swing.*;

/**
 * Created by Lovi on 2017. 03. 09. @ 0:19.
 */
public class ServerTest {
	public static void main(String[] args) {
		Server sally = new Server();
		sally.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sally.startRunning();
	}
}
