package com.example.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.notes.model.Note;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private OnNoteClickListener onNoteClickListener;
    private OnNoteLongClickListener onNoteLongClickListener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    public interface OnNoteLongClickListener {
        void onNoteLongClick(Note note);
    }

    public NotesAdapter(List<Note> noteList) {
        this.noteList = noteList;
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.onNoteClickListener = listener;
    }

    public void setOnNoteLongClickListener(OnNoteLongClickListener listener) {
        this.onNoteLongClickListener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.txtTitle.setText(note.getTitre() != null ? note.getTitre() : "Sans titre");
        holder.txtContent.setText(note.getContenu() != null ? note.getContenu() : "");


        // Gestion du clic sur l'élément
        holder.itemView.setOnClickListener(v -> {
            if (onNoteClickListener != null) {
                onNoteClickListener.onNoteClick(note);
            }
        });

        // Gestion du long clic sur l'élément
        holder.itemView.setOnLongClickListener(v -> {
            if (onNoteLongClickListener != null) {
                onNoteLongClickListener.onNoteLongClick(note);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return noteList != null ? noteList.size() : 0;
    }

    public void updateNotes(List<Note> newNotes) {
        this.noteList = newNotes;
        notifyDataSetChanged();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtContent, txtCategory;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtNoteTitle);
            txtContent = itemView.findViewById(R.id.txtNoteContent);
            txtCategory = itemView.findViewById(R.id.txtNoteCategory);
        }
    }
}

