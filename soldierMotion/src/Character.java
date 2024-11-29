import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore.PrivateKeyEntry;

import javax.imageio.ImageIO;

public class Character implements KeyListener {
	private BufferedImage sprite;
	private int x = 50; //처음 캐릭터 그릴 위치 x좌표
	private int y = 400;
	private State [] states;
	private int stateIndex = 0;
	private boolean flip = false;  // 왼쪽, 오른쪽 반전 상태를 관리하는 변수
	
	public Character() {
		loadImage();
		states = new State[6];
		State state = new State();
		states[0] = state;//아무것도 안할때 숨쉬기 모션
		state.width = 128;//잘라낼 크기 x
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;//어디서부터 잘라내기 시작할지
		state.start_y = 640;
		state.frame_size = 6;//프레임 수
		
		state = new State();
		states[1] = state;//왼쪽으로 가기 
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 384;
		state.frame_size = 8;
		state.stop = true;
		
		state = new State();
		states[2] = state;//오른쪽으로 가기
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 384;
		state.frame_size = 8;
		
		state = new State();
		states[3] = state;//근접공격
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 0;
		state.frame_size = 3;
		
		state = new State();
		states[4] = state;//사격
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 512;
		state.frame_size = 4;
		
		state = new State();
		states[5] = state;//죽음
		state.width = 128;
		state.height = 128;
		state.index_x = 0;
		state.index_y = 0;
		state.start_x = 0;
		state.start_y = 128;
		state.frame_size = 4;
		
		state.stop = true;	
	}
	
	private State getState() {
		return states[stateIndex];
	}
	
	private void loadImage() {
		try {
			this.sprite = ImageIO.read(new File("res/soldier.png"));
			this.sprite = TransformColorToTransparency(sprite, new Color(255, 255, 255));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected BufferedImage TransformColorToTransparency(BufferedImage image, Color c1) {
		  final int r1 = c1.getRed();
		  final int g1 = c1.getGreen();
		  final int b1 = c1.getBlue();
		 
		  ImageFilter filter = new RGBImageFilter() {
				public int filterRGB(int x, int y, int rgb) {
					int r = ( rgb & 0xFF0000 ) >> 16;
					int g = ( rgb & 0xFF00 ) >> 8;
					int b = ( rgb & 0xFF );
					if( r == r1 && g == g1 && b == b1) {
						return rgb & 0xFFFFFF;
					}
					return rgb;
				}
			};
		 
			ImageProducer ip = new FilteredImageSource( image.getSource(), filter );
			Image img = Toolkit.getDefaultToolkit().createImage(ip);
			BufferedImage dest = new BufferedImage(img.getWidth(null), 
					img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = dest.createGraphics();
			g.drawImage(img, 0, 0, null);
			g.dispose();
			return dest;
	}
	public void draw(Graphics g, Screen screen) {
		drawCharacter(getState(), g, screen);
	}
	
	private void drawCharacter(State state, Graphics g, Screen screen) {
        int ix = state.width * state.index_x + state.start_x;
        int iy = state.height * state.index_y + state.start_y;

        // Graphics2D로 변환하여 반전 처리
        Graphics2D g2d = (Graphics2D) g;

        if (flip) {  // 반전 상태가 true일 경우 (왼쪽 방향)
            g2d.scale(-1, 1);  // x축을 -1로 반전
            g2d.drawImage(sprite, -x - state.width, y,
                    -x, y + state.height,
                    ix, iy,
                    ix + state.width, iy + state.height, screen);
            g2d.scale(-1, 1);  // 원래 상태로 복구
        } else {  // 반전 상태가 false일 경우 (오른쪽 방향)
            g2d.drawImage(sprite, x, y,
                    x + state.width, y + state.height,
                    ix, iy,
                    ix + state.width,
                    iy + state.height, screen);
        }

	    
	    if (screen.getCount() % 100 == 0) {
	        if (state.index_x < state.frame_size - 1) {
	            state.index_x++;
	        } else {
	            if (!state.stop)
	                state.index_x = 0;
	            else
	                state.index_x = state.frame_size - 1;
	        }
	    }
	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                this.stateIndex = 1;  // 왼쪽으로 가기
                x -= 2;
                states[1].stop = false;
                flip = true;  // 왼쪽 방향으로 반전
                break;
            case KeyEvent.VK_RIGHT:
                this.stateIndex = 2;  // 오른쪽으로 가기
                x += 2;
                states[2].stop = false;
                flip = false;  // 오른쪽 방향으로 복원
                break;
            case KeyEvent.VK_A:
                this.stateIndex = 3;  // 근접 공격
                states[3].stop = false;
                break;
            case KeyEvent.VK_S:
                this.stateIndex = 4;  // 사격
                states[4].stop = false;
                break;
            case KeyEvent.VK_D:
                this.stateIndex = 5;  // 죽음
                states[5].stop = false;
                break;
        }
    }

	@Override
	public void keyReleased(KeyEvent e) {
	    // TODO Auto-generated method stub
	    this.stateIndex = 0;
	    states[1].stop = true;// 왼쪽 애니메이션 끝났을 때 멈추기
	    states[2].stop = true;
	    states[3].stop = true;// 오른쪽 애니메이션 끝났을 때 멈추기
	    states[4].stop = true;
	    states[5].stop = true;
	}

}
