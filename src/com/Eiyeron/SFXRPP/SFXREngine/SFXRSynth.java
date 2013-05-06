package com.Eiyeron.SFXRPP.SFXREngine;

import java.util.Random;

/**
 * @author Eiyeron
 * @version 1.00 | Finished
 *	SFXRSynth is the SFXR++ Sound Synth. It's used to generate sound from presets and give SFXRSound as results.
 */
public class SFXRSynth {

	private SFXRPreset snd;
	
	private boolean playing_sample = false;
	private int phase;
	private double fperiod;
	private double fmaxperiod;
	private double fslide;
	private double fdslide;
	private int period;
	private double square_duty;
	private double square_slide;
	private int env_stage;
	private int env_time;
	private int[] env_length = new int[3];
	private double env_vol;
	private double fphase;
	private double fdphase;
	private int iphase;
	private double[] phaser_buffer = new double[1024];
	private int ipp;
	private double[] noise_buffer = new double[32];
	private double fltp;
	private double fltdp;
	private double fltw;
	private double fltw_d;
	private double fltdmp;
	private double fltphp;
	private double flthp;
	private double flthp_d;
	private double vib_phase;
	private double vib_speed;
	private double vib_amp;
	private int rep_time;
	private int rep_limit;
	private int arp_time;
	private int arp_limit;
	private double arp_mod;

	private Random rnd;

	/**
	 * @param snd Sound to synth. don't synth directly.
	 */
	public SFXRSynth(SFXRPreset snd) {
		this.snd = snd;
		rnd = new Random(snd.seed);
		resetSample(false);
		playing_sample = true;
	}

	private double frnd(double v) {
		return rnd.nextDouble() * v;
	}

	private void resetSample(boolean restart) {
		if (!restart)
			phase = 0;
		fperiod = 100.0 / (snd.p_base_freq * snd.p_base_freq + 0.001);
		period = (int) fperiod;
		fmaxperiod = 100.0 / (snd.p_freq_limit * snd.p_freq_limit + 0.001);
		fslide = 1.0 - Math.pow((double) snd.p_freq_ramp, 3.0) * 0.01;
		fdslide = -Math.pow((double) snd.p_freq_dramp, 3.0) * 0.000001;
		square_duty = 0.5f - snd.p_duty * 0.5f;
		square_slide = -snd.p_duty_ramp * 0.00005f;
		if (snd.p_arp_mod >= 0.0f)
			arp_mod = 1.0 - Math.pow((double) snd.p_arp_mod, 2.0) * 0.9;
		else
			arp_mod = 1.0 + Math.pow((double) snd.p_arp_mod, 2.0) * 10.0;
		arp_time = 0;
		arp_limit = (int) (Math.pow(1.0f - snd.p_arp_speed, 2.0f) * 20000 + 32);
		if (snd.p_arp_speed == 1.0f)
			arp_limit = 0;
		if (!restart) {
			// reset filter
			fltp = 0.0f;
			fltdp = 0.0f;
			fltw = Math.pow(snd.p_lpf_freq, 3.0f) * 0.1f;
			fltw_d = 1.0f + snd.p_lpf_ramp * 0.0001f;
			fltdmp = 5.0f / (1.0f + Math.pow(snd.p_lpf_resonance, 2.0f) * 20.0f)
					* (0.01f + fltw);
			if (fltdmp > 0.8f)
				fltdmp = 0.8f;
			fltphp = 0.0f;
			flthp = Math.pow(snd.p_hpf_freq, 2.0f) * 0.1f;
			flthp_d = 1.0 + snd.p_hpf_ramp * 0.0003f;
			// reset vibrato
			vib_phase = 0.0f;
			vib_speed = Math.pow(snd.p_vib_speed, 2.0f) * 0.01f;
			vib_amp = snd.p_vib_strength * 0.5f;
			// reset envelope
			env_vol = 0.0f;
			env_stage = 0;
			env_time = 0;
			env_length[0] = (int) (snd.p_env_attack * snd.p_env_attack * 100000.0f);
			env_length[1] = (int) (snd.p_env_sustain * snd.p_env_sustain * 100000.0f);
			env_length[2] = (int) (snd.p_env_decay * snd.p_env_decay * 100000.0f);

			fphase = Math.pow(snd.p_pha_offset, 2.0f) * 1020.0f;
			if (snd.p_pha_offset < 0.0f)
				fphase = -fphase;
			fdphase = Math.pow(snd.p_pha_ramp, 2.0f) * 1.0f;
			if (snd.p_pha_ramp < 0.0f)
				fdphase = -fdphase;
			iphase = Math.abs((int) fphase);
			ipp = 0;
			for (int i = 0; i < 1024; i++)
				phaser_buffer[i] = 0.0f;

			for (int i = 0; i < 32; i++)
				noise_buffer[i] = frnd(2.0f) - 1.0f;

			rep_time = 0;
			rep_limit = (int) (Math.pow(1.0f - snd.p_repeat_speed, 2.0f) * 20000 + 32);
			if (snd.p_repeat_speed == 0.0f)
				rep_limit = 0;
		}
	}

	private double synthSample() {

		if (!playing_sample)
			return 0.0;

		rep_time++;
		if (rep_limit != 0 && rep_time >= rep_limit) {
			rep_time = 0;
			// println(this + " resetting sample " + rep_limit + " " +
			// rep_time);
			resetSample(true);
		}

		// frequency envelopes/arpeggios
		arp_time++;
		if (arp_limit != 0 && arp_time >= arp_limit) {
			arp_limit = 0;
			fperiod *= arp_mod;
		}
		fslide += fdslide;
		fperiod *= fslide;
		if (fperiod > fmaxperiod) {
			fperiod = fmaxperiod;
			if (snd.p_freq_limit > 0.0f)
				playing_sample = false;
		}
		double rfperiod = fperiod;
		if (vib_amp > 0.0f) {
			vib_phase += vib_speed;
			rfperiod = fperiod * (1.0 + Math.sin(vib_phase) * vib_amp);
		}
		period = (int) rfperiod;
		if (period < 8)
			period = 8;
		square_duty += square_slide;
		if (square_duty < 0.0f)
			square_duty = 0.0f;
		if (square_duty > 0.5f)
			square_duty = 0.5f;
		// volume envelope
		env_time++;
		if (env_stage > env_length.length || env_time > env_length[env_stage]) {
			env_time = 0;
			env_stage++;
			// System.out.println("Change of state");
			if (env_stage == 3) {
				playing_sample = false;
				// System.out.println("End of sound");
			}
		}
		// println(this + " env_length = " + Arrays.toString(env_length) +
		// " env_stage: "
		// + env_stage + " " + playing_sample);
		if (env_stage == 0) {
			env_vol = (double) env_time / env_length[0];
		}
		if (env_stage == 1) {
			env_vol = 1.0f
					+ Math.pow(1.0f - (double) env_time / env_length[1], 1.0f)
					* 2.0f * snd.p_env_punch;
		}
		if (env_stage == 2) {
			env_vol = 1.0f - (double) env_time / env_length[2];
		}

		// phaser step
		fphase += fdphase;
		iphase = Math.abs((int) fphase);
		if (iphase > 1023)
			iphase = 1023;

		if (flthp_d != 0.0f) {
			flthp *= flthp_d;
			if (flthp < 0.00001f)
				flthp = 0.00001f;
			if (flthp > 0.1f)
				flthp = 0.1f;
		}

		double ssample = 0.0f;
		for (int si = 0; si < 8; si++) // 8x supersampling
		{

			double sample = 0.0f;
			phase++;
			if (phase >= period) {
				// phase=0;
				phase %= period;
				if (snd.wave_type == WaveForm.NOISE) {
					for (int j = 0; j < 32; j++) {
						noise_buffer[j] = frnd(2.0f) - 1.0f;
					}
				}
			}

			// base waveform
			double fp = (double) phase / period;
			switch (snd.wave_type) {
			case SQUARE:
				if (fp < square_duty)
					sample = 0.5f;
				else
					sample = -0.5f;
				break;
			case SAWTOOTH: // sawtooth
				sample = 1.0f - fp * 2;
				break;
			case SINE: // sine
				sample = (double) Math.sin(fp * 2 * Math.PI);
				break;
			case NOISE: // noise
				sample = noise_buffer[phase * 32 / period];
				break;
			case TRIANGLE:
				sample = Math.abs(1 - fp * 2) - 1;
				break;
			case TAN:
				sample = Math.tan(Math.PI * fp);
				break;
			case WHISTLE:
				sample = 0.75 * Math.sin(fp * 2 * Math.PI) + 0.25
						* Math.sin(fp * 2 * 20 * Math.PI);
				break;
			case BREAKER:
				sample = Math.abs(1 - fp * fp * 2) - 1;
				break;
			}

			// lp filter
			double pp = fltp;
			fltw *= fltw_d;
			if (fltw < 0.0f)
				fltw = 0.0f;
			if (fltw > 0.1f)
				fltw = 0.1f;
			if (snd.p_lpf_freq != 1.0f) {
				fltdp += (sample - fltp) * fltw;
				fltdp -= fltdp * fltdmp;
			} else {
				fltp = sample;
				fltdp = 0.0f;
			}
			fltp += fltdp;
			// hp filter
			fltphp += fltp - pp;
			fltphp -= fltphp * flthp;
			sample = fltphp;

			// phaser
			phaser_buffer[ipp & 1023] = sample;
			sample += phaser_buffer[(ipp - iphase + 1024) & 1023];
			ipp = (ipp + 1) & 1023;

			// println(this + " " + ssample + " in supersampling nr " + si
			// + " wave_type: " + wave_type + " sample: " + sample
			// + " env_vol: " + env_vol);

			// final accumulation and envelope application
			ssample += sample * env_vol;
		}

		ssample = ssample / 8 * snd.master_vol;

		ssample *= 2.0f * snd.sound_vol;

		if (ssample > 1.0f)
			ssample = 1.0f;
		if (ssample < -1.0f)
			ssample = -1.0f;
		return ssample;
		// }
	}

	/**
	 * @return SFXRSound of the SFXRPreset given
	 */
	public SFXRSound synthSound() {
		int length = this.env_length[0] + this.env_length[1] + this.env_length[2];
		byte[] barray = new byte[length];
		double[] darray = new double[length];
		for (int i = 0; i < barray.length; i++) {
			length = i;
			double synth = this.synthSample();
			barray[i] = (byte) (synth * 127f);
			darray[i] = synth;
		}
		return new SFXRSound(barray, 44100);
	}
}
