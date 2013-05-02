package com.Eiyeron.SFXRPP;

import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SFXRData {

	public WaveForm wave_type;

	public double p_base_freq; // Start Frequency
	public double p_freq_limit; // Min Frequency
	public double p_freq_ramp; // Slide
	public double p_freq_dramp; // Delta Slide
	public double p_duty; // Square Duty
	public double p_duty_ramp; // Duty Sweep

	public double p_vib_strength; // Vibrato Depth
	public double p_vib_speed; // Vibrato Speed
	public double p_vib_delay; // ???

	public double p_env_attack; // Attack Time
	public double p_env_sustain; // Sustain Time
	public double p_env_decay; // Decay Time
	public double p_env_punch; // Sustain Punch

	boolean filter_on; // ???
	public double p_lpf_resonance; // LP Filter Resonance
	public double p_lpf_freq; // LP Filter Cutoff
	public double p_lpf_ramp; // LP Filter Cutoff Sweep
	public double p_hpf_freq; // HP Filter Cutoff
	public double p_hpf_ramp; // HP Filter Cutoff Sweep

	public double p_pha_offset; // Phaser Offset
	public double p_pha_ramp; // Phaser Sweep

	public double p_repeat_speed; // Repeat Speed

	public double p_arp_speed; // Change Speed
	public double p_arp_mod; // Change Amount

	public double master_vol = 0.05f;

	public double sound_vol = 0.5f;

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

	public Random rnd;

	public SFXRData(int seed) {
		rnd = new Random(seed);
		resetParams();
		resetSample(false);
		playing_sample = true;
	}

	public int clampInt(double x, int a, int b) {
		int intX = (int) x;
		return (intX < a ? a : (intX > b ? b : intX));
	}

	double frnd(double v) {
		return rnd.nextDouble() * v;
	}

	public int rnd(int v) {
		return clampInt(Math.round(rnd.nextDouble() * v), 0, v);
	}

	public WaveForm rndToWF(int v) {
		return WaveForm.values()[rnd(v)];
	}

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
		p_vib_delay = 0.0f;

		p_env_attack = 0.0f;
		p_env_sustain = 0.3f;
		p_env_decay = 0.4f;
		p_env_punch = 0.0f;

		filter_on = false;
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

	public void resetSample(boolean restart) {
		if (!restart)
			phase = 0;
		fperiod = 100.0 / (p_base_freq * p_base_freq + 0.001);
		period = (int) fperiod;
		fmaxperiod = 100.0 / (p_freq_limit * p_freq_limit + 0.001);
		fslide = 1.0 - Math.pow((double) p_freq_ramp, 3.0) * 0.01;
		fdslide = -Math.pow((double) p_freq_dramp, 3.0) * 0.000001;
		square_duty = 0.5f - p_duty * 0.5f;
		square_slide = -p_duty_ramp * 0.00005f;
		if (p_arp_mod >= 0.0f)
			arp_mod = 1.0 - Math.pow((double) p_arp_mod, 2.0) * 0.9;
		else
			arp_mod = 1.0 + Math.pow((double) p_arp_mod, 2.0) * 10.0;
		arp_time = 0;
		arp_limit = (int) (Math.pow(1.0f - p_arp_speed, 2.0f) * 20000 + 32);
		if (p_arp_speed == 1.0f)
			arp_limit = 0;
		if (!restart) {
			// reset filter
			fltp = 0.0f;
			fltdp = 0.0f;
			fltw = Math.pow(p_lpf_freq, 3.0f) * 0.1f;
			fltw_d = 1.0f + p_lpf_ramp * 0.0001f;
			fltdmp = 5.0f / (1.0f + Math.pow(p_lpf_resonance, 2.0f) * 20.0f)
					* (0.01f + fltw);
			if (fltdmp > 0.8f)
				fltdmp = 0.8f;
			fltphp = 0.0f;
			flthp = Math.pow(p_hpf_freq, 2.0f) * 0.1f;
			flthp_d = 1.0 + p_hpf_ramp * 0.0003f;
			// reset vibrato
			vib_phase = 0.0f;
			vib_speed = Math.pow(p_vib_speed, 2.0f) * 0.01f;
			vib_amp = p_vib_strength * 0.5f;
			// reset envelope
			env_vol = 0.0f;
			env_stage = 0;
			env_time = 0;
			env_length[0] = (int) (p_env_attack * p_env_attack * 100000.0f);
			env_length[1] = (int) (p_env_sustain * p_env_sustain * 100000.0f);
			env_length[2] = (int) (p_env_decay * p_env_decay * 100000.0f);

			fphase = Math.pow(p_pha_offset, 2.0f) * 1020.0f;
			if (p_pha_offset < 0.0f)
				fphase = -fphase;
			fdphase = Math.pow(p_pha_ramp, 2.0f) * 1.0f;
			if (p_pha_ramp < 0.0f)
				fdphase = -fdphase;
			iphase = Math.abs((int) fphase);
			ipp = 0;
			for (int i = 0; i < 1024; i++)
				phaser_buffer[i] = 0.0f;

			for (int i = 0; i < 32; i++)
				noise_buffer[i] = frnd(2.0f) - 1.0f;

			rep_time = 0;
			rep_limit = (int) (Math.pow(1.0f - p_repeat_speed, 2.0f) * 20000 + 32);
			if (p_repeat_speed == 0.0f)
				rep_limit = 0;
		}
	}

	public double synthSample() {

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
			if (p_freq_limit > 0.0f)
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
					* 2.0f * p_env_punch;
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
				if (wave_type == WaveForm.NOISE) {
					for (int j = 0; j < 32; j++) {
						noise_buffer[j] = frnd(2.0f) - 1.0f;
					}
				}
			}

			// base waveform
			double fp = (double) phase / period;
			switch (wave_type) {
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
			if (p_lpf_freq != 1.0f) {
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

		ssample = ssample / 8 * master_vol;

		ssample *= 2.0f * sound_vol;

		if (ssample > 1.0f)
			ssample = 1.0f;
		if (ssample < -1.0f)
			ssample = -1.0f;
		return ssample;
		// }
	}

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
		case BIP: // blip/select
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
		p_vib_delay = frnd(2.0f) - 1.0f;
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
			p_vib_delay += frnd(0.1f) - 0.05f;
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

	public byte[] synthSound(int length) {
		byte[] barray = new byte[length];
		for (int i = 0; i < barray.length; i++) {
			length = i;
			double synth = this.synthSample();
			barray[i] = (byte) (synth * 127f);
		}
		return barray;
	}

	public int getLength() {
		return env_length[0] + env_length[1] + env_length[2];
	}

	public void play() {
		final SFXRData soundThread = this.clone();
		new Thread(new Runnable() {
			public void run() {
				try {
					soundThread.resetSample(false);
					final int sampleRate = 44000;

					final AudioFormat audioFormat = new AudioFormat(sampleRate,
							8, 1, true, true);
					SourceDataLine line = AudioSystem
							.getSourceDataLine(audioFormat);
					line = AudioSystem.getSourceDataLine(audioFormat);
					line.open(audioFormat);
					line.start();

					int length = soundThread.getLength();
					byte[] barray = soundThread.synthSound(length);
					// play the byteArray
					line.write(barray, 0, length);// (byte[] b,
													// int off,
													// int len)
					line.drain();
					line.flush();

					line.close();

				} catch (LineUnavailableException e) {
					System.out.println("Sound Error : ");
					e.printStackTrace();
				}
			}
		}).start();
		this.resetSample(false);
	}

	public SFXRData clone() {
		SFXRData clone = new SFXRData(0);
		clone.master_vol = master_vol;
		clone.p_arp_mod = p_arp_mod;
		clone.p_arp_speed = p_arp_speed;
		clone.p_base_freq = p_base_freq;
		clone.p_duty = p_duty;
		clone.p_duty_ramp = p_duty_ramp;
		clone.p_env_attack = p_env_attack;
		clone.p_env_decay = p_env_decay;
		clone.p_env_punch = p_env_punch;
		clone.p_env_sustain = p_env_sustain;
		clone.p_freq_dramp = p_freq_dramp;
		clone.p_freq_limit = p_freq_limit;
		clone.p_freq_ramp = p_freq_ramp;
		clone.p_hpf_freq = p_hpf_freq;
		clone.p_hpf_ramp = p_hpf_ramp;
		clone.p_lpf_freq = p_lpf_freq;
		clone.p_lpf_ramp = p_lpf_ramp;
		clone.p_lpf_resonance = p_lpf_resonance;
		clone.p_pha_offset = p_pha_offset;
		clone.p_pha_ramp = p_pha_ramp;
		clone.p_vib_delay = p_vib_delay;
		clone.p_vib_speed = p_vib_speed;
		clone.p_vib_strength = p_vib_strength;
		clone.wave_type = wave_type;
		clone.resetSample(false);
		return clone;
	}

	public static void main(String[] args) {

		Random rand = new Random();
		SFXRData sound = new SFXRData(rand.nextInt());
		sound.randomize();
		sound.play();
	}
}
