package com.app.app.silverbarsapp.handlers;

import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by isaacalmanza on 03/26/17.
 */
public class MuscleHandler {

    private static final String TAG = MuscleHandler.class.getSimpleName();


    private Utilities utilities = new Utilities();


    public MuscleHandler(){}


    public String getMainMuscle(ArrayList<ExerciseRep> exercises){
        List<String> muscles = getMuscles(exercises);
        return getMainMuscleByType(
                whatTypeAre(
                        utilities.deleteCopiesofList(muscles)
                    )
                );

    }

    private String getMainMuscleByType(List<String> types){
            if (types.contains("UPPER BODY") && types.contains("LOWER BODY") ){
                return "FULL BODY";
            }else if ( (types.contains("UPPER BODY") && types.contains("ABS/CORE")) || types.contains("UPPER BODY")){
                return "UPPER BODY";
            }else if ( (types.contains("LOWER BODY") && types.contains("ABS/CORE")) || types.contains("LOWER BODY")){
                return "LOWER BODY";
            }else {
                return "ABS/CORE";
            }
    }

    private List<String> whatTypeAre(List<String> muscles_no_copies){
        List<String> types = new ArrayList<>();

        for (int a = 0; a<muscles_no_copies.size();a++){
            types.add(checkWhichMuscleIs(muscles_no_copies.get(a)));
        }

        return types;
    }

    private String checkWhichMuscleIs(String muscle){
        switch (muscle){
            case "HAMSTRINGS":
                return "LOWER BODY";
            case "TRAPEZIUS":
                return "UPPER BODY";
            case "TRICEPS":
                return "UPPER BODY";
            case "LOWER-BACK":
                return "ABS/CORE";
            case "RHOMBOIDS":
                return "UPPER BODY";
            case "GLUTES":
                return "LOWER BODY";
            case "CALVES":
                return "LOWER BODY";
            case "OBLIQUES":
                return "ABS/CORE";
            case "CUADRICEPS":
                return "LOWER BODY";
            case "ADDUCTORS":
                return "LOWER BODY";
            case "DELTOIDS":
                return "UPPER BODY";
            case "BICEPS":
                return "UPPER BODY";
            case "RECTUS-ABDOMINIS":
                return "ABS/CORE";
            case "LATISSIMUS-DORSI":
                return "UPPER BODY";
            case "PECTORALIS-MAJOR":
                return "UPPER BODY";
            default:
                return "";
        }
    }


    private List<String> getMuscles(ArrayList<ExerciseRep> exercises){
        List<String> muscles = new ArrayList<>();
        for (ExerciseRep exercise: exercises){
            for (MuscleExercise muscle: exercise.getExercise().getMuscles()){
                muscles.add(muscle.getMuscle());
            }
        }
        return muscles;
    }


}
