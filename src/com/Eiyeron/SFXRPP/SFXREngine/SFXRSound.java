package com.Eiyeron.SFXRPP.SFXREngine;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SFXRSound {
	private byte[] pcm;
	private double[] pcm_double;
	private int bitRate;

	public static SFXRSound SFXRSoundByPresets(FX fx) {
		SFXRPreset pre = new SFXRPreset(fx);
		SFXRSynth synth = new SFXRSynth(pre);
		return synth.synthSound();
	}
	
	public SFXRSound(byte[] pcm, int bitRate) {
		this.pcm = pcm;
		this.pcm_double = new double[pcm.length];
		for(int i = 0; i < pcm.length; i++)
			pcm_double[i] = (int)pcm[i] / 127.;
		this.bitRate = bitRate;
	}
	
	public SFXRSound(byte[] pcm, double[] pcm_double, int bitRate){
		this.pcm = pcm;
		this.pcm_double = pcm_double;
		this.bitRate = bitRate;
	}

	public byte[] getPcm() {
		return pcm;
	}

	public void setPcm(byte[] pcm) {
		this.pcm = pcm;
	}

	public double[] getPcm_double() {
		return pcm_double;
	}

	public void setPcm_double(double[] pcm_double) {
		this.pcm_double = pcm_double;
	}

	public int getBitRate() {
		return bitRate;
	}

	public void setBitRate(int bitRate) {
		this.bitRate = bitRate;
	}

	public static void writeToWav(SFXRSound snd, String pathName, String name)
			throws IOException { // Path, name.wav
		long length = snd.getPcm().length;
		InputStream bais = new ByteArrayInputStream(snd.getPcm());
		AudioFormat af = new AudioFormat(Encoding.PCM_SIGNED,
				snd.getBitRate(), 8, 2, 2, snd.getBitRate(), false);
		AudioInputStream aisTemp = new AudioInputStream(bais, af, length);
		File fileOut = new File(pathName + name + ".wav");
		AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

		if (AudioSystem.isFileTypeSupported(fileType, aisTemp)) {
			AudioSystem.write(aisTemp, fileType, fileOut);
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		SFXRSound clone = new SFXRSound(pcm.clone(), bitRate);
		return clone;
	}

	public void play() {
		try {
			final SFXRSound soundThread;
			soundThread = (SFXRSound) this.clone();
			new Thread(new Runnable() {
				public void run() {
					try {

						final AudioFormat audioFormat = new AudioFormat(
								soundThread.bitRate, 8, 1, true, true);
						SourceDataLine line = AudioSystem
								.getSourceDataLine(audioFormat);
						line = AudioSystem.getSourceDataLine(audioFormat);
						line.open(audioFormat);
						line.start();

						// play the byteArray
						line.write(soundThread.getPcm(), 0,
								soundThread.getPcm().length);// (byte[] b,
						// int off,
						// int len)
						line.drain();
						line.flush();

						line.close();

					} catch (LineUnavailableException e) {
						System.err.println("Audio Error:\n\t" + e.getMessage()
								+ "\nExiting.");
					}
				}
			}).start();
		} catch (CloneNotSupportedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
	}

}
