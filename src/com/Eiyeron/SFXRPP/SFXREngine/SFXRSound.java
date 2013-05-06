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

/**
 * @author Eiyeron
 * @version 1.00 | Finished
 * A SFXRSOund contains sound array and functions to hear/write/change it
 */
public class SFXRSound {
	private byte[] pcm;
	private double[] pcm_double;
	private int sampleRate;

	/**
	 * Generates A sound directly from a chosen FX
	 * @param fx FX chosen
	 * @return The sound generated, ready-to-use
	 */
	public static SFXRSound SFXRSoundByPresets(FX fx) {
		SFXRPreset pre = new SFXRPreset(fx);
		SFXRSynth synth = new SFXRSynth(pre);
		return synth.synthSound();
	}
	
	/**
	 * Default constructor
	 * @param pcm Sound byte array
	 * @param sampleRate Sound's SampleRate
	 */
	public SFXRSound(byte[] pcm, int sampleRate) {
		this.pcm = pcm;
		convertByteArrayToDouble();
		this.sampleRate = sampleRate;

	}
	
	/**
	 * Default constructor
	 * @param pcm Sound double array
	 * @param sampleRate Sound's SampleRate
	 */
	public SFXRSound(double[] pcm, int sampleRate) {
		this.pcm_double = pcm;
		this.pcm = new byte[pcm.length];
		convertDoubleArrayToByte();
		this.sampleRate = sampleRate;

	}	
	
	/**
	 * Default constructor. Doesn't verify if the Byte array equals Double array
	 * @param pcm Sound byte array
	 * @param pcm_double double array
	 * @param sampleRate Sound's SampleRate
	 */
	public SFXRSound(byte[] pcm, double[] pcm_double, int sampleRate){
		this.pcm = pcm;
		this.pcm_double = pcm_double;
		this.sampleRate = sampleRate;
	}

	private void convertByteArrayToDouble() {
		this.pcm_double = new double[pcm.length];
		for(int i = 0; i < pcm.length; i++)
			pcm_double[i] = (int)pcm[i] / 127.;
	}
	
	private void convertDoubleArrayToByte() {
		this.pcm = new byte[pcm.length];
		for(int i = 0; i < pcm.length; i++)
			pcm[i] = (byte) (pcm[i] * 127);
	}
	
	/**
	 * @return Sound's byte array
	 */
	public byte[] getPcm() {
		return pcm;
	}

	/**
	 * @param pcm Sound's byte array
	 */
	public void setPcm(byte[] pcm) {
		this.pcm = pcm;
		convertByteArrayToDouble();
	}

	/**
	 * @return Sound's double array
	 */
	public double[] getPcm_double() {
		return pcm_double;
	}

	/**
	 * @param pcm Sound's double array
	 */
	public void setPcm_double(double[] pcm_double) {
		this.pcm_double = pcm_double;
		convertDoubleArrayToByte();
	}

	/**
	 * @return Sample rate
	 */
	public int getSampleRate() {
		return sampleRate;
	}

	/**
	 * @param sampleRate Sample Rate
	 */
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	/**
	 * Generates a new .wav file of the sound.
	 * @param snd Sound to write
	 * @param pathName File's path
	 * @param name File's name (without .wav)
	 * @throws IOException
	 */
	public static void writeToWav(SFXRSound snd, String pathName, String name)
			throws IOException { // Path, name.wav
		long length = snd.getPcm().length;
		InputStream bais = new ByteArrayInputStream(snd.getPcm());
		AudioFormat af = new AudioFormat(Encoding.PCM_SIGNED,
				snd.getSampleRate(), 8, 2, 2, snd.getSampleRate(), false);
		AudioInputStream aisTemp = new AudioInputStream(bais, af, length);
		File fileOut = new File(pathName + name + ".wav");
		AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

		if (AudioSystem.isFileTypeSupported(fileType, aisTemp)) {
			AudioSystem.write(aisTemp, fileType, fileOut);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		SFXRSound clone = new SFXRSound(pcm.clone(), sampleRate);
		return clone;
	}

	/**
	 * Plays the current sound
	 */
	public void play() {
		try {
			final SFXRSound soundThread;
			soundThread = (SFXRSound) this.clone();
			new Thread(new Runnable() {
				public void run() {
					try {

						final AudioFormat audioFormat = new AudioFormat(
								soundThread.sampleRate, 8, 1, true, true);
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
