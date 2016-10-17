package com.app.proj.silverbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.app.proj.silverbars.Utilities.loadExerciseImageFromDevice;
import static com.app.proj.silverbars.Utilities.saveExerciseImageInDevice;
/**
 * Created by isaacalmanza on 10/04/16.
 */

public class AllExercisesAdapter extends RecyclerView.Adapter<AllExercisesAdapter.AllExercisesViewHolder> {

    private static final String TAG = "AllExercisesAdapter";
    private Context mContext;
    private List<Exercise> mExercises = new ArrayList<>();

    public static SparseBooleanArray selectedItems = new SparseBooleanArray();

    public static class AllExercisesViewHolder extends RecyclerView.ViewHolder {

        // Campos respectivos de un item
        //public ImageView imagen;
        public TextView nombre;
        public TextView next;
        public ImageView unchecked, checked;

        SimpleDraweeView imagen;


        public AllExercisesViewHolder(View itemView) {
            super(itemView);
            //imagen = (ImageView) itemView.findViewById(R.id.imagen);
            nombre = (TextView) itemView.findViewById(R.id.nombre);

            imagen = (SimpleDraweeView) itemView.findViewById(R.id.imagen);

            unchecked = (ImageView) itemView.findViewById(R.id.unchecked);
            checked = (ImageView) itemView.findViewById(R.id.checked);

        }
    }

    public AllExercisesAdapter(Context context,List<Exercise> exercises) {
        mContext = context;
        mExercises = exercises;
    }

    @Override
    public int getItemCount() {
        return mExercises.size();
    }

    @Override
    public AllExercisesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.all_exercises_list, viewGroup, false);
        return new AllExercisesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AllExercisesViewHolder viewHolder,int i) {

        final int a = viewHolder.getAdapterPosition();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedItems.get(a, false)) {
                    selectedItems.delete(a);

                    viewHolder.checked.setVisibility(View.GONE);
                    viewHolder.unchecked.setVisibility(View.VISIBLE);
                } else {

                    selectedItems.put(a, true);
                    viewHolder.unchecked.setVisibility(View.GONE);
                    viewHolder.checked.setVisibility(View.VISIBLE);
                }
            }
        });

        viewHolder.nombre.setText(mExercises.get(a).getExercise_name());

        Uri uri = Uri.parse(mExercises.get(a).getExercise_image());
        viewHolder.imagen.setImageURI(uri);



    }


}