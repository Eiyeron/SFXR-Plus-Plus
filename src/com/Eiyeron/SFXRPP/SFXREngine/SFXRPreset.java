package com.Eiyeron.SFXRPP.SFXREngine;

import java.util.Random;


/**
 * @author Eiyeron
 * @version 1.00 | Finished
 *	SFXR++ Sound presets. A sound is generated with theses options.
 *	Save/load functions are available.
 */
public class SFXRPreset {

	static Random rnd = new Random();
	
	public WaveForm wave_type = WaveForm.SQUARE;

	/**
	 * Start Frequency
	 */
	public double p_base_freq = 0;
	/**
	 * Minimal Frequency
	 */
	public double p_freq_limit = 0;
	/**
	 * Pitch Slide
	 */
	public double p_freq_ramp = 0;
	/**
	 * Pitch delta Slide
	 */
	public double p_freq_dramp = 0;
	/**
	 * Square Duty. Only used with square waveform
	 */
	public double p_duty = 0.5;
	/**
	 * Square Duty Slide
	 */
	public double p_duty_ramp = 0;

	/**
	 * Vibrato Depth
	 */
	public double p_vib_strength = 0;
	/**
	 * Vibrato Speed
	 */
	public double p_vib_speed = 0;

	/**
	 * Attack Time
	 */
	public double p_env_attack = 0;
	/**
	 * Sustain Time
	 */
	public double p_env_sustain = 0;
	/**
	 * Decay Time
	 */
	public double p_env_decay = 0;
	/**
	 * Sustain Punch
	 */
	public double p_env_punch = 0;

	/**
	 * LP Filter Resonance
	 */
	public double p_lpf_resonance = 0;
	/**
	 * LP Filter Cutoff
	 */
	public double p_lpf_freq = 0;
	/**
	 * LP Filter Cutoff Slide
	 */
	public double p_lpf_ramp = 0;
	/**
	 * HP Filter Cutoff
	 */
	public double p_hpf_freq;
	/**
	 * HP Filter Cutoff Slide
	 */
	public double p_hpf_ramp;

	/**
	 * Phaser Offset
	 */
	public double p_pha_offset = 0;
	/**
	 * Phaser Slide
	 */
	public double p_pha_ramp = 0;

	/**
	 * Repeat Speed
	 */
	public double p_repeat_speed = 0; // Repeat Speed

	/**
	 * Arpeggio Speed
	 */
	public double p_arp_speed = 0;
	/**
	 * Arpeggio Depth
	 */
	public double p_arp_mod = 0; // Change Amount

	/***
	 * Master Volume
	 */
	public double master_vol = 0.05f;
	/**
	 * Sound Volume
	 */
	public double sound_vol = 0.5f;
	
	/**
	 * Used to get uniform noises between each synth
	 */
	public int seed; // Random seed
	
	/**
	 * Generates a predefined preset.
	 * @param fx FX chosen
	 */
	public SFXRPreset(FX fx) {
		this();
		random(fx);
	}

	/**
	 * Just do it manually. Please.
	 */
	public SFXRPreset() {
		resetParams();
	}

	/**
	 * Clears the parameters to a default value
	 */
	public void resetParams() {
		wave_type = WaveForm.SQUARE;

		p_base_freq = 0.3f;
		p_freq_limit = 0.0f;
		p_freq_ramp = 0.0f;
		p_freq_dramp = 0.0f;
		p_duty = 0.0f;
		p_duty_ramp = 0.0f;

		p_vib_strength = 0.0f;
		p_vib_speed = 0.0f;

		p_env_attack = 0.0f;
		p_env_sustain = 0.3f;
		p_env_decay = 0.4f;
		p_env_punch = 0.0f;

		p_lpf_resonance = 0.0f;
		p_lpf_freq = 1.0f;
		p_lpf_ramp = 0.0f;
		p_hpf_freq = 0.0f;
		p_hpf_ramp = 0.0f;

		p_pha_offset = 0.0f;
		p_pha_ramp = 0.0f;

		p_repeat_speed = 0.0f;

		p_arp_speed = 0.0f;
		p_arp_mod = 0.0f;
	}
	
	/**
	 * Changes the current preset to a predefined preset.
	 * @param fx FX chosen
	 */
	public void random(FX fx) {

		switch (fx) {
		case PICKUP: // pickup/coin
			resetParams();
			p_base_freq = 0.4f + frnd(0.5f);
			p_env_attack = 0.0f;
			p_env_sustain = frnd(0.1f);
			p_env_decay = 0.1f + frnd(0.4f);
			p_env_punch = 0.3f + frnd(0.3f);
			if (rnd(1) > 0) {
				p_arp_speed = 0.5f + frnd(0.2f);
				p_arp_mod = 0.2f + frnd(0.4f);
				// println(this + " rnd(1) > 0 " + p_arp_speed + " " +
				// p_arp_mod);
			}
			// println(this + " setting " + p_base_freq + " " + p_env_sustain +
			// " " + p_env_decay + " " + p_env_punch);
			break;
		case LASER: // laser/shoot
			resetParams();
			wave_type = rndToWF(2);
			if (wave_type == WaveForm.SINE && rnd(1) > 0)
				wave_type = rndToWF(1);
			p_base_freq = 0.5f + frnd(0.5f);
			p_freq_limit = p_base_freq - 0.2f - frnd(0.6f);
			if (p_freq_limit < 0.2f)
				p_freq_limit = 0.2f;
			p_freq_ramp = -0.15f - frnd(0.2f);
			if (rnd(2) == 0) {
				p_base_freq = 0.3f + frnd(0.6f);
				p_freq_limit = frnd(0.1f);
				p_freq_ramp = -0.35f - frnd(0.3f);
			}
			if (rnd(1) > 0) {
				p_duty = frnd(0.5f);
				p_duty_ramp = frnd(0.2f);
			} else {
				p_duty = 0.4f + frnd(0.5f);
				p_duty_ramp = -frnd(0.7f);
			}
			p_env_attack = 0.0f;
			p_env_sustain = 0.1f + frnd(0.2f);
			p_env_decay = frnd(0.4f);
			if (rnd(1) > 0)
				p_env_punch = frnd(0.3f);
			if (rnd(2) == 0) {
				p_pha_offset = frnd(0.2f);
				p_pha_ramp = -frnd(0.2f);
			}
			if (rnd(1) > 0)
				p_hpf_freq = frnd(0.3f);
			break;
		case EXPLOSION: // explosion
			resetParams();
			wave_type = WaveForm.NOISE;
			if (rnd(1) > 0) {
				p_base_freq = 0.1f + frnd(0.4f);
				p_freq_ramp = -0.1f + frnd(0.4f);
			} else {
				p_base_freq = 0.2f + frnd(0.7f);
				p_freq_ramp = -0.2f - frnd(0.2f);
			}
			p_base_freq *= p_base_freq;
			if (rnd(4) == 0)
				p_freq_ramp = 0.0f;
			if (rnd(2) == 0)
				p_repeat_speed = 0.3f + frnd(0.5f);
			p_env_attack = 0.0f;
			p_env_sustain = 0.1f + frnd(0.3f);
			p_env_decay = frnd(0.5f);
			if (rnd(1) == 0) {
				p_pha_offset = -0.3f + frnd(0.9f);
				p_pha_ramp = -frnd(0.3f);
			}
			p_env_punch = 0.2f + frnd(0.6f);
			if (rnd(1) > 0) {
				p_vib_strength = frnd(0.7f);
				p_vib_speed = frnd(0.6f);
			}
			if (rnd(2) == 0) {
				p_arp_speed = 0.6f + frnd(0.3f);
				p_arp_mod = 0.8f - frnd(1.6f);
			}
			break;
		case POWERUP: // powerup
			resetParams();
			if (rnd(1) > 0)
				wave_type = WaveForm.SAWTOOTH;
			else
				p_duty = frnd(0.6f);
			if (rnd(1) > 0) {
				p_base_freq = 0.2f + frnd(0.3f);
				p_freq_ramp = 0.1f + frnd(0.4f);
				p_repeat_speed = 0.4f + frnd(0.4f);
			} else {
				p_base_freq = 0.2f + frnd(0.3f);
				p_freq_ramp = 0.05f + frnd(0.2f);
				if (rnd(1) > 0) {
					p_vib_strength = frnd(0.7f);
					p_vib_speed = frnd(0.6f);
				}
			}
			p_env_attack = 0.0f;
			p_env_sustain = frnd(0.4f);
			p_env_decay = 0.1f + frnd(0.4f);
			break;
		case HURT: // hit/hurt
			resetParams();
			wave_type = rndToWF(2);
			if (wave_type == WaveForm.SINE)
				wave_type = WaveForm.NOISE;
			if (wave_type == WaveForm.SQUARE)
				p_duty = frnd(0.6f);
			p_base_freq = 0.2f + frnd(0.6f);
			p_freq_ramp = -0.3f - frnd(0.4f);
			p_env_attack = 0.0f;
			p_env_sustain = frnd(0.1f);
			p_env_decay = 0.1f + frnd(0.2f);
			if (rnd(1) > 0)
				p_hpf_freq = frnd(0.3f);
			break;
		case JUMP: // jump
			resetParams();
			wave_type = WaveForm.SQUARE;
			p_duty = frnd(0.6f);
			p_base_freq = 0.3f + frnd(0.3f);
			p_freq_ramp = 0.1f + frnd(0.2f);
			p_env_attack = 0.0f;
			p_env_sustain = 0.1f + frnd(0.3f);
			p_env_decay = 0.1f + frnd(0.2f);
			if (rnd(1) > 0)
				p_hpf_freq = frnd(0.3f);
			if (rnd(1) > 0)
				p_lpf_freq = 1.0f - frnd(0.6f);
			break;
		case BEEP: // blip/select
			resetParams();
			wave_type = rndToWF(1);
			if (wave_type == WaveForm.NOISE)
				p_duty = frnd(0.6f);
			p_base_freq = 0.2f + frnd(0.4f);
			p_env_attack = 0.0f;
			p_env_sustain = 0.1f + frnd(0.1f);
			p_env_decay = frnd(0.2f);
			p_hpf_freq = 0.1f;
			break;
		default:
			break;
		}
	}

	/**
	 * RANDOMIZE ALL THAT THING!
	 */
	public void randomize() {
		p_base_freq = Math.pow(frnd(2.0f) - 1.0f, 2.0f);
		if (rnd(1) > 0)
			p_base_freq = Math.pow(frnd(2.0f) - 1.0f, 3.0f) + 0.5f;
		p_freq_limit = 0.0f;
		p_freq_ramp = Math.pow(frnd(2.0f) - 1.0f, 5.0f);
		if (p_base_freq > 0.7f && p_freq_ramp > 0.2f)
			p_freq_ramp = -p_freq_ramp;
		if (p_base_freq < 0.2f && p_freq_ramp < -0.05f)
			p_freq_ramp = -p_freq_ramp;
		p_freq_dramp = Math.pow(frnd(2.0f) - 1.0f, 3.0f);
		p_duty = frnd(2.0f) - 1.0f;
		p_duty_ramp = Math.pow(frnd(2.0f) - 1.0f, 3.0f);
		p_vib_strength = Math.pow(frnd(2.0f) - 1.0f, 3.0f);
		p_vib_speed = frnd(2.0f) - 1.0f;
		p_env_attack = Math.pow(frnd(2.0f) - 1.0f, 3.0f);
		p_env_sustain = Math.pow(frnd(2.0f) - 1.0f, 2.0f);
		p_env_decay = frnd(2.0f) - 1.0f;
		p_env_punch = Math.pow(frnd(0.8f), 2.0f);
		if (p_env_attack + p_env_sustain + p_env_decay < 0.2f) {
			p_env_sustain += 0.2f + frnd(0.3f);
			p_env_decay += 0.2f + frnd(0.3f);
		}
		p_lpf_resonance = frnd(2.0f) - 1.0f;
		p_lpf_freq = 1.0f - Math.pow(frnd(1.0f), 3.0f);
		p_lpf_ramp = Math.pow(frnd(2.0f) - 1.0f, 3.0f);
		if (p_lpf_freq < 0.1f && p_lpf_ramp < -0.05f)
			p_lpf_ramp = -p_lpf_ramp;
		p_hpf_freq = Math.pow(frnd(1.0f), 5.0f);
		p_hpf_ramp = Math.pow(frnd(2.0f) - 1.0f, 5.0f);
		p_pha_offset = Math.pow(frnd(2.0f) - 1.0f, 3.0f);
		p_pha_ramp = Math.pow(frnd(2.0f) - 1.0f, 3.0f);
		p_repeat_speed = frnd(2.0f) - 1.0f;
		p_arp_speed = frnd(2.0f) - 1.0f;
		p_arp_mod = frnd(2.0f) - 1.0f;
	}

	/**
	 * Slightly alters the parameters. Useful to avoid sound repetitiveness
	 */
	public void mutate() {

		if (rnd(1) > 0)
			p_base_freq += frnd(0.1f) - 0.05f;
		// if(rnd(1) > 0) p_freq_limit+=frnd(0.1f)-0.05f;
		if (rnd(1) > 0)
			p_freq_ramp += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_freq_dramp += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_duty += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_duty_ramp += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_vib_strength += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_vib_speed += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_env_attack += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_env_sustain += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_env_decay += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_env_punch += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_lpf_resonance += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_lpf_freq += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_lpf_ramp += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_hpf_freq += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_hpf_ramp += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_pha_offset += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_pha_ramp += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_repeat_speed += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_arp_speed += frnd(0.1f) - 0.05f;
		if (rnd(1) > 0)
			p_arp_mod += frnd(0.1f) - 0.05f;
	}

	private int clampInt(double x, int a, int b) {
		int intX = (int) x;
		return (intX < a ? a : (intX > b ? b : intX));
	}

	private int rnd(int v) {
		return clampInt(Math.round(rnd.nextDouble() * v), 0, v);
	}

	private double frnd(double v) {
		return rnd.nextDouble() * v;
	}
	
	private WaveForm rndToWF(int v) {
		return WaveForm.values()[rnd(v)];
	}

	/**
	 * A little wrapper to easier the usage of a Preset in a game
	 */
	public void play() {
		SFXRSynth syn = new SFXRSynth(this);
		syn.synthSound().play();
	}
	
}
