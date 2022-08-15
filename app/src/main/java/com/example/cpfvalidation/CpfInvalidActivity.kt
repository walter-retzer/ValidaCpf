package com.example.cpfvalidation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import java.lang.Exception

class CpfInvalidActivity : AppCompatActivity() {

    var numberCpf: String = ""

    private val myCPF: TextView
        get() = findViewById(R.id.textSubtitleCpfInvalidate)

    private val buttonReturn: Button
        get() = findViewById(R.id.button_return)

    private val sharedPref: SharedPrefCpf = SharedPrefCpf.instance

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cpf_invalid)

        supportActionBar?.hide()
        checkItemsFromBundle()
        buttonReturn.setOnClickListener { sendToCpfValidation(numberCpf) }
    }

    // Método para realizar a chamada de retorna a Activity Anterior
    override fun onBackPressed() {
        sendToCpfValidation(numberCpf)
    }

    // Método para iniciar a Activity CpfOkActivity
    private fun sendToCpfValidation(cpfNumber: String) {
        val intent = Intent(this, ValidationCpfActivity::class.java).apply {
            putExtra("Cpf", cpfNumber)
        }
        startActivity(intent)
    }

    // Método que verifica se houve informações passadas por Bundle:
    @SuppressLint("SetTextI18n")
    private fun checkItemsFromBundle() {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            numberCpf = bundle.getString("Cpf").toString()
            myCPF.text = "O CPF: $numberCpf, é inválido!"
        }
    }

    // Método para realizar a leitura do CPF salvo no Shared Preferences
    @SuppressLint("SetTextI18n")
    private fun readCpf() {
        try {
            myCPF.text = "O CPF: ${sharedPref.readNumberCpf("CPF")}, é inválido!"
        } catch (e: Exception) {
            Log.e("Shared Pref", "Erro ao ler dado do CPF salvo no Shared Preferences")
        }
    }
}
