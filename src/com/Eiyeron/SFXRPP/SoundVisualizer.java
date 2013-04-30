package com.Eiyeron.SFXRPP;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SoundVisualizer extends JPanel {
	private ArrayList<Double> histogram;
	
	public void updateHistogram(SFXRData sound) {
		histogram.clear();
		sound.resetSample(false);
		for(int i = 0; i < sound.getLength(); ++i) {
			histogram.add(sound.synthSample());
		}
		sound.resetSample(false);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(histogram != null && histogram.size() > 0) {
			int ecart =  histogram.size() / getWidth();
			for(int i = 0; i < getWidth(); i++) {
				float x =((float)i*ecart / (float)histogram.size()) *getWidth(); 
				double y = histogram.get(i*ecart) * getHeight();
				g.setColor(Color.red);
				g.fillRect((int)x , getHeight() / 2, 1, (int)y);
			}			
		}
	}

	public SoundVisualizer() {
		super();
		setPreferredSize(new Dimension(300, 200));
		histogram = new ArrayList<Double>();
	}
	
	public static void main(String[] args) {
		JFrame fen = new JFrame();
		SoundVisualizer snd = new SoundVisualizer();
		Random rand = new Random();
		SFXRData sound = new SFXRData(rand.nextInt());
		sound.randomize();

		snd.updateHistogram(sound);
		sound.play();
		
		fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fen.add(snd);
		fen.pack();
		fen.setVisible(true);
	}

}
