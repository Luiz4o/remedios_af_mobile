package com.firebase.remedios;


import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.UUID;

public class AddRemedioActivity extends AppCompatActivity {

    private EditText editNome, editDesc;
    private TextView txtHorario;
    private Button btnHorario, btnSalvar;

    private int horaSelecionada;
    private int minutoSelecionado;
    private FirebaseFirestore db;
    private String remedioId = null; // se for edição, terá valor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remedio);

        db = FirebaseFirestore.getInstance();

        editNome = findViewById(R.id.editNome);
        editDesc = findViewById(R.id.editDescricao);
        txtHorario = findViewById(R.id.txtHorario);
        btnHorario = findViewById(R.id.btnSelecionarHorario);
        btnSalvar = findViewById(R.id.btnSalvar);

        remedioId = getIntent().getStringExtra("id");

        if (remedioId != null) {
            carregarDados();
        }

        btnHorario.setOnClickListener(v -> selecionarHorario());

        btnSalvar.setOnClickListener(v -> salvarRemedio());
    }

    private void selecionarHorario() {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    horaSelecionada = hourOfDay;
                    minutoSelecionado = minute;

                    String h = String.format("%02d:%02d", hourOfDay, minute);
                    txtHorario.setText(h);
                },
                hora, minuto, true
        );

        dialog.show();
    }

    private void carregarDados() {
        db.collection("remedios").document(remedioId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Remedio r = documentSnapshot.toObject(Remedio.class);
                        if (r != null) {
                            editNome.setText(r.getNome());
                            editDesc.setText(r.getDescricao());
                            txtHorario.setText(r.getHorario());
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao carregar", Toast.LENGTH_SHORT).show()
                );
    }

    private void salvarRemedio() {
        String nome = editNome.getText().toString().trim();
        String desc = editDesc.getText().toString().trim();
        String horario = txtHorario.getText().toString().trim();

        if (nome.isEmpty() || horario.isEmpty()) {
            Toast.makeText(this, "Preencha nome e horário!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (remedioId == null) {
            remedioId = UUID.randomUUID().toString();
        }

        Remedio remedio = new Remedio(
                remedioId,
                nome,
                desc,
                horario,
                false // sempre começa como não tomado
        );

        db.collection("remedios").document(remedioId)
                .set(remedio)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show()
                );

    }
}
