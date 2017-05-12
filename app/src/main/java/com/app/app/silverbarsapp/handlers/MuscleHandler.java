package com.app.app.silverbarsapp.handlers;

import android.util.Log;

import com.app.app.silverbarsapp.models.ExerciseRep;
import com.app.app.silverbarsapp.models.MuscleExercise;
import com.app.app.silverbarsapp.utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by isaacalmanza on 03/26/17.
 */
public class MuscleHandler {

    private static final String TAG = MuscleHandler.class.getSimpleName();


    private Utilities utilities = new Utilities();


    public MuscleHandler(){}


    public String getMainMuscle(ArrayList<ExerciseRep> exercises){
        List<String> list_all_muscles = getMuscles(exercises);
        return knowWhatTypes(utilities.deleteCopiesofList(list_all_muscles),list_all_muscles);
    }


    private String knowWhatTypes(List<String> muscles_no_copies,List<String> list_all_muscles){
        Log.d(TAG,"muscles:"+ muscles_no_copies);

        int[] muscles_name_times = getTimesInList(muscles_no_copies,list_all_muscles);
        Log.d(TAG,"n times:"+ Arrays.toString(muscles_name_times));

        List<String> types = whatTypeAre(muscles_no_copies);

        List<String> types_no_copies = utilities.deleteCopiesofList(types);
        Log.d(TAG,"types_no_copies: "+types_no_copies);

        int[] types_times = getTimesInList(types_no_copies,types);
        Log.d(TAG,"types times:"+ Arrays.toString(types_times));

        return whichIsMainMuscle(types_no_copies,types_times);
    }

    private String whichIsMainMuscle(List<String> types_no_copies,int[] types_times){
        int mayor = types_times[0];

        for (int types_time : types_times) {
            if (types_time > mayor) {
                mayor = types_time;
            } else if (types_time == mayor) {
                return "FULL BODY";
            }
        }

       return getMainMuscleByTotal(mayor,types_times,types_no_copies);
    }

    private String getMainMuscleByTotal(int mayor_times_in_list,int[] types_times,List<String> types_no_copies){
        String main_muscle = "";
        for (int b = 0;b<types_times.length;b++){
            if (types_times[b] == mayor_times_in_list){
                main_muscle = types_no_copies.get(b);
                break;
            }
        }
        return main_muscle;
    }



    private int[] getTimesInList(List<String>  list_with_no_copies,List<String> complete_list){
        int[] times_in_list = new int[list_with_no_copies.size()];

        for (int i = 0;i<list_with_no_copies.size();i++){
            for (String element:complete_list){
                if (list_with_no_copies.get(i).equals(element)){
                    times_in_list[i] = times_in_list[i]+1;
                }
            }
        }

        return times_in_list;
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
