package com.Eiyeron.SFXRPP;

import java.io.IOException;

import com.Eiyeron.SFXRPP.SFXREngine.FX;
import com.Eiyeron.SFXRPP.SFXREngine.SFXRPreset;
import com.Eiyeron.SFXRPP.SFXREngine.SFXRSound;
import com.Eiyeron.SFXRPP.SFXREngine.SFXRSynth;

public class SFXRTest {
	public static void main(String[] args) throws IOException {
		SFXRPreset pre = new SFXRPreset();
		pre.random(FX.POWERUP);
		SFXRSynth synth = new SFXRSynth(pre);
		SFXRSound snd = synth.synthSound();
		SFXRSound.writeToWav(snd, "/home/d12003702/Bureau/", "Test");
		
	}
	
}
