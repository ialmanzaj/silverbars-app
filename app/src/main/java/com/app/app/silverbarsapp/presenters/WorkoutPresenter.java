package com.app.app.silverbarsapp.presenters;

import android.content.Context;
import android.widget.LinearLayout;

import com.app.app.silverbarsapp.callbacks.WorkoutCallback;
import com.app.app.silverbarsapp.interactors.WorkoutInteractor;
import com.app.app.silverbarsapp.models.Workout;
import com.app.app.silverbarsapp.utils.Utilities;
import com.app.app.silverbarsapp.viewsets.WorkoutView;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by isaacalmanza on 01/11/17.
 */

public class WorkoutPresenter extends BasePresenter implements WorkoutCallback {

    private static final String TAG = WorkoutPresenter.class.getSimpleName();

    private WorkoutInteractor interactor;
    private WorkoutView view;
    private Context context;

    private Utilities utilities = new Utilities();

    private  int ISOMETRIC = 0,CARDIO = 0,PYLOMETRICS = 0,STRENGTH = 0;


    public WorkoutPresenter(WorkoutView view, WorkoutInteractor interactor){
        this.view = view;
        this.interactor = interactor;
    }

    public void init(Context context){
        this.context = context;
    }


    private void putTypesInWorkout(LinearLayout contentInfo,List<String> types){
        getCountTimes(types);

        List<String> typesExercise_to_Layout;
        typesExercise_to_Layout = utilities.deleteCopiesofList(types);

        //Log.v(TAG,"typesExercise_to_Layout: "+typesExercise_to_Layout);
        //Log.v(TAG,"typesExercise_to_Layout size: "+typesExercise_to_Layout.size());


        //Log.v(TAG,"ISOMETRIC: "+ISOMETRIC);
        //Log.v(TAG,"CARDIO: "+CARDIO);
        //Log.v(TAG,"PYLOMETRICS: "+PYLOMETRICS);
        //Log.v(TAG,"STRENGTH: "+STRENGTH);


      /*  int ISOMETRIC_ = 0, CARDIO_ = 0, STRENGTH_ = 0, PYLOMETRICS_ = 0;

        if (ISOMETRIC > 0){
            //Log.v(TAG,"porcentaje ISOMETRIC: "+ISOMETRIC+"/"+types.size());
            ISOMETRIC_ = (ISOMETRIC *100 / types.size());
            //Log.v(TAG,"porcentaje: "+ISOMETRIC_);

            RelativeLayout relativeLayout = utilities.createRelativeProgress(context,context.getResources().getString(R.string.ISOMETRIC),ISOMETRIC_);
            contentInfo.addView(relativeLayout);

        }if (CARDIO > 0){
            //Log.v(TAG,"porcentaje CARDIO: "+CARDIO+"/"+types.size());
            CARDIO_ = ( CARDIO*100 / types.size());
            //Log.v(TAG,"porcentaje: "+CARDIO_);

            RelativeLayout relativeLayout = utilities.createRelativeProgress(context,context.getResources().getString(R.string.CARDIO),CARDIO_);
            contentInfo.addView(relativeLayout);

        }if (STRENGTH > 0){
            //Log.v(TAG,"porcentaje STRENGTH: "+STRENGTH+"/"+types.size());
            STRENGTH_ = ((STRENGTH*100/ types.size()));
            //Log.v(TAG,"porcentaje: "+STRENGTH_);

            RelativeLayout relativeLayout = utilities.createRelativeProgress(context,context.getResources().getString(R.string.STRENGTH),STRENGTH_);
            contentInfo.addView(relativeLayout);

        }if (PYLOMETRICS > 0) {
            //Log.v(TAG, "porcentaje PYLOMETRICS: " + PYLOMETRICS + "/" + types.size());
            PYLOMETRICS_ = ((PYLOMETRICS * 100 / types.size()));
            //Log.v(TAG, "porcentaje: " + PYLOMETRICS_);

            RelativeLayout relativeLayout = utilities.createRelativeProgress(context,context.getResources().getString(R.string.PYLOMETRICS),PYLOMETRICS_);
            contentInfo.addView(relativeLayout);
        }
        */

    }

    private void getCountTimes(List<String> list){
        for (int a = 0; a<list.size();a++) {
            if (list.get(a).equals("ISOMETRIC")) {
                ISOMETRIC = ISOMETRIC+1;
            }if (list.get(a).equals("CARDIO")){
                CARDIO=CARDIO+1;
            }if(list.get(a).equals("PYLOMETRICS")){
                PYLOMETRICS = PYLOMETRICS+1;
            }if (list.get(a).equals("STRENGTH")){
                STRENGTH = STRENGTH+1;
            }
        }
    }


    public void saveWorkout(Workout workout) throws SQLException {
        interactor.saveWorkout(workout,this);
    }


    public void setWorkoutOn(int workout_id) throws SQLException {
        interactor.setWorkoutOn(workout_id);
    }


    public boolean isWorkoutExist(int workout_id) throws SQLException {
        return interactor.isWorkoutExist(workout_id);
    }


    private boolean isWorkoutOn(int workoutId) throws SQLException {
        return interactor.isWorkoutSaved(workoutId);
    }



    public void setWorkoutOff(int workout_id) throws SQLException {
        if (isWorkoutExist(workout_id)) {
            interactor.setWorkoutOff(workout_id);
        }
    }

    public boolean isWorkoutAvailable(int workout_id) throws SQLException {
        if (isWorkoutExist(workout_id)){
            return isWorkoutOn(workout_id);
        }

        return false;
    }



    @Override
    public void onWorkout(boolean created) {
        view.onWorkout(created);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onRestart() {

    }


    @Override
    public void onDestroy() {
        //interactor.onDestroy();
    }


}
