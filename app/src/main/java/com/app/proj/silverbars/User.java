package com.app.proj.silverbars;
/**
 * Created by isaacalmanza on 10/04/16.
 */

public class User {

     private int id;
     private String username;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public class ProgressionMuscle {
        private int id;
        private String muscle;
        private String person;
        private int muscle_activation_progress;
        private int level;
        private String date;

        public ProgressionMuscle(int id,String muscle,String person,int muscle_activation_progress,int level,String date){
            this.id = id;
            this.muscle = muscle;
            this.person = person;
            this.muscle_activation_progress = muscle_activation_progress;
            this.level = level;
            this.date = date;

        }

        public int getId(){
            return id;
        }

        public String getMuscle(){
            return muscle;
        }

        public String getPerson(){
            return person;
        }

        public int getLevel(){
            return level;
        }

        public int getMuscle_activation() {
            return muscle_activation_progress;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public void setMuscle_activation_progress(int muscle_activation_progress) {
            this.muscle_activation_progress = muscle_activation_progress;
        }

        public String getDate() {
            return date;
        }
    }


}
