package com.Eiyeron.SFXRPP;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.Eiyeron.SFXRPP.SFXREngine.FX;
import com.Eiyeron.SFXRPP.SFXREngine.SFXRSound;

/**
  * SoundVizualizer shows the volume spectrum of the given sound. Used in RSFSR++ Soundbox.
  * @author Eiyeron / Florian DORMONT
  * @version 1.00 | Finished
**/

@SuppressWarnings("serial")
public class SoundVisualizer extends JPanel {
	private ArrayList<Double> histogram;
	
	public void updateHistogram(SFXRSound sound) {
		histogram.clear();
		for(int i = 0; i < sound.getPcm().length; ++i) {
			histogram.add(sound.getPcm_double()[i]);
		}
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
				double y = histogram.get(i*ecart)*getHeight();
				g.setColor(Color.red);
				g.fillRect((int)x , getHeight() / 2 - (int)(y/2), 2, (int)y);
			}			
		}
	}

	public SoundVisualizer() {
		super();
		setPreferredSize(new Dimension(300, 200));
		histogram = new ArrayList<Double>();
	}
	
	public static void main(String[] args) throws IOException {
		JFrame fen = new JFrame();
		SoundVisualizer snd = new SoundVisualizer();
		Random rand = new Random();
		SFXRSound sound = SFXRSound.SFXRSoundByPresets(FX.values()[rand.nextInt(FX.values().length)]);

		snd.updateHistogram(sound);
		sound.play();
		SFXRSound.writeToWav(sound, "/home/d12003702/", "Test");
		
		fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fen.add(snd);
		fen.pack();
		fen.setVisible(true);
	}

}
