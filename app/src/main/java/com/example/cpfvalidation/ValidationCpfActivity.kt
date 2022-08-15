package com.example.cpfvalidation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class ValidationCpfActivity : AppCompatActivity() {

    private val inputCPF: TextInputEditText
        get() = findViewById(R.id.text_input_cpf)

    private val buttonValidate: Button
        get() = findViewById(R.id.button_validate)

    private val sharedPref: SharedPrefCpf = SharedPrefCpf.instance

    private var cpfInputAux = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validation_cpf)

        supportActionBar?.hide()
        inputMaskInCpf()
        checkItemsFromBundle()

        buttonValidate.setOnClickListener {
            if (inputCPF.text?.length == 14) {
                verifyCPF(inputCPF.text.toString())
            } else Toast.makeText(this, "Digite corretamente o CPF!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) { // Here You have to save count value
        super.onSaveInstanceState(outState)

        val inputCpfValue = inputCPF.text.toString()
        Log.i("Ui", "Cpf digitado a ser salvo é: $inputCpfValue")
        outState.putString(COUNT_KEY, inputCpfValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { // Here You have to restore count value
        super.onRestoreInstanceState(savedInstanceState)

        val cpfReaderValue = savedInstanceState.getString(COUNT_KEY)
        Log.i("Ui", "Cpf salvo é: $cpfReaderValue")
        inputCPF.setText(cpfReaderValue.toString())
    }

    // Método que Verifica se o CPF digitado é válido:
    private fun verifyCPF(numberCpf: String): Boolean {
        var sumTestFirstDigit: Int = 0
        var sumTestSecondDigit: Int = 0
        var testFirstDigit: Int = 0
        var testSecondDigit: Int = 0
        var statusCpf: Boolean = false

        //sharedPref.saveNumberCpf("CPF", numberCpf)

        // Checagem de CPFs que são considerados inválidos:
        if (numberCpf == "111.111.111-11" ||
            numberCpf == "222.222.222-22" ||
            numberCpf == "333.333.333-33" ||
            numberCpf == "444.444.444-44" ||
            numberCpf == "555.555.555-55" ||
            numberCpf == "666.666.666-66" ||
            numberCpf == "777.777.777-77" ||
            numberCpf == "888.888.888-88" ||
            numberCpf == "999.999.999-99"
        ) {
            Toast.makeText(this, "CPF Inválido!!", Toast.LENGTH_LONG).show()
            sendToCpfInvalidate(numberCpf)
            return statusCpf
        }

        // Tratamento da String Recebida para retirar a máscara do número do CPF:
        val numberCpfWithoutMask = clearMask(numberCpf)
        Log.d("Mask:", numberCpfWithoutMask)

        // Conversão dos números recebidos do Tipo String para Int
        val converterNumbersToInt = numberCpfWithoutMask.map {
            it.toString().toInt()
        }

        // Valor do Número dos Digitos de Verificação do CPF:
        val firstDigitValidateNumber = converterNumbersToInt[9]
        val secondDigitValidateNumber = converterNumbersToInt[10]

        // Teste do Primeiro Digito Verficador do CPF:
        for (i in 0..8) {
            sumTestFirstDigit += converterNumbersToInt[i] * (10 - i)
        }
        Log.d("Soma Teste:", "$sumTestFirstDigit")

        testFirstDigit = (sumTestFirstDigit * 10) % 11
        Log.d("Test 1 Dig:", "$testFirstDigit")
        if (testFirstDigit == 10) testFirstDigit = 0

        // Teste do Segundo Digito Verficador do CPF:
        for (i in 0..9) {
            sumTestSecondDigit += converterNumbersToInt[i] * (11 - i)
            Log.d("Soma Second Digit", "$sumTestSecondDigit")
        }

        testSecondDigit = (sumTestSecondDigit * 10) % 11
        if (testSecondDigit == 10) testSecondDigit = 0

        Log.d("Digito 1 Testado", "$testFirstDigit")
        Log.d("Digito 1 CPF", "$firstDigitValidateNumber")

        Log.d("Digito 2 Testado", "$testSecondDigit")
        Log.d("Digito 2 CPF", "$secondDigitValidateNumber")

        statusCpf =
            if (testFirstDigit == firstDigitValidateNumber && testSecondDigit == secondDigitValidateNumber) {
                Toast.makeText(this, "CPF Válido!!", Toast.LENGTH_LONG).show()
                Log.d("CPF:", "Válido!!")
                true
            } else {
                Toast.makeText(this, "CPF Inválido!!", Toast.LENGTH_LONG).show()
                Log.d("CPF:", "Inválido!!")
                false
            }

        if (statusCpf) sendToCpfValidateOk(numberCpf) else sendToCpfInvalidate(numberCpf)
        return statusCpf
    }

    // Método que retira a máscara ao nuúmero de cpf digitado:
    private fun clearMask(s: String): String {
        return s.replace("-", "").replace("/", "").replace(".", "")
    }

    // Método para inserir a máscara no número do CPF digitado pelo Usuário:
    private fun inputMaskInCpf() {
        inputCPF.addTextChangedListener(object : TextWatcher {
            var isUpdating: Boolean = false

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Variáveis de strings
                val str = clearMask(s.toString())
                val mask = "###.###.###-##"
                var mascara = ""

                // Checa se está sendo feito update, para não entrar em loop infinito
                if (isUpdating) {
                    isUpdating = false
                    return
                }

                // Checa mascara e cria string com base na mascara
                var i = 0
                for (m in mask.toCharArray()) {
                    if (m != '#' && count > before) {
                        mascara += m
                        continue
                    }
                    try {
                        mascara += str[i]
                    } catch (e: Exception) {
                        break
                    }
                    i++
                }
                // Faz o update da string no EditText e verifica se está completa
                isUpdating = true
                inputCPF.setText(mascara)
                inputCPF.setSelection(mascara.length)
            }
        })
    }

    // Método para iniciar a Activity CpfOkActivity
    private fun sendToCpfValidateOk(cpfNumber: String) {
        val intent = Intent(this, CpfOkActivity::class.java).apply {
            putExtra("Cpf", cpfNumber)
        }
        startActivity(intent)
    }

    // Método para iniciar a Activity CpfInvalidateActivity
    private fun sendToCpfInvalidate(cpfNumber: String) {
        val intent = Intent(this, CpfInvalidActivity::class.java).apply {
            putExtra("Cpf", cpfNumber)
        }
        startActivity(intent)
    }

    // Método para realizar a leitura do número do cpf salvo no Shared Pref:
    private fun readCpf() {
        try {
            inputCPF.setText(sharedPref.readNumberCpf("CPF"))
        } catch (e: java.lang.Exception) {
            Log.e("Shared Pref", "Erro ao ler dado do CPF salvo no Shared Preferences")
        }
    }

    // Método que verifica se houve informações passadas por Bundle:
    @SuppressLint("SetTextI18n")
    private fun checkItemsFromBundle() {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            cpfInputAux = bundle.getString("Cpf").toString()
            inputCPF.setText(cpfInputAux)
        }
    }

    // Valor const "Cpf" para salvar / ler valor do bundle
    companion object {
        const val COUNT_KEY = "Cpf"
    }
}
