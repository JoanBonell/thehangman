package cat.udl.gtidic.course2223.teacher.thehangman.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cat.udl.gtidic.course2223.teacher.thehangman.R;
import cat.udl.gtidic.course2223.teacher.thehangman.model.Game;
import cat.udl.gtidic.course2223.teacher.thehangman.viewmodel.ActivityGameBinding;
import cat.udl.gtidic.course2223.teacher.thehangman.viewmodel.GameViewModel;

public class MainActivity extends AppCompatActivity{

    String userName;

    private GameViewModel gameViewModel;
    Button btnNewLetter;
    TextView visibleWord;
    TextView lettersChosen;
    EditText etNewLetter;
    ImageView ivState;
    TextView playerNameText;
    Game game;
    public static final int LETTER_VALIDATION_NO_VALID_BECAUSE_SIZE = 1;
    public static final int LETTER_VALIDATION_NO_VALID_BECAUSE_ALREADY_SELECTED = 2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        here is a good place to implement MVVM if someone is interested

//        initializing views
        btnNewLetter = findViewById(R.id.btnNewLetter);
        btnNewLetter.setOnClickListener(v -> newLetter());
        visibleWord = findViewById(R.id.tvVisibleWord);
        lettersChosen = findViewById(R.id.tvLettersChosen);
        etNewLetter = findViewById(R.id.etNewLetter);
        ivState = findViewById(R.id.ivState);
        playerNameText = findViewById(R.id.playerName);
        // collecting data from Intent - Bundle from previous Activity
        Bundle extra = getIntent().getExtras();
        String nomJugador = extra.getString("nomdelJugador");
        Log.d(Game.TAG, "Nom del jugador: " + nomJugador);
        playerNameText.setText(nomJugador);
        Toast.makeText(this, nomJugador, Toast.LENGTH_LONG).show();
//        starting game mechanics
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        Activity DataBindingUtil = null;
        //ActivityGameBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //binding.setGameViewModel(game);
        //binding.setLifecycleOwner(this);
        startGame();
    }

    /**
     * Retorna el Drawable segons l'estat correcte
     */
    private Drawable getDrawableFromState(int state){
        int r_desired = -1;

        switch (state){
            case 0: r_desired = R.drawable.round_0; break;
            case 1: r_desired = R.drawable.round_1; break;
            case 2: r_desired = R.drawable.round_2; break;
            case 3: r_desired = R.drawable.round_3; break;
            case 4: r_desired = R.drawable.round_4; break;
            case 5: r_desired = R.drawable.round_5; break;
            case 6: r_desired = R.drawable.round_6; break;
            case 7: r_desired = R.drawable.round_7; break;
        }
        return ContextCompat.getDrawable(this, r_desired);
    }

    /**
     * Actualitza les views de la pantalla
     */
    private void refreshWords(){
        visibleWord.setText(game.visibleWord());
        lettersChosen.setText(game.lettersChosen());
        ivState.setImageDrawable(getDrawableFromState(game.getCurrentRound()));
    }

    /**
     * Afegeix la lletra al joc
     */
    private void newLetter(){
        String novaLletra = etNewLetter.getText().toString().toUpperCase();
        etNewLetter.setText("");


        int validLetter = game.addLetter(novaLletra);
        String errorAMostrar = "";
        if (validLetter != Game.LETTER_VALIDATION_OK){
            errorAMostrar = "Lletra no vàlida";
            if(validLetter==LETTER_VALIDATION_NO_VALID_BECAUSE_ALREADY_SELECTED){
                errorAMostrar+= " perquè ja ha estat seleccionada anteriorment";
            }
            else if(validLetter==LETTER_VALIDATION_NO_VALID_BECAUSE_SIZE){
                errorAMostrar+= " per el seu tamany";
            }
            Toast toast = Toast.makeText(getApplicationContext(),errorAMostrar, Toast.LENGTH_SHORT);
            toast.show();
            Log.d(Game.TAG, errorAMostrar);
        }
        Log.d(Game.TAG, "Estat actual: " + game.getCurrentRound());

        refreshWords();
        hideKeyboard();
        checkGameOver();
    }

    /**
     * Revisa si el joc ha acabat i informa via Log (de moment)
     */
    private void checkGameOver(){
        if (game.isPlayerTheWinner()){
            Log.d(Game.TAG, "El jugador ha guanyat!");
            Toast toast = Toast.makeText(getApplicationContext(), "Has guanyat!", Toast.LENGTH_SHORT);
            toast.show();
        }

        if (game.isGameOver()){
            Log.d(Game.TAG, "El Joc ha acabat");
            btnNewLetter.setEnabled(false);
            etNewLetter.setEnabled(false);
            finish();
        }

    }

    /**
     * Inicia el joc i actualitza l'activitat
     */
    private void startGame(){
        game = new Game();
        refreshWords();
    }

    /* -------- METODES AUXILIARS --------- */

    /**
     * Amaga el teclat virtual de la pantalla
     */
    private void hideKeyboard(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}