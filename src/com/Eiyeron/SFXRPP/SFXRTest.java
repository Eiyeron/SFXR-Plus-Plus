package com.Eiyeron.SFXRPP;

import java.io.IOException;

import com.Eiyeron.SFXRPP.SFXREngine.FX;
import com.Eiyeron.SFXRPP.SFXREngine.SFXRPreset;
import com.Eiyeron.SFXRPP.SFXREngine.SFXRSound;
import com.Eiyeron.SFXRPP.SFXREngine.SFXRSynth;

/**
 * @author Eiyeron
 *	Simple test to hear the sound and write it in a wav file.
 * @version 1.00 | Finished
 */
public class SFXRTest {
	public static void main(String[] args) throws IOException {
		SFXRPreset pre = new SFXRPreset();
		pre.random(FX.HURT);
		SFXRSynth synth = new SFXRSynth(pre);
		SFXRSound snd = synth.synthSound();
		SFXRSound.writeToWav(snd, "/home/d12003702/Bureau/", "Test");
		
	}
	
}
