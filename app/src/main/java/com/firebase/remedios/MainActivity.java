package com.firebase.remedios;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RemedioAdapter adapter;
    private List<Remedio> remedioList;
    private FirebaseFirestore db;
    private FloatingActionButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerRemedios);
        btnAdd = findViewById(R.id.btnAddRemedio);

        remedioList = new ArrayList<>();
        adapter = new RemedioAdapter(
                remedioList,
                remedio -> editRemedio(remedio),
                remedio -> deleteRemedio(remedio),
                (remedio, marcado) -> marcarComoTomado(remedio, marcado)
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddRemedioActivity.class))
        );

        ImageView btnRecompensas = findViewById(R.id.btnRecompensas);

        btnRecompensas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RecompensasActivity.class);
                startActivity(intent);
            }
        });

        loadRemedios();
    }

    private void loadRemedios() {
        db.collection("remedios")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Erro ao carregar", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value == null) return; // Adicione esta verificação por segurança

                    remedioList.clear();
                    for (QueryDocumentSnapshot doc : value) { // Use QueryDocumentSnapshot para garantir que o doc não é nulo
                        Remedio r = doc.toObject(Remedio.class);
                        r.setId(doc.getId()); // <<< LINHA CRUCIAL: Defina o ID do documento no objeto
                        remedioList.add(r);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void deleteRemedio(Remedio r) {
        db.collection("remedios")
                .document(r.getId())
                .delete()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(this, "Remédio apagado", Toast.LENGTH_SHORT).show();
                });

    }

    private void editRemedio(Remedio r) {
        Intent i = new Intent(MainActivity.this, AddRemedioActivity.class);
        i.putExtra("id", r.getId());
        startActivity(i);
    }

    private void marcarComoTomado(Remedio remedio, boolean tomado) {

        db.collection("remedios")
                .document(remedio.getId())
                .update("tomado", tomado)
                .addOnSuccessListener(unused -> {

                    if (tomado) {
                        verificarRecompensaDiaria();
                    }
                });
    }

    private void verificarRecompensaDiaria() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Date hoje = new Date();

        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        cal1.setTime(hoje);
        cal1.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal1.set(java.util.Calendar.MINUTE, 0);
        cal1.set(java.util.Calendar.SECOND, 0);
        cal1.set(java.util.Calendar.MILLISECOND, 0);

        db.collection("recompensas")
                .get()
                .addOnSuccessListener(query -> {

                    boolean jaGanhouHoje = false;

                    for (DocumentSnapshot doc : query) {
                        Date data = doc.getDate("data");

                        if (data != null) {
                            java.util.Calendar cal2 = java.util.Calendar.getInstance();
                            cal2.setTime(data);
                            cal2.set(java.util.Calendar.HOUR_OF_DAY, 0);
                            cal2.set(java.util.Calendar.MINUTE, 0);
                            cal2.set(java.util.Calendar.SECOND, 0);
                            cal2.set(java.util.Calendar.MILLISECOND, 0);

                            if (cal1.getTime().equals(cal2.getTime())) {
                                jaGanhouHoje = true;
                                break;
                            }
                        }
                    }

                    if (false) {
                        Toast.makeText(this, "Você já ganhou o Pokémon de hoje!", Toast.LENGTH_LONG).show();
                    } else {
                        gerarPokemonAleatorio();
                    }
                });
    }

    private void gerarPokemonAleatorio() {
        int randomId = new Random().nextInt(898) + 1;
        String url = "https://pokeapi.co/api/v2/pokemon/" + randomId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String nome = response.getString("name");
                        String imagem = response.getJSONObject("sprites").getString("front_default");

                        salvarRecompensa(nome, imagem);

                        mostrarPopupPokemon(nome, imagem);

                    } catch (Exception e) { e.printStackTrace(); }
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }

    private void salvarRecompensa(String nome, String imagem) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> recompensa = new HashMap<>();
        recompensa.put("nome", nome);
        recompensa.put("imagem", imagem);
        recompensa.put("data", new Date());

        db.collection("recompensas")
                .add(recompensa);
    }

    private void mostrarPopupPokemon(String nome, String imagemUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Você ganhou um Pokémon!");

        ImageView img = new ImageView(this);

        int largura = (int) (120 * getResources().getDisplayMetrics().density);
        int altura = (int) (120 * getResources().getDisplayMetrics().density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(largura, altura);
        params.gravity = android.view.Gravity.CENTER;
        img.setLayoutParams(params);

        Picasso.get().load(imagemUrl).into(img);

        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(20, 20, 20, 20);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.addView(img);

        builder.setView(layout);
        builder.setMessage("Novo Pokémon: " + nome);

        builder.setPositiveButton("Ok", null);
        builder.show();
    }


}
