import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

class Screen extends JPanel implements KeyListener {
    private int x = 350, y = 400; // 캐릭터의 초기 위치
    private int health = 100; // 캐릭터의 초기 체력
    private boolean jumping = false, movingLeft = false, movingRight = false, invincible = false;
    // 점프 여부, 왼쪽/오른쪽 이동 여부, 무적 상태 여부를 나타내는 플래그
    private int velocityY = 0; // 캐릭터의 y축 속도 (점프/낙하)
    private final int groundY = 400; // 캐릭터가 설 수 있는 바닥의 y축 위치
    private final Timer movementTimer, enemySpawnTimer, invincibilityTimer;
    // 움직임, 적 생성, 무적 상태 관리를 위한 타이머
    private final JFrame parentFrame;
    private final Color characterColor; // 캐릭터의 색상
    private final ArrayList<Enemy> enemies = new ArrayList<>(); // 적 목록
    private final ArrayList<Projectile> projectiles = new ArrayList<>(); // 투사체 목록
    private final Random random = new Random(); // 랜덤 생성기
    private int lastDirection = 1; // 마지막으로 움직인 방향 (1: 오른쪽, -1: 왼쪽)
    private Character character = new Character(); // 캐릭터 객체
    private int countNumber = 0; // 프레임 카운터
    private Image bgImage;
    private Clip bgmClip;
    private int score = 0; //점수
    private int stage =1; // 현재 스테이
    
    // 생성자: 화면 초기화 및 타이머 설정
    public Screen(JFrame parentFrame, Color characterColor, int stage) {
        this.parentFrame = parentFrame;
        this.characterColor = characterColor;
        this.stage = stage;

        setBackground(Color.WHITE); // 배경색 설정
        setFocusable(true); // 키 이벤트 활성화
        addKeyListener(this); // 키 리스너 추가

        // 초기화 메소드 호출
        initialize();
        
        // 캐릭터 움직임을 처리하는 타이머
        movementTimer = new Timer(15, e -> {
            if (jumping) { // 점프 로직
                y += velocityY;
                velocityY += 2; // 중력 효과
                if (y >= groundY) { // 바닥에 도달했을 때 상태 초기화
                    y = groundY;
                    jumping = false;
                }
            }
            if (movingLeft) x = Math.max(x - 5, 0); // 왼쪽으로 이동
            if (movingRight) x = Math.min(x + 5, getWidth() - 50); // 오른쪽으로 이동

            character.update(); // 캐릭터 상태 업데이트
            moveEnemies(); // 적 이동 처리
            moveProjectiles(); // 투사체 이동 처리
            checkCollisions(); // 충돌 검사

            repaint(); // 화면 갱신
        });

        // 적 생성 타이머
        enemySpawnTimer = new Timer(300 + random.nextInt(500), e -> spawnEnemy());
        // 무적 상태 해제 타이머
        invincibilityTimer = new Timer(500, e -> invincible = false);

        movementTimer.start(); // 타이머 시작
        enemySpawnTimer.start();

        // 포커스 설정
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void initialize() {
        loadBackgroundImage();
        playBackgroundMusic();
    }

    
    private void loadBackgroundImage() {
        try {
            String bgFile = (stage == 1) ? "res/bg1.jpg" : "res/bg2.jpg";
            ImageIcon icon = new ImageIcon(bgFile);
            bgImage = icon.getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private void playBackgroundMusic() {
    	try {
    		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("res/bgm1.mp3"));
    		bgmClip = AudioSystem.getClip(); bgmClip.open(audioInputStream);
    		bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // 반복 재생
    	} catch (Exception e) {
    			e.printStackTrace();
    	}
    }
 

    // 적 생성 로직
    private void spawnEnemy() {
        int side = random.nextInt(2); // 적이 생성될 방향 (왼쪽: 0, 오른쪽: 1)
        int enemyX = (side == 0) ? 0 : getWidth() - 30; // 적의 초기 x 위치
        enemies.add(new Enemy(enemyX, groundY, 30)); // 적 추가
    }

    // 적 이동 처리
    private void moveEnemies() {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.x < x) enemy.x += 1; // 캐릭터 방향으로 이동
            else if (enemy.x > x) enemy.x -= 1;

            if (enemy.health <= 0) {
                iterator.remove(); // 체력이 0 이하인 적 제거
                score++; // 적을 죽일 때마다 점수 증가
                checkStageClear(); // 스테이지 클리어 조건 확인
            }
        }
    }
    
    private void checkStageClear() {
        if ((stage == 1 && score >= 10) || (stage == 2 && score >= 20)) {
            showStageClearMessage();
        }
    }
    
    private void showStageClearMessage() {
        String message = (stage == 1) ? "STAGE1 CLEAR" : "ALL STAGE CLEAR\n좀비로부터 생존하셨습니다";
        String button1Text = (stage == 1) ? "NEXT STAGE" : "종료";
        String button2Text = "STOP";

        // 커스텀 JDialog 생성
        JDialog dialog = new JDialog(parentFrame, "Stage Clear", true);
        dialog.setLayout(new BorderLayout());

        // 메시지 라벨 추가
        JLabel messageLabel = new JLabel(message, JLabel.CENTER);
        dialog.add(messageLabel, BorderLayout.CENTER);

        // 버튼 패널 추가
        JPanel buttonPanel = new JPanel();
        JButton button1 = new JButton(button1Text);
        JButton button2 = new JButton(button2Text);

        button1.addActionListener(e -> {
            dialog.dispose();
            if (stage == 1) {
                // 다음 스테이지로 이동
                parentFrame.getContentPane().removeAll();
                Screen nextStage = new Screen(parentFrame, characterColor, 2); // 스테이지 2로 이동
                parentFrame.add(nextStage);
                parentFrame.revalidate();
                parentFrame.repaint();
                SwingUtilities.invokeLater(nextStage::requestFocusInWindow); // 포커스 설정
            } else {
                // 프로그램 종료
                System.exit(0);
            }
        });

        button2.addActionListener(e -> System.exit(0));

        buttonPanel.add(button1);
        buttonPanel.add(button2);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }




    // 투사체 이동 처리
    private void moveProjectiles() {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.move(); // 투사체 이동

            boolean hit = false;
            for (Enemy enemy : enemies) {
                // 투사체가 적과 충돌했는지 검사
                if (new Rectangle(projectile.x, projectile.y, projectile.size, projectile.size)
                        .intersects(new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size))) {
                    enemy.health--; // 적 체력 감소
                    hit = true;
                    break;
                }
            }

            if (hit || projectile.isOutOfBounds()) iterator.remove(); // 충돌하거나 화면 밖으로 나간 투사체 제거
        }
    }

    // 캐릭터와 적의 충돌 검사
    private void checkCollisions() {
        if (!invincible) { // 무적 상태가 아닐 때만 충돌 검사
            for (Enemy enemy : enemies) {
                if (new Rectangle(x, y, 50, 50).intersects(new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size))) {
                    health -= 10; // 충돌 시 체력 감소
                    invincible = true; // 무적 상태 활성화
                    invincibilityTimer.start(); // 무적 상태 타이머 시작

                    if (health <= 0) { // 체력이 0 이하일 경우 게임 종료
                        JOptionPane.showMessageDialog(this, "GAME OVER!!");
                        System.exit(0);
                    }
                    break;
                }
            }
        }
    }

    // 근접 공격 처리
    private void performMeleeAttack() {
        int attackX = (lastDirection == 1) ? x + 50 : x - 50; // 공격 방향에 따른 x 좌표
        int attackWidth = 40; // 공격 범위
        Rectangle attackArea = new Rectangle(attackX, y, attackWidth, 50);

        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            Rectangle enemyRect = new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size);
            if (attackArea.intersects(enemyRect)) {
                enemy.health--; // 적 체력 감소
                if (enemy.health <= 0) iterator.remove(); // 체력이 0 이하인 적 제거
            }
        }
    }

    // 화면을 그리는 메소드
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 배경화면 그리기
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }

        // 캐릭터 체력바
        g.setColor(Color.RED);
        g.fillRect(x, y - 10, 50, 5); // 체력바 배경 (얇게 설정)
        g.setColor(Color.GREEN);
        g.fillRect(x, y - 10, health / 2, 5); // 체력바 표시 (얇게 설정)

        // 캐릭터
        if(characterColor == Color.GREEN) {
            character.draw(g, this); // 커스텀 캐릭터 그리기
        } else {
            g.setColor(characterColor);
            g.fillRect(x, y, 50, 50); // 기본 사각형 캐릭터
        }

        // 적
        g.setColor(Color.RED);
        for (Enemy enemy : enemies) g.fillRect(enemy.x, enemy.y, enemy.size - 10, enemy.size + 50);

        // 투사체
        g.setColor(Color.ORANGE);
        for (Projectile projectile : projectiles)
            g.fillRect(projectile.x, projectile.y, projectile.size, projectile.size);

        // 점수 표시
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("점수: " + score, getWidth() - 100, 20);
    }
    

    
    public void characterMove(int keyCode) {
    	switch (keyCode) {
    	case KeyEvent.VK_LEFT:
            movingLeft = true;
            lastDirection = -1;
            break;
        case KeyEvent.VK_RIGHT:
            movingRight = true;
            lastDirection = 1;
            break;
        case KeyEvent.VK_UP:
            if (!jumping) {
                jumping = true;
                velocityY = -15;
            }
    	}
    }
    
    public int getCount() {
		return this.countNumber;
	}
    
    // 키보드 입력 처리
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        character.actionPressed(key); // 캐릭터 상태 변경
        characterMove(key); // 움직임 처리
        if (key == KeyEvent.VK_A) performMeleeAttack(); // 근접 공격
        if (key == KeyEvent.VK_S) fireProjectile(); // 투사체 발사
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        character.actionReleased(key); // 캐릭터 상태 해제
        if (key == KeyEvent.VK_LEFT) movingLeft = false; // 왼쪽 이동 해제
        if (key == KeyEvent.VK_RIGHT) movingRight = false; // 오른쪽 이동 해제
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // 사용하지 않음
    }
    
    // 투사체 발사 처리
    private void fireProjectile() {
        int speed = lastDirection * 10; // 방향에 따른 투사체 속도
        projectiles.add(new Projectile(x + 25, y + 25, speed)); // 투사체 추가
    }
}