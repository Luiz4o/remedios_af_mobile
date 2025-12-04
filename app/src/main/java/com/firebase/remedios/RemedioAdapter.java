package com.firebase.remedios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RemedioAdapter extends RecyclerView.Adapter<RemedioAdapter.RemedioViewHolder> {

    private List<Remedio> remedios;
    private OnItemClick listener;
    private OnItemLongClick longClick;
    private OnCheckTomadoChange checkListener;

    public interface OnItemClick {
        void onClick(Remedio remedio);
    }

    public interface OnItemLongClick {
        void onLongClick(Remedio remedio);
    }

    public interface OnCheckTomadoChange {
        void onChecked(Remedio remedio, boolean isChecked);
    }

    public RemedioAdapter(List<Remedio> remedios,
                          OnItemClick listener,
                          OnItemLongClick longClick,
                          OnCheckTomadoChange checkListener) {
        this.remedios = remedios;
        this.listener = listener;
        this.longClick = longClick;
        this.checkListener = checkListener;
    }

    @NonNull
    @Override
    public RemedioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remedio, parent, false);
        return new RemedioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RemedioViewHolder holder, int position) {
        Remedio r = remedios.get(position);

        holder.nome.setText(r.getNome());
        holder.horario.setText(r.getHorario());
        holder.checkTomado.setChecked(r.isTomado());

        holder.itemView.setOnClickListener(v -> listener.onClick(r));

        holder.itemView.setOnLongClickListener(v -> {
            longClick.onLongClick(r);
            return true;
        });

        holder.checkTomado.setOnCheckedChangeListener((button, isChecked) -> {
            r.setTomado(isChecked);
            checkListener.onChecked(r, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return remedios.size();
    }

    class RemedioViewHolder extends RecyclerView.ViewHolder {
        TextView nome, horario;
        CheckBox checkTomado;

        public RemedioViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.txtNomeRemedio);
            horario = itemView.findViewById(R.id.txtHorarioRemedio);
            checkTomado = itemView.findViewById(R.id.checkTomado);
        }
    }
}

