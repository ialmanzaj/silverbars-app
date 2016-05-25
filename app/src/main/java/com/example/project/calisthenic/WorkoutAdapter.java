package com.example.project.calisthenic;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by saleventa7 on 5/24/2016.
 */
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.VH> {

    private static final int TYPE_WORKOUT = 0;
    private static final int TYPE_VIEW_MORE = 1;
    private static final String[] WORKOUTS = {"Chest", "Upper Body", "Core", "Back Destruction", "Chest"};
    private static final int[] IMG = {R.mipmap.imagen1, R.mipmap.imagen2, R.mipmap.imagen3, R.mipmap.imagen4, R.mipmap.imagen5};

    private final FragmentActivity context;

    public static class VH extends RecyclerView.ViewHolder {

        FrameLayout layout;
        ImageView img;
        TextView text;
        Button btn;

        public VH(View v) {
            super(v);

            layout = (FrameLayout) v.findViewById(R.id.layout);
            img  = (ImageView) v.findViewById(R.id.img);
            text = (TextView)  v.findViewById(R.id.text);
            btn  = (Button)    v.findViewById(R.id.btn);
        }
    }

    public WorkoutAdapter(FragmentActivity context) {
        this.context = context;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;

        switch (viewType) {
            case TYPE_WORKOUT:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_row_layout, parent, false);
                return new VH(v);
            case TYPE_VIEW_MORE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_mas_row_layout, parent, false);
                return new VH(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(VH vh, int position) {
        switch (vh.getItemViewType()) {
            case TYPE_WORKOUT:
                vh.img.setImageResource(IMG[position]);
                vh.text.setText(WORKOUTS[position]);
                vh.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, WorkoutActivity.class);
                        context.startActivity(i);
                    }
                });

                if (position == 5) {
                    ViewGroup.MarginLayoutParams params;
                }

                break;
            case TYPE_VIEW_MORE:
                vh.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                break;
        }

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    @Override
    public int getItemViewType(int position) {
        return position < 5 ? TYPE_WORKOUT : TYPE_VIEW_MORE;
    }
}
