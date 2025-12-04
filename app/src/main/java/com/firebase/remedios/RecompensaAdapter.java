package com.firebase.remedios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecompensaAdapter extends RecyclerView.Adapter<RecompensaAdapter.ViewHolder> {

    private List<Recompensa> lista;
    private Context context;

    public RecompensaAdapter(List<Recompensa> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recompensa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recompensa r = lista.get(position);

        holder.nome.setText(r.getNome());

        Picasso.get().load(r.getImagem())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imagem);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imagem;
        TextView nome;

        public ViewHolder(View itemView) {
            super(itemView);
            imagem = itemView.findViewById(R.id.imgPokemon);
            nome = itemView.findViewById(R.id.txtNomePokemon);
        }
    }
}

