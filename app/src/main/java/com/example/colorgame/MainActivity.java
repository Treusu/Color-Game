package com.example.colorgame;

import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private GameLogic gameLogic;
    private TextView tvBalance, tvDie1, tvDie2, tvDie3, tvHistory, tvSelectedColorInfo;
    private EditText etBetAmount;
    private Button btnRoll, btnIncreaseBet, btnDecreaseBet, btnCashIn, btnCashOut;
    private Button[] colorButtons;
    private final Handler handler = new Handler();
    private boolean isRolling = false;

    // Sound features
    private SoundPool soundPool;
    private int[] soundIds;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameLogic = new GameLogic();

        initViews();
        initSounds();
        setupClickListeners();
        setupTextWatcher();

        if (savedInstanceState != null) {
            gameLogic.setBalance(savedInstanceState.getDouble("balance"));
            gameLogic.setSelectedColor((GameLogic.GameColor) savedInstanceState.getSerializable("selectedColor"));
            gameLogic.setHistory((java.util.List<GameLogic.GameResult>) savedInstanceState.getSerializable("history"));
            gameLogic.setCurrentBet(savedInstanceState.getDouble("currentBet"));
        }

        updateUI();
    }

    private void initViews() {
        tvBalance = findViewById(R.id.tvBalance);
        etBetAmount = findViewById(R.id.etBetAmount);
        tvDie1 = findViewById(R.id.tvDie1);
        tvDie2 = findViewById(R.id.tvDie2);
        tvDie3 = findViewById(R.id.tvDie3);
        tvHistory = findViewById(R.id.tvHistory);
        tvSelectedColorInfo = findViewById(R.id.tvSelectedColorInfo);
        btnRoll = findViewById(R.id.btnRoll);
        btnIncreaseBet = findViewById(R.id.btnIncreaseBet);
        btnDecreaseBet = findViewById(R.id.btnDecreaseBet);
        btnCashIn = findViewById(R.id.btnCashIn);
        btnCashOut = findViewById(R.id.btnCashOut);

        colorButtons = new Button[]{
                findViewById(R.id.btnRed),
                findViewById(R.id.btnBlue),
                findViewById(R.id.btnGreen),
                findViewById(R.id.btnYellow),
                findViewById(R.id.btnWhite),
                findViewById(R.id.btnViolet)
        };
    }

    private void initSounds() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        // Note: Resource names must be lowercase and use underscores (no hyphens)
        // Ensure your files in res/raw are renamed to match these IDs
        soundIds = new int[2];
        soundIds[0] = soundPool.load(this, R.raw.letitride, 1);
        soundIds[1] = soundPool.load(this, R.raw.letsgogambling, 1);
    }

    private void setupClickListeners() {
        for (int i = 0; i < colorButtons.length; i++) {
            final int index = i;
            colorButtons[i].setOnClickListener(v -> {
                if (isRolling) return;
                gameLogic.setSelectedColor(GameLogic.GameColor.values()[index]);
                updateUI();
            });
        }

        btnIncreaseBet.setOnClickListener(v -> {
            if (gameLogic.increaseBet()) {
                etBetAmount.setText(String.format(Locale.US, "%.2f", gameLogic.getCurrentBet()));
                updateUI();
            }
        });

        btnDecreaseBet.setOnClickListener(v -> {
            if (gameLogic.decreaseBet()) {
                etBetAmount.setText(String.format(Locale.US, "%.2f", gameLogic.getCurrentBet()));
                updateUI();
            }
        });

        btnCashIn.setOnClickListener(v -> {
            gameLogic.cashIn(100.0);
            updateUI();
            Toast.makeText(this, "Cashed in ₱100.00", Toast.LENGTH_SHORT).show();
        });

        btnCashOut.setOnClickListener(v -> {
            double amount = gameLogic.cashOut();
            updateUI();
            etBetAmount.setText("1.00");
            Toast.makeText(this, String.format(Locale.US, "Cashed out ₱%.2f", amount), Toast.LENGTH_LONG).show();
        });

        btnRoll.setOnClickListener(v -> startDiceRoll());
    }

    private void setupTextWatcher() {
        etBetAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double val = Double.parseDouble(s.toString());
                    gameLogic.setCurrentBet(val);
                    updateUI();
                } catch (NumberFormatException e) {
                    gameLogic.setCurrentBet(0);
                    updateUI();
                }
            }
        });
    }

    private void startDiceRoll() {
        if (isRolling || gameLogic.getSelectedColor() == null || gameLogic.getCurrentBet() <= 0 || gameLogic.getCurrentBet() > gameLogic.getBalance()) {
            return;
        }

        isRolling = true;
        setControlsEnabled(false);

        // Play random sound
        int randomSound = soundIds[random.nextInt(soundIds.length)];
        soundPool.play(randomSound, 1, 1, 0, 0, 1);

        final int duration = 1750; // Updated to 1.75 seconds
        final int interval = 100;
        final long startTime = System.currentTimeMillis();

        Runnable rollAnimation = new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed < duration) {
                    showRandomColors();
                    handler.postDelayed(this, interval);
                } else {
                    finalizeRoll();
                }
            }
        };
        handler.post(rollAnimation);
    }

    private void showRandomColors() {
        GameLogic.GameColor[] colors = GameLogic.GameColor.values();
        setDieColor(tvDie1, colors[random.nextInt(colors.length)]);
        setDieColor(tvDie2, colors[random.nextInt(colors.length)]);
        setDieColor(tvDie3, colors[random.nextInt(colors.length)]);
    }

    private void finalizeRoll() {
        GameLogic.GameResult result = gameLogic.rollDice();
        if (result != null) {
            setDieColor(tvDie1, result.diceResults[0]);
            setDieColor(tvDie2, result.diceResults[1]);
            setDieColor(tvDie3, result.diceResults[2]);
        }
        isRolling = false;
        setControlsEnabled(true);
        updateUI();
    }

    private void setDieColor(TextView tv, GameLogic.GameColor color) {
        tv.setBackgroundColor(Color.parseColor(color.hex));
        tv.setText("");
    }

    private void updateUI() {
        tvBalance.setText(String.format(Locale.US, "Balance: ₱%.2f", gameLogic.getBalance()));

        GameLogic.GameColor selected = gameLogic.getSelectedColor();
        if (selected != null) {
            tvSelectedColorInfo.setText("Betting on: " + selected.name);
            tvSelectedColorInfo.setTextColor(Color.parseColor(selected.hex));
        } else {
            tvSelectedColorInfo.setText("Select a color to bet");
            tvSelectedColorInfo.setTextColor(Color.BLACK);
        }

        for (int i = 0; i < colorButtons.length; i++) {
            GameLogic.GameColor color = GameLogic.GameColor.values()[i];
            if (selected == color) {
                colorButtons[i].setBackground(getDrawable(R.drawable.button_border));
                colorButtons[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(color.hex)));
            } else {
                colorButtons[i].setBackground(null);
                colorButtons[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(color.hex)));
            }
        }

        StringBuilder historyText = new StringBuilder();
        for (GameLogic.GameResult res : gameLogic.getHistory()) {
            historyText.append(res.toString()).append("\n");
        }
        tvHistory.setText(historyText.toString());

        btnDecreaseBet.setEnabled(!isRolling && gameLogic.getCurrentBet() > 1.0);
        btnIncreaseBet.setEnabled(!isRolling && gameLogic.getCurrentBet() < gameLogic.getBalance());
        btnRoll.setEnabled(!isRolling && selected != null && gameLogic.getCurrentBet() > 0 && gameLogic.getCurrentBet() <= gameLogic.getBalance());
        btnCashOut.setEnabled(!isRolling && gameLogic.getBalance() > 0);
        btnCashIn.setEnabled(!isRolling);
        etBetAmount.setEnabled(!isRolling);
    }

    private void setControlsEnabled(boolean enabled) {
        btnRoll.setEnabled(enabled);
        btnIncreaseBet.setEnabled(enabled);
        btnDecreaseBet.setEnabled(enabled);
        btnCashIn.setEnabled(enabled);
        btnCashOut.setEnabled(enabled);
        etBetAmount.setEnabled(enabled);
        for (Button b : colorButtons) b.setEnabled(enabled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("balance", gameLogic.getBalance());
        outState.putSerializable("selectedColor", gameLogic.getSelectedColor());
        outState.putSerializable("history", (java.io.Serializable) gameLogic.getHistory());
        outState.putDouble("currentBet", gameLogic.getCurrentBet());
    }
}
