package com.firebase.remedios;


import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RecompensasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecompensaAdapter adapter;
    private List<Recompensa> listaRecompensas = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recompensas);

        recyclerView = findViewById(R.id.recyclerRecompensas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecompensaAdapter(listaRecompensas, this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        carregarRecompensas();
    }

    private void carregarRecompensas() {
        db.collection("recompensas")
                .orderBy("data")
                .get()
                .addOnSuccessListener(query -> {
                    listaRecompensas.clear();

                    for (DocumentSnapshot doc : query) {
                        Recompensa r = doc.toObject(Recompensa.class);
                        listaRecompensas.add(r);
                    }

                    adapter.notifyDataSetChanged();

                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao carregar recompensas", Toast.LENGTH_SHORT).show();
                });
    }
}

