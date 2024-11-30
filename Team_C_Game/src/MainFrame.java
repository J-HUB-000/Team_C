
import javax.swing.JFrame;

public class MainFrame extends JFrame{
	public MainFrame() {        // 프레임 설정
        setTitle("좀비 서바이벌");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // 화면 중앙에 표시
        setResizable(false);

        // Screen 패널 추가
        Title screen = new Title(this);
        add(screen);

        setVisible(true);
	}
	
}
