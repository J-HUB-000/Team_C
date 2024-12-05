package motion;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Timer;
import java.util.TimerTask;

public class Screen extends Canvas implements ComponentListener {
	
	private Graphics bg;
	private Image offScreen;
	private Dimension dim;
	private Character_1 soldier = new Character_1();
	private Character_2 shotgun = new Character_2();
	private Character_3 batman = new Character_3();
	private zombie_1 normalzombie = new zombie_1();
	private zombie_2 speedzombie = new zombie_2();
	private zombie_3 girlzombie = new zombie_3();
	private int countNumber = 0;
	
	public Screen() {
		addComponentListener(this);
		addKeyListener(soldier);
		addKeyListener(shotgun);
		addKeyListener(batman);
		addKeyListener(normalzombie);
		addKeyListener(speedzombie);
		addKeyListener(girlzombie);
		setFocusable(true);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				repaint();
				counting();
			}
		}, 0, 1);
	}
	public void counting() {
		this.countNumber++;
	}
	
	public int getCount() {
		return this.countNumber;
	}
	
	private void initBuffer() {
		this.dim = getSize();
		this.offScreen = createImage(dim.width, dim.height);
		this.bg = this.offScreen.getGraphics();
	}
	
	@Override
	public void paint(Graphics g) {
		bg.clearRect(0, 0, dim.width, dim.height);
		//~~~~
		
		
		soldier.draw(bg, this);
		shotgun.draw(bg, this);
		batman.draw(bg, this);
		normalzombie.draw(bg, this);
		speedzombie.draw(bg, this);
		girlzombie.draw(bg, this);
		g.drawImage(offScreen, 0, 0, this);
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		initBuffer();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	

}
