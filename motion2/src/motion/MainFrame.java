package motion;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
	public MainFrame() {
		setTitle("스트리트 파이터 게임!!");
		setSize(800, 800);
		add(new Screen());
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
