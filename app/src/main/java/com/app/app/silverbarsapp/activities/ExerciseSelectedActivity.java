package com.app.app.silverbarsapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.app.silverbarsapp.R;
import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.presenters.BasePresenter;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

public class ExerciseSelectedActivity extends BaseActivity {

    private static final String TAG = ExerciseSelectedActivity.class.getSimpleName();


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.imagen) SimpleDraweeView mExerciseImg;

    @BindView(R.id.number) AutoCompleteTextView mNumberView;
    @BindView(R.id.option) Spinner mTypeExerciseSpinner;
    @BindView(R.id.weight) AutoCompleteTextView mWeight;
    @BindView(R.id.change)Button change;

    private ExerciseRep mExercise;
    private int position;

    @Override
    protected int getLayout() {
        return R.layout.activity_create_exercise_selected;
    }

    @Nullable
    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        position = extras.getInt("position");
        mExercise = extras.getParcelable("exercise");

        initSpinner();


        if (mExercise != null) {
            setupToolbar(mExercise.getExercise().getExercise_name());
            mExerciseImg.setImageURI(Uri.parse((mExercise.getExercise().getExercise_image())));

            mNumberView.setText(String.valueOf(mExercise.getNumber()));

            mWeight.setText(String.valueOf(mExercise.getWeight()));


            Log.d(TAG,"state "+mExercise.getExercise_state());

            if (Objects.equals(mExercise.getExercise_state(), "REP")) {
                mTypeExerciseSpinner.setSelection(0);
            }else {
                mTypeExerciseSpinner.setSelection(1);
            }
        }
    }

    public void setupToolbar(String title){
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_clear_white_24px);
            getSupportActionBar().setTitle(title);
        }
    }

    private void initSpinner(){
        //Inicialize classification adapter
        ArrayAdapter<String> mTypeAdapter  = new ArrayAdapter<>(this, R.layout.simple_spinner_item);
        mTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mTypeAdapter.add(getString(R.string.activity_exercise_selected_reps));
        mTypeAdapter.add(getString(R.string.activity_exercise_selected_secs));
        mTypeExerciseSpinner.setAdapter(mTypeAdapter);
        mTypeExerciseSpinner.setSelection(0);
    }


    @OnClick(R.id.change)
    public void changeExercise(){
        if (mExercise.getNumber() <= 0){
            Toast.makeText(this, R.string.activity_create_workout_final_reps_seleccion, Toast.LENGTH_SHORT).show();
            return;
        }


        Intent return_intent = new Intent();
        return_intent.putExtra("position",position);
        return_intent.putExtra("exercise",mExercise);
        setResult(RESULT_OK, return_intent);
        finish();
    }

    @OnTextChanged(value = R.id.number)
    void repChanged(CharSequence charSequence){
        if (!Objects.equals(charSequence.toString(), "")){
            mExercise.setNumber(Integer.parseInt(charSequence.toString()));
        }
    }

    @OnItemSelected(R.id.option)
    void optionSelected(Spinner spinner, int position) {
        mExercise.setExerciseState(position == 0 ? "REP" : "SEC");
    }

    @OnTextChanged(value = R.id.weight)
    void weightChanged(CharSequence charSequence){
        String weight_txt = charSequence.toString();
        double weight = Objects.equals(weight_txt, "") ? 0 : Double.valueOf(weight_txt);
        mExercise.setWeight(weight);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //Log.d(TAG, "action bar clicked");
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
