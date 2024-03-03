package duylt.mobile.app.ecommerce.virgomusic.libs.equalizer;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;


import duylt.mobile.app.ecommerce.virgomusic.R;
import duylt.mobile.app.ecommerce.virgomusic.presentation.ui.act.home.PresetsActivity;

public class EqualizerFragment extends Fragment {

    public static final String ARG_AUDIO_SESSIOIN_ID = "audio_session_id";

    static int themeColor = Color.parseColor("#B24242");
    public Equalizer mEqualizer;
    ImageView equalizerSwitch;
    public BassBoost bassBoost;
    public PresetReverb presetReverb;
    ImageView backBtn;

    int y = 0;
    LinearLayout mLinearLayout;

    SeekBar[] seekBarFinal = new SeekBar[5];

    TextView presetSpinner;


    Context ctx;
    CardView containerEqualizerType;

    public EqualizerFragment() {
        // Required empty public constructor
    }
    float[] points;
    short numberOfFrequencyBands;
    private int audioSesionId;
    static boolean showBackButton = true;

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    int position = result.getData().getIntExtra("index_equalizer_type", 0);
                    Log.d("heelo", position + "");

                    switch (position) {
                        case 0: {
                            presetSpinner.setText("Normal");
                            break;
                        } case 1: {
                            presetSpinner.setText("Classical");
                            break;
                        } case 2: {
                            presetSpinner.setText("Dance");
                            break;
                        } case 3: {
                            presetSpinner.setText("Flat");
                            break;
                        } case 4: {
                            presetSpinner.setText("Folk");
                            break;
                        } case 5: {
                            presetSpinner.setText("Heavy Metal");
                            break;
                        } case 6: {
                            presetSpinner.setText("Hip Hop");
                            break;
                        } case 7: {
                            presetSpinner.setText("Jazz");
                            break;
                        } case 8: {
                            presetSpinner.setText("Pop");
                            break;
                        } case 9: {
                            presetSpinner.setText("Rock");
                            break;
                        }
                    }

                    try {
                        mEqualizer.usePreset((short) (position));
                        Settings.presetPos = position;
                        short numberOfFreqBands = 5;

                        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];

                        for (short i = 0; i < numberOfFreqBands; i++) {
                            seekBarFinal[i].setProgress(mEqualizer.getBandLevel(i) - lowerEqualizerBandLevel);
                            points[i] = mEqualizer.getBandLevel(i) - lowerEqualizerBandLevel;
                            Settings.seekbarpos[i] = mEqualizer.getBandLevel(i);
                            Settings.equalizerModel.getSeekbarpos()[i] = mEqualizer.getBandLevel(i);
                        }
                    } catch (Exception e) {
                        Toast.makeText(ctx, "Error while updating Equalizer", Toast.LENGTH_SHORT).show();
                    }
                    Settings.equalizerModel.setPresetPos(position);
                }
            });

    public static EqualizerFragment newInstance(int audioSessionId) {

        Bundle args = new Bundle();
        args.putInt(ARG_AUDIO_SESSIOIN_ID, audioSessionId);

        EqualizerFragment fragment = new EqualizerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Settings.isEditing = true;

        if (getArguments() != null && getArguments().containsKey(ARG_AUDIO_SESSIOIN_ID)) {
            audioSesionId = getArguments().getInt(ARG_AUDIO_SESSIOIN_ID);
        }

        if (Settings.equalizerModel == null) {
            Settings.equalizerModel = new EqualizerModel();
            Settings.equalizerModel.setReverbPreset(PresetReverb.PRESET_NONE);
            Settings.equalizerModel.setBassStrength((short) (1000 / 19));
        }

        mEqualizer = new Equalizer(0, audioSesionId);
        bassBoost = new BassBoost(0, audioSesionId);
        bassBoost.setEnabled(Settings.isEqualizerEnabled);
        BassBoost.Settings bassBoostSettingTemp = bassBoost.getProperties();
        BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
        bassBoostSetting.strength = Settings.equalizerModel.getBassStrength();
        bassBoost.setProperties(bassBoostSetting);

        presetReverb = new PresetReverb(0, audioSesionId);
        presetReverb.setPreset(Settings.equalizerModel.getReverbPreset());
        presetReverb.setEnabled(Settings.isEqualizerEnabled);

        mEqualizer.setEnabled(Settings.isEqualizerEnabled);

        if (Settings.presetPos == 0) {
            for (short bandIdx = 0; bandIdx < mEqualizer.getNumberOfBands(); bandIdx++) {
                mEqualizer.setBandLevel(bandIdx, (short) Settings.seekbarpos[bandIdx]);
            }
        } else {
            mEqualizer.usePreset((short) Settings.presetPos);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ctx = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equalizer, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = view.findViewById(R.id.iconBack);
        backBtn.setVisibility(showBackButton ? View.VISIBLE : View.GONE);
        backBtn.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });


        equalizerSwitch = view.findViewById(R.id.switchEqualizer);
        equalizerSwitch.setActivated(false);
        equalizerSwitch.setOnClickListener(_view -> {
            equalizerSwitch.setActivated(
                    !equalizerSwitch.isActivated()
            );
            boolean isChecked = equalizerSwitch.isActivated();
            mEqualizer.setEnabled(isChecked);
            bassBoost.setEnabled(isChecked);
            presetReverb.setEnabled(isChecked);
            Settings.isEqualizerEnabled = isChecked;
            Settings.equalizerModel.setEqualizerEnabled(isChecked);
        });

        // init
        presetSpinner = view.findViewById(R.id.textEqualizerType);
        presetSpinner.setText("Normal");

        containerEqualizerType = view.findViewById(R.id.containerEqualizerType);
        containerEqualizerType.setOnClickListener(_view -> {
            Intent intent = new Intent(ctx, PresetsActivity.class);
            intent.putExtra("eq_type", presetSpinner.getText().toString().trim());
            someActivityResultLauncher.launch(intent);
        });

        mLinearLayout = view.findViewById(R.id.equalizerContainer);

        TextView equalizerHeading = new TextView(getContext());
        equalizerHeading.setText(R.string.equalizer);
        equalizerHeading.setTextSize(20);
        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);

        numberOfFrequencyBands = 5;

        points = new float[numberOfFrequencyBands];

        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
        final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < numberOfFrequencyBands; i++) {
            final short equalizerBandIndex = i;
            final TextView frequencyHeaderTextView = new TextView(getContext());
            frequencyHeaderTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            frequencyHeaderTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextView.setTextColor(Color.parseColor("#212121"));
            frequencyHeaderTextView.setText((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + "Hz");

            LinearLayout seekBarRowLayout = new LinearLayout(getContext());
            seekBarRowLayout.setOrientation(LinearLayout.VERTICAL);

            TextView lowerEqualizerBandLevelTextView = new TextView(getContext());
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            lowerEqualizerBandLevelTextView.setTextColor(Color.parseColor("#212121"));
            lowerEqualizerBandLevelTextView.setText((lowerEqualizerBandLevel / 100) + "dB");

            TextView upperEqualizerBandLevelTextView = new TextView(getContext());
            lowerEqualizerBandLevelTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            upperEqualizerBandLevelTextView.setTextColor(Color.parseColor("#212121"));
            upperEqualizerBandLevelTextView.setText((upperEqualizerBandLevel / 100) + "dB");

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.weight = 1;

            SeekBar seekBar = new SeekBar(getContext());
            TextView textView = new TextView(getContext());
            switch (i) {
                case 0:
                    seekBar = view.findViewById(R.id.seekBar1);
                    textView = view.findViewById(R.id.textView1);
                    break;
                case 1:
                    seekBar = view.findViewById(R.id.seekBar2);
                    textView = view.findViewById(R.id.textView2);
                    break;
                case 2:
                    seekBar = view.findViewById(R.id.seekBar3);
                    textView = view.findViewById(R.id.textView3);
                    break;
                case 3:
                    seekBar = view.findViewById(R.id.seekBar4);
                    textView = view.findViewById(R.id.textView4);
                    break;
                case 4:
                    seekBar = view.findViewById(R.id.seekBar5);
                    textView = view.findViewById(R.id.textView5);
                    break;
            }
            seekBarFinal[i] = seekBar;
            seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN));
            seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(themeColor, PorterDuff.Mode.SRC_IN));
            seekBar.setId(i);
//            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);

            textView.setText(frequencyHeaderTextView.getText());
            textView.setTextColor(
                    getResources().getColor(R.color.color_text_primary)
            );
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            if (Settings.isEqualizerReloaded) {
                points[i] = Settings.seekbarpos[i] - lowerEqualizerBandLevel;
                seekBar.setProgress(Settings.seekbarpos[i] - lowerEqualizerBandLevel);
            } else {
                points[i] = mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel;
                seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
                Settings.seekbarpos[i] = mEqualizer.getBandLevel(equalizerBandIndex);
                Settings.isEqualizerReloaded = true;
            }

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mEqualizer.setBandLevel(equalizerBandIndex, (short) (progress + lowerEqualizerBandLevel));
                    points[seekBar.getId()] = mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel;
                    Settings.seekbarpos[seekBar.getId()] = (progress + lowerEqualizerBandLevel);
                    Settings.equalizerModel.getSeekbarpos()[seekBar.getId()] = (progress + lowerEqualizerBandLevel);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    presetSpinner.setText("Custom");
                    Settings.presetPos = 0;
                    Settings.equalizerModel.setPresetPos(0);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        Button mEndButton = new Button(getContext());
        mEndButton.setBackgroundColor(themeColor);
        mEndButton.setTextColor(Color.WHITE);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEqualizer != null) {
            mEqualizer.release();
        }

        if (bassBoost != null) {
            bassBoost.release();
        }

        if (presetReverb != null) {
            presetReverb.release();
        }

        Settings.isEditing = false;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private int id = -1;

        public Builder setAudioSessionId(int id) {
            this.id = id;
            return this;
        }

        public Builder setAccentColor(int color) {
            themeColor = color;
            return this;
        }

        public Builder setShowBackButton(boolean show) {
            showBackButton = show;
            return this;
        }

        public EqualizerFragment build() {
            return EqualizerFragment.newInstance(id);
        }
    }


}
