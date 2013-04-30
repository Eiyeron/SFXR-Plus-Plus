package com.Eiyeron.SFXRPP;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.springworldgames.SFXR.FX;
import com.springworldgames.SFXR.SFXRData;
import com.springworldgames.SFXR.WaveForm;

public class SFXRpp extends JFrame implements ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4648172894076113183L;

	Random rand;
	SFXRData sound;

	JSlider sliders[];
	JLabel label[];
	JLabel value[];
	JSlider volume;
	JLabel volumeVal;

	JButton play;
	JButton random;
	JButton mutate;
	JButton open;
	JButton save;

	JButton[] options;

	JSpinner waveForm;
	JPanel slidersContainer;
	JPanel presetsContainer;
	
	SoundVisualizer soundVis;

	final int sliderPrecision = 10000;

	private String fieldsName[] = { "WaveForm", "Start Frequency", "Slide",
			"Delta Slide", "Square Duty", "Duty Sweep", "Vibrato Strength",
			"Vibrato Speed", "Attack", "Sustain", "Decay", "Punch",
			"LP Filter Resonance", "LP Filter Cutoff", " LP Filter Sweep",
			"HP Filter Cutoff", " HP Filter Sweep", "Phaser Offset",
			"Phaser Sweep", "Repeat Speed", "Change Amount", "Change Speed" };

	public void initTopButtons() {
		play = new JButton("Play");
		play.addActionListener(this);

		random = new JButton("Random");
		random.addActionListener(this);

		mutate = new JButton("Mutate");
		mutate.addActionListener(this);

		open = new JButton("Open");
		open.addActionListener(this);

		save = new JButton("Save");
		save.addActionListener(this);

		getContentPane().add(play, "growx");
		getContentPane().add(random, "split 2, center");
		getContentPane().add(mutate);
		getContentPane().add(open, "split 2, center");
		getContentPane().add(save, "wrap");
	}

	public void initPresets() {
		presetsContainer = new JPanel(new MigLayout());
		options = new JButton[FX.values().length];
		for (int i = 0; i < options.length; i++) {
			options[i] = new JButton(FX.values()[i].toString());
			options[i].addActionListener(this);
			presetsContainer.add(options[i], "center, wrap, growx");
		}

		getContentPane().add(presetsContainer, "west, growy");
	}

	public void initSliders() {
		for (int i = 0; i < label.length; i++) {
			if (i + 1 < fieldsName.length)
				label[i] = new JLabel(fieldsName[i + 1]);
			else
				label[i] = new JLabel("l" + i);

			if (i == 1 || i == 2 || i == 4 || i == 13 || i == 15 || i == 16
					|| i == 17) {
				sliders[i] = new JSlider(-sliderPrecision / 2,
						sliderPrecision / 2, 0);
				sliders[i].setMajorTickSpacing(sliderPrecision / 2);
			} else {
				sliders[i] = new JSlider(0, sliderPrecision, 0);
				sliders[i].setMajorTickSpacing(sliderPrecision);
			}

			sliders[i].setPaintTicks(true);
			sliders[i].setMajorTickSpacing(sliderPrecision / 2);

			slidersContainer.add(label[i]);
			slidersContainer.add(sliders[i], "growx");

			value[i] = new JLabel(String.valueOf(sliders[i].getValue()
					/ (float) sliderPrecision));
			slidersContainer.add(value[i], "span, wrap, growx");

			if (i == 4 || i == 6 || i == 10 || i == 15 | i == 17 || i == 18) {
				JSeparator sep = new JSeparator();
				sep.setPreferredSize(new Dimension(120, 2));
				slidersContainer.add(sep, "span");
			}
		}

	}

	public void initWin() {
		getContentPane().setLayout(new MigLayout("center, fill"));
		soundVis = new SoundVisualizer();
		getContentPane().add(soundVis, "south");

		initTopButtons();
		initPresets();

		getContentPane().add(new JLabel("Wave Form"), "center");

		waveForm = new JSpinner(new SpinnerListModel(WaveForm.values()));
		getContentPane().add(waveForm, "span, growx, wrap");

		volume = new JSlider(0, sliderPrecision);
		volumeVal = new JLabel();
		volume.addChangeListener(this);

		getContentPane().add(new JLabel("Master Volume"));
		getContentPane().add(volume, "growx");
		getContentPane().add(volumeVal, "wrap");

		slidersContainer = new JPanel(new MigLayout("center, fill"));

		initSliders();
		for (int i = 0; i < sliders.length; i++) {
			sliders[i].addChangeListener(this);
		}

		JScrollPane slidersC = new JScrollPane(slidersContainer,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(slidersC, "growx, span, wrap");
		
	}

	public void updateOptions() {

		waveForm.setValue(sound.wave_type);

		sliders[0].setValue((int) (sound.p_base_freq * sliderPrecision));
		sliders[1].setValue((int) (sound.p_freq_ramp * sliderPrecision / 2));
		sliders[2].setValue((int) (sound.p_freq_dramp * sliderPrecision / 2));
		sliders[3].setValue((int) (sound.p_duty * sliderPrecision));
		sliders[4].setValue((int) (sound.p_duty_ramp * sliderPrecision / 2));

		sliders[5].setValue((int) (sound.p_duty * sliderPrecision));
		sliders[6].setValue((int) (sound.p_duty * sliderPrecision));

		sliders[7].setValue((int) (sound.p_env_attack * sliderPrecision));
		sliders[8].setValue((int) (sound.p_env_sustain * sliderPrecision));
		sliders[9].setValue((int) (sound.p_env_decay * sliderPrecision));
		sliders[10].setValue((int) (sound.p_env_punch * sliderPrecision));

		sliders[11].setValue((int) (sound.p_lpf_resonance * sliderPrecision));
		sliders[12].setValue((int) (sound.p_lpf_freq * sliderPrecision));
		sliders[13].setValue((int) (sound.p_lpf_ramp * sliderPrecision / 2));
		sliders[14].setValue((int) (sound.p_hpf_freq * sliderPrecision));
		sliders[15].setValue((int) (sound.p_hpf_ramp * sliderPrecision / 2));

		sliders[16].setValue((int) (sound.p_pha_offset * sliderPrecision / 2));
		sliders[17].setValue((int) (sound.p_pha_ramp * sliderPrecision / 2));

		sliders[18].setValue((int) (sound.p_repeat_speed * sliderPrecision));

		sliders[19].setValue((int) (sound.p_arp_mod * sliderPrecision));
		sliders[20].setValue((int) (sound.p_arp_speed * sliderPrecision));

		volume.setValue((int) (sound.master_vol * sliderPrecision));

	}

	public void updateLabels() {
		for (int i = 0; i < value.length; i++) {
			value[i].setText(String.valueOf(sliders[i].getValue()
					/ (float) sliderPrecision));
		}
		volumeVal.setText(String.valueOf(volume.getValue()
				/ (float) sliderPrecision));
	}

	public void updateVis() {
		soundVis.updateHistogram(sound);
		soundVis.repaint();
	}
	
	public SFXRpp() {
		this.setTitle("SFXR++ Soundbox");
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		rand = new Random();
		sound = new SFXRData(rand.nextInt());
		sound.randomize();
		sliders = new JSlider[fieldsName.length - 1];
		label = new JLabel[fieldsName.length - 1];
		value = new JLabel[fieldsName.length - 1];

		initWin();
		updateOptions();
		updateLabels();
		this.pack();
		this.setSize(getContentPane().getPreferredSize().width + 20,
				getContentPane().getPreferredSize().height);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String composant = evt.getActionCommand();
		FX preset = null;
		try {
			preset = FX.valueOf(composant);
		} catch (Exception e) {
		}

		if(composant == "Play") {
			updateVis();
			doSound();
		}
		else if (composant == "Random") {
			sound.randomize();
			updateOptions();
			updateVis();
			doSound();

		} else if (composant == "Mutate") {
			sound.mutate();
			updateVis();
			doSound();

		} else if (composant == "Save") {
			System.out.println("Saving");
			FileWriter fw;
			try {
				fw = new FileWriter(new File("saved.sfp"));
				WaveForm wf = (WaveForm) (waveForm.getValue());
				fw.write(wf.ordinal());
				for (JSlider js : sliders) {
					fw.write(js.getValue());
					System.out.println(js.getValue());
				}
				fw.flush();
				fw.close();

			} catch (IOException e) {
				System.err.println("Saving Error");
				e.printStackTrace();
			}
		} else if (preset != null) {
			sound.random(preset);
			updateOptions();
			doSound();	
			updateVis();			
		}
	}

	public void doSound() {
		sound.resetParams();
		sound.resetSample(false);
		sound.wave_type = (WaveForm) waveForm.getValue();
		sound.p_base_freq = sliders[0].getValue() / (float) sliderPrecision;

		sound.p_freq_ramp = sliders[1].getValue()
				/ (float) (sliderPrecision / 2);
		sound.p_freq_dramp = sliders[2].getValue()
				/ (float) (sliderPrecision / 2);
		sound.p_duty = sliders[3].getValue() / (float) sliderPrecision;
		sound.p_duty_ramp = sliders[4].getValue()
				/ (float) (sliderPrecision / 2);

		sound.p_vib_strength = sliders[5].getValue() / (float) sliderPrecision;
		sound.p_vib_speed = sliders[6].getValue() / (float) sliderPrecision;

		sound.p_env_attack = sliders[7].getValue() / (float) sliderPrecision;
		sound.p_env_sustain = sliders[8].getValue() / (float) sliderPrecision;
		sound.p_env_decay = sliders[9].getValue() / (float) sliderPrecision;
		sound.p_env_punch = sliders[10].getValue() / (float) sliderPrecision;

		sound.p_lpf_resonance = sliders[11].getValue()
				/ (float) sliderPrecision;
		sound.p_lpf_freq = sliders[12].getValue() / (float) sliderPrecision;
		sound.p_lpf_ramp = sliders[13].getValue()
				/ (float) (sliderPrecision / 2);
		sound.p_hpf_freq = sliders[14].getValue() / (float) sliderPrecision;
		sound.p_hpf_ramp = sliders[15].getValue()
				/ (float) (sliderPrecision / 2);

		sound.p_pha_offset = sliders[16].getValue()
				/ (float) (sliderPrecision / 2);
		sound.p_pha_ramp = sliders[17].getValue()
				/ (float) (sliderPrecision / 2);

		sound.p_repeat_speed = sliders[18].getValue() / (float) sliderPrecision;

		sound.p_arp_mod = sliders[19].getValue() / (float) sliderPrecision;
		sound.p_arp_speed = sliders[20].getValue() / (float) sliderPrecision;

		sound.master_vol = volume.getValue() / (float) sliderPrecision;

		sound.play();
	}

	public static void main(String args[]) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("Slider.paintValue", Boolean.FALSE);

		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		SFXRpp main = new SFXRpp();
		System.out.println(main.getName());

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		updateLabels();
	}
}
