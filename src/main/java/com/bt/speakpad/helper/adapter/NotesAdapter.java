package com.bt.speakpad.helper.adapter;

import android.content.Context;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.speakpad.R;
import com.bt.speakpad.helper.pojo.Notes;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends ListAdapter<Notes, NotesAdapter.MyViewHolder> {

    private Context context;
    private OnItemClickListener listener;

    public NotesAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }


    private static final DiffUtil.ItemCallback<Notes> DIFF_CALLBACK = new DiffUtil.ItemCallback<Notes>() {
        @Override
        public boolean areItemsTheSame(@NonNull Notes oldNotes, @NonNull Notes newNotes) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Notes oldNotes, @NonNull Notes newNotes) {
            return false;
        }
    };

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_notes, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Notes note = getItem(i);
        holder.txtText.setText(Html.fromHtml(note.getNote()));
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtText;
        //Todo Views
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtText = itemView.findViewById(R.id.txtText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.OnItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(Notes item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}