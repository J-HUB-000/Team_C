import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

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
    private int x = 350, y = 384; // 캐릭터의 초기 위치
    private int health = 100; // 캐릭터의 초기 체력
    private boolean jumping = false, movingLeft = false, movingRight = false, invincible = false;
    // 점프 여부, 왼쪽/오른쪽 이동 여부, 무적 상태 여부를 나타내는 플래그
    private int velocityY = 0; // 캐릭터의 y축 속도 (점프/낙하)
    private final int groundY = 384; // 캐릭터가 설 수 있는 바닥의 y축 위치
    private final Timer movementTimer, enemySpawnTimer;
	private Timer invincibilityTimer;
    // 움직임, 적 생성, 무적 상태 관리를 위한 타이머
    private final JFrame parentFrame;
    private final Color characterColor; // 캐릭터의 색상
    private final Map<Integer, Boolean> keyStates = new HashMap<>(); // 키 상태를 추적
    private final Map<Integer, Timer> actionTimers = new HashMap<>(); // 키에 대응하는 타이머
    private final ArrayList<Enemy> enemies = new ArrayList<>(); // 적 목록
    private final ArrayList<Projectile> projectiles = new ArrayList<>(); // 투사체 목록
    private final Random random = new Random(); // 랜덤 생성기
    private int lastDirection = 1; // 마지막으로 움직인 방향 (1: 오른쪽, -1: 왼쪽)
    private Character character = new Character(); // 캐릭터 객체
    private Enemy enemy = new Enemy(310,350,40); // 캐릭터 객체
    private int countNumber = 0; // 프레임 카운터
    private int delay, select = 0; 
    private boolean meleeAttackCooldown = false; // A키 공격 쿨타임 상태
    private boolean projectileAttackCooldown = false; // S키 공격 쿨타임 상태
    private boolean shotgunCooldown = false; // 샷건 쿨타임 상태
    private Image bgImage;
    private Clip bgmClip;
    private int score = 0; //점수
    private int stage = 1; // 현재 스테이지
    
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
        movementTimer = new Timer(20, e -> {
            if (jumping) { // 점프 로직
                y += velocityY;
                velocityY += 1; // 중력 효과
                if (y >= groundY) { // 바닥에 도달했을 때 상태 초기화
                    y = groundY;
                    jumping = false;
                }
            }
            
        	if (movingLeft) x = Math.max(x - 5, 0); // 왼쪽으로 이동
        	if (movingRight) x = Math.min(x + 5, getWidth() - 50); // 오른쪽으로 이동
        	
            character.update(); // 캐릭터 상태 업데이트
            enemy.updateAnimation();
            moveEnemies(); // 적 이동 처리
            moveProjectiles(); // 투사체 이동 처리
            checkCollisions(); // 충돌 검사

            repaint(); // 화면 갱신
        });
        // 적 생성 타이머
        enemySpawnTimer = new Timer(500 + random.nextInt(100), e -> spawnEnemy());
        // 무적 상태 해제 타이머
        invincibilityTimer = new Timer(1000, e -> invincible = false);

        movementTimer.start(); // 타이머 시작
        enemySpawnTimer.start();
    }
    
    private void loadBackgroundImage() {
        try {
            String bgFile;
            switch (stage) {
                case 1: bgFile = "res/bg1.jpg"; break;
                case 2: bgFile = "res/bg2.jpg"; break;
                //배경 이미지 추가.
                default: bgFile = "res/default.jpg"; // 기본 배경
            }
            ImageIcon icon = new ImageIcon(bgFile);
            bgImage = icon.getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        enemies.clear();  // 적 목록 초기화
        projectiles.clear();  // 투사체 초기화
        loadBackgroundImage();
        playBackgroundMusic();
     // 체력 초기화
        if (stage == 1) {
            health = 100; // 스테이지 1에서는 초기 체력 100
        } else if (stage == 2) {
            health = 100; // 스테이지 2에서는 체력 초기화
        }
    }
    
    private void playBackgroundMusic() {
        try {
            // 기존 클립이 재생 중이면 정지하고 닫기
            if (bgmClip != null && bgmClip.isRunning()) {
                bgmClip.stop();
                bgmClip.close();
            }
            String musicFile;
            switch (stage) {
                case 1: 
                    musicFile = "res/bgm1.wav"; 
                    break;
                case 2: 
                    musicFile = "res/bgm1.wav"; 
                    break;
                // 추가 스테이지 음악 파일
                default: 
                    musicFile = "res/bgm1.wav";
            }
            
            // 새로운 음악 파일 로드 및 재생
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(musicFile));
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioInputStream);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // 반복 재생
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stopBackgroundMusic() {
        if (bgmClip != null) {
            bgmClip.stop();
            bgmClip.close();
        }
    }

    // 적 생성 로직
    private void spawnEnemy() {
        int side = random.nextInt(2); // 적이 생성될 방향 (왼쪽: 0, 오른쪽: 1)
        int enemyX = (side == 0) ? 0 : getWidth(); // 적의 초기 x 위치
        enemies.add(new Enemy(enemyX, groundY, 40)); // 적 추가
    }
    
    private int round = 10;
    //스테이지 클리어 조건
    private void checkStageClear() {
        if ((stage == 1 && score >= round) || (stage == 2 && score >= round)) {
            showStageClearMessage();
        }
    }
    
    public void cleanup() {
        // 실행 중인 타이머 정지
        if (movementTimer != null) movementTimer.stop();
        if (enemySpawnTimer != null) enemySpawnTimer.stop();
        if (invincibilityTimer != null) invincibilityTimer.stop();

        // 배경음악 정지
        stopBackgroundMusic();

        // 기타 리소스 정리 (필요에 따라 추가)
        keyStates.clear();
        actionTimers.values().forEach(Timer::stop);
        actionTimers.clear();
    }

    private void showStageClearMessage() {
    	String message = (stage == 1) ? "STAGE 클리어" : "당신은 생존했습니다.";
        String button1Text = (stage == 1) ? "다음 스테이지" : "종료";
        String button2Text = "타이틀 화면으로";

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
                // 현재 Screen의 배경음악을 정리하고, 새 스테이지로 이동
                stopBackgroundMusic(); // 현재 배경음악 중지
                parentFrame.getContentPane().removeAll();
                Screen nextStage = new Screen(parentFrame, characterColor, 2); // 스테이지 2로 이동
                parentFrame.add(nextStage);
                parentFrame.revalidate();
                parentFrame.repaint();
                SwingUtilities.invokeLater(nextStage::requestFocusInWindow); // 포커스 설정
            } else {
                // 모든 스테이지 클리어 후 종료
                System.exit(0);
            }
        });

        button2.addActionListener(e -> {
            dialog.dispose();
            cleanup(); // 현재 Screen 리소스 정리
            // 첫 화면으로 돌아가기
            parentFrame.getContentPane().removeAll();
            Title titleScreen = new Title(parentFrame); // 초기 화면 패널 추가
            parentFrame.add(titleScreen);
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        buttonPanel.add(button1);
        buttonPanel.add(button2);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    // 적 이동 처리
    private void moveEnemies() {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.x < x-50) { 
            	enemy.x += 1; // 캐릭터 방향으로 이동
            	enemy.facingLeft = false; // 캐릭터 방향에 따라 좀비 좌우 변경
            }
            else if (enemy.x > x-10) {
            	enemy.x -= 1;
            	enemy.facingLeft = true;
            }
            
            // 애니메이션 업데이트
            if (countNumber % 20 == 0) {
                enemy.updateAnimation();
            }
            
            if (enemy.health <= 0) {
            	iterator.remove(); // 체력이 0 이하인 적 제거
                score++; // 적을 죽일 때마다 점수 증가
                checkStageClear(); // 스테이지 클리어 조건 확인
            }
        }
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
                        .intersects(new Rectangle(enemy.x, enemy.y, enemy.size, enemy.size+15))) {
                    enemy.health--; // 적 체력 감소
                    hit = true;
                    break;
                }
            }
            
            // 충돌하거나 화면 밖으로 나간 투사체 제거
            if (hit || projectile.isOutOfBounds()) iterator.remove(); 
        }
    }

    // 캐릭터와 적의 충돌 검사
    private void checkCollisions() {
        if (!invincible) { // 무적 상태가 아닐 때만 충돌 검사
            for (Enemy enemy : enemies) {
                if (new Rectangle(x+10, y, 20, 50).intersects(new Rectangle(enemy.x+30, enemy.y, enemy.size,enemy.size))) {
                    health -= 10; // 충돌 시 체력 감소
                    invincible = true; // 무적 상태 활성화
                    invincibilityTimer.start(); // 무적 상태 타이머 시작

                    if (health <= 0) { // 체력이 0 이하일 경우 게임 종료
                        JOptionPane.showMessageDialog(this, "게임 오버!");
                        System.exit(0);
                    }
                    break;
                }
            }
        }
    }
    private void startInvincibilityTimer() {
        invincibilityTimer = new Timer(1000, e -> {
            invincible = false; // 무적 상태 종료
        });
        invincibilityTimer.setRepeats(false); // 한 번만 실행
        invincibilityTimer.start(); // 타이머 시작
    }

    
    // 근접 공격 처리
    private void performMeleeAttack() {
    	if (meleeAttackCooldown) return; // 쿨타임 중이면 무시
        meleeAttackCooldown = true; // 쿨타임 활성화
        
        int attackX = (lastDirection == 1) ?  x + 30 : x - 40; // 공격 방향에 따른 x 좌표
        int attackWidth = 50; // 공격 범위
        Rectangle attackArea = new Rectangle(attackX, y+30, attackWidth, 50);

        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            Rectangle enemyRect = new Rectangle(enemy.x + 30, enemy.y, enemy.size, enemy.size + 60);
            if (attackArea.intersects(enemyRect)) {
                enemy.health--; // 적 체력 감소
            }
            if (enemy.health<= 0) {
            	iterator.remove(); // 체력이 0 이하인 적 제거
                score++; // 적을 죽일 때마다 점수 증가
                checkStageClear(); // 스테이지 클리어 조건 확인
            }
        }
        // 일정 시간 후 쿨타임 해제
        coolTime(200);
    }

    // 투사체 발사 처리
    private void fireProjectile() {
        // 쿨타임 체크
        if (projectileAttackCooldown || shotgunCooldown) return; // 이미 쿨타임 중이면 발사하지 않음

        projectileAttackCooldown = true; // 기본 쿨타임 활성화

        int speed = lastDirection * 20; // 방향에 따른 투사체 속도
        
        // 샷건 캐릭터일 경우
        if (character.getSelectCharacter() == 2) {
            // 3발의 총알을 동시에 발사
            projectiles.add(new Projectile(x + 25, y + 40, speed)); // 첫 번째 총알
            projectiles.add(new Projectile(x + 25, y + 45, speed + 5)); // 두 번째 총알 (약간 더 빠르게)
            projectiles.add(new Projectile(x + 25, y + 50, speed - 5)); // 세 번째 총알 (약간 더 느리게)
            projectiles.add(new Projectile(x + 25, y + 48, speed + 10)); // 세 번째 총알 (약간 더 느리게)

            // 샷건 쿨타임을 2초로 설정
            shotgunCooldown = true;
            Timer shotgunCooldownTimer = new Timer(2000, e -> shotgunCooldown = false); // 2000ms 후에 쿨타임 해제
            shotgunCooldownTimer.setRepeats(false);
            shotgunCooldownTimer.start();
        } else {
            // 기본 총알 발사
            projectiles.add(new Projectile(x + 25, y + 44, speed));
        }

        // 기본 쿨타임을 200ms로 설정 (발사 후 200ms 동안 발사 불가)
        coolTime(200); 
    }

	// 일정 시간 후 쿨타임 해제
 	private void coolTime(int delay) {
 		Timer cooldownTimer = new Timer(delay, e -> {
 			projectileAttackCooldown = false;
 			meleeAttackCooldown = false;
 		});
 		cooldownTimer.setRepeats(false);
 		cooldownTimer.start();
	}

    // 화면을 그리는 메소드
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 배경화면 그리기
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
        
        int groundHeight = 90; // 바닥의 높이 
        g.setColor(new Color(101, 67, 33)); // 어두운 갈색 
        g.fillRect(0, getHeight() - groundHeight, getWidth(), groundHeight); // 네모 박스 그리기

        // 점수 표시
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        if (stage==2) round = 15;
        g.drawString("Score: " + score +"/"+ round , getWidth() - 450, 40);

        // 캐릭터 체력바
        g.setColor(Color.RED);
        g.fillRect(x, y - 10, 50, 10); // 체력바 배경
        g.setColor(Color.GREEN);
        g.fillRect(x, y - 10, health / 2, 10); // 체력바 표시

        // 캐릭터 충돌 영역 그리기
        g.setColor(Color.BLUE);
        g.drawRect(x+10, y, 20, 50); // 캐릭터의 충돌 영역 (사각형)

        // 적
        for (Enemy enemy : enemies) {
            enemy.draw(g, this);
            // 적의 충돌 영역 그리기
            g.setColor(Color.RED);
            g.drawRect(enemy.x+30, enemy.y, enemy.size, enemy.size+60); // 적의 충돌 영역 (사각형)
        }

        // 투사체
        g.setColor(Color.ORANGE);
        for (Projectile projectile : projectiles) {
            g.fillRect(projectile.x, projectile.y, projectile.size, projectile.size);

            // 투사체의 충돌 영역 그리기
            g.setColor(Color.YELLOW);
            g.drawRect(projectile.x, projectile.y, projectile.size, projectile.size); // 투사체 충돌 영역 (사각형)
        }

        // 근접 공격 범위 그리기
        if (lastDirection == 1) { // 오른쪽 공격
            g.setColor(Color.CYAN);
            g.drawRect(x + 30, y+30, 50, 50); // 근접 공격 범위 (오른쪽)
        } else if (lastDirection == -1) { // 왼쪽 공격
            g.setColor(Color.CYAN);
            g.drawRect(x - 40, y+30, 50, 50); // 근접 공격 범위 (왼쪽)
        }


        // 캐릭터 그리기
        character.draw(g, this); // 커스텀 캐릭터 그리기
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
        if (key == KeyEvent.VK_A) {
        	startActionWithDelay(key, this::performMeleeAttack, 300); // 근접 공격 후 300ms 딜레이
        }
        else if (key == KeyEvent.VK_S) {
        	if(character.getSelectCharacter() == 1) {//단발 사격 군인
            	startActionWithDelay(key, this::fireProjectile, 200); // 투사체 발사 후 100ms 딜레이
            	coolTime(200);
        	}
        	else if(character.getSelectCharacter() == 2) {//샷건 캐릭터 4발 씩 쏨
        		startActionWithDelay(key, this::fireProjectile, 200);
            	coolTime(200);
        	}
        	else {//막대기 캐릭터
        		startActionWithDelay(key, this::performMeleeAttack, 300); // 근접 공격 후 300ms 딜레이
        	}
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        character.actionReleased(key); // 캐릭터 상태 해제
        keyStates.put(key, false); // 키 상태 초기화

        // 해당 키의 타이머 중지
        Timer timer = actionTimers.remove(key);
        if (timer != null) timer.stop();
        
        if (key == KeyEvent.VK_LEFT) movingLeft = false; // 왼쪽 이동 해제
        if (key == KeyEvent.VK_RIGHT) movingRight = false; // 오른쪽 이동 해제
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // 사용하지 않음
    }

    // 딜레이를 두고 동작을 반복 실행
    private void startActionWithDelay(int keyCode, Runnable action, int delay) {
        if (keyStates.getOrDefault(keyCode, false)) return; // 이미 동작 중이면 무시
        keyStates.put(keyCode, true);

        // 한 번 실행
        action.run();

        // 딜레이 이후 반복 실행
        Timer timer = new Timer(delay, e -> {
            if (keyStates.getOrDefault(keyCode, false)) action.run();
        });
        timer.start();
        actionTimers.put(keyCode, timer);
    }
}
