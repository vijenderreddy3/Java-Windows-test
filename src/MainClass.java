import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MainClass extends JFrame {
	public static void main(String[] strings) {
		new MainClass().setVisible(true);
	}

	private final TextArea area = new TextArea();
	private final JButton sawButton = new JButton("SAW");
	private final JButton gbnButton = new JButton("GBN");
	private final JFileChooser chooser = new JFileChooser(".");

	public MainClass() {
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setLayout(new BorderLayout());

		this.add(new JScrollPane(area), BorderLayout.CENTER);
		JPanel buttonsPanel = new JPanel();
		this.add(buttonsPanel, BorderLayout.SOUTH);
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.add(sawButton);
		sawButton.addActionListener(listener);
		buttonsPanel.add(gbnButton);
		gbnButton.addActionListener(listener);
	}
	
	private final ActionListener listener = new ActionListener() {
		@Override
		
		
		public void actionPerformed(ActionEvent e) {
			new Server(Values.PORT, MainClass.this,Values.HOST);
			SenderBoth client;
			if (e.getSource() == sawButton) {
				client = new SAW(Values.LOSS, MainClass.this);
			} else {
				client = new GoBackN(Values.LOSS, Values.GBN_WINDOW_SIZE, MainClass.this);
			}

			int option = chooser.showOpenDialog(MainClass.this);
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				try (InputStream inputStream = new FileInputStream(file)) {
					client.send(Values.HOST, Values.PORT, inputStream);
				} catch (Exception e2) {
					throw new RuntimeException(e2);
				}
			}
		}
	};

	public void append(String text) {
		area.append(text + "\n");
	}
}
