package com.example.cpfvalidation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ValidationCpfActivity : AppCompatActivity() {

    private val inputCPF: TextInputEditText
        get() = findViewById(R.id.textInputCpf)

    private val inputCpfLayout: TextInputLayout
        get() = findViewById(R.id.inputCpf)

    private val buttonValidate: Button
        get() = findViewById(R.id.buttonValidate)

    private val counterNumbersCpf: TextView
        get() = findViewById(R.id.textCounterNumberCpf)

    private var cpfInputAux = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validation_cpf)

        val colors = ContextCompat.getColorStateList(this, android.R.color.holo_red_light)

        supportActionBar?.hide()
        inputMaskInCpf()
        checkItemsFromBundle()

        buttonValidate.setOnClickListener {
            if (inputCPF.text?.length == 14) {
                verifyCPF(inputCPF.text.toString())

            } else {
                Toast.makeText(this, "Digite corretamente o CPF!", Toast.LENGTH_LONG).show()
                inputCpfLayout.setHelperTextColor(colors)
                inputCpfLayout.helperText = "Verifique o cpf digitado."
            }
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
        var calculateFirstDigit: Int = 0
        var calculateSecondDigit: Int = 0
        var statusCpf: Boolean = false

        // Checagem de CPFs que são inválidos:
        if (Cpf.checkCpfInvalid(cpfNumber = numberCpf)) {
            Toast.makeText(this, "CPF Inválido!!", Toast.LENGTH_LONG).show()
            sendToCpfInvalidate(numberCpf)
            return false
        }

        // Número do Cpf Recebido convertido para Int
        val cpf = Cpf.converterCpfToInt(numberCpf)

        // Valor do Número dos Digitos de Verificação do CPF Recebido:
        val firstDigitValidateNumber = Cpf.getFirstDigit(cpf)
        val secondDigitValidateNumber = Cpf.getSecondDigit(cpf)

        // Somas Auxiliares para validação do CPF:
        val sumFirstDigit = Cpf.calcSumValidate(cpf = cpf, false)
        val sumSecondDigit = Cpf.calcSumValidate(cpf = cpf, true)

        // Cálculo do Primeiro Digito Verificador:
        calculateFirstDigit = Cpf.calcDigitValidation(soma = sumFirstDigit)
        Log.d("Digito 1 Testado", "$calculateFirstDigit")
        Log.d("Digito 1 CPF", "$firstDigitValidateNumber")

        // Cálculo do Segundo Digito Verificador:
        calculateSecondDigit = Cpf.calcDigitValidation(soma = sumSecondDigit)
        Log.d("Digito 2 Testado", "$calculateSecondDigit")
        Log.d("Digito 2 CPF", "$secondDigitValidateNumber")

        // Compara os digitos verificadores para validar o CPF:
        statusCpf = Cpf.compareDigits(
            calculateFirstDigit = calculateFirstDigit,
            firstDigitValidateNumber = firstDigitValidateNumber,
            calculateSecondDigit = calculateSecondDigit,
            secondDigitValidateNumber = secondDigitValidateNumber,
        )

        // Inicia a Atividade correspondente ao CPF ser válido ou inválido.
        if (statusCpf) sendToCpfValidateOk(numberCpf) else sendToCpfInvalidate(numberCpf)
        return statusCpf
    }


    // Método que retira a máscara ao nuúmero de cpf digitado:
    private fun clearMask(s: String): String {
        return s.replace("-", "").replace("/", "").replace(".", "")
    }


    // Método para inserir a máscara no número do CPF digitado pelo Usuário:
    private fun inputMaskInCpf() {

        var isRunning: Boolean = false
        var isDeleting: Boolean = false


        inputCPF.addTextChangedListener(object : TextWatcher {
            var isUpdating: Boolean = false

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                isDeleting = count > after
            }

            override fun afterTextChanged(s: Editable) {
                if (isRunning || isDeleting) {
                    return
                }

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Variáveis de strings
                val str = clearMask(s.toString())
                val mask = "###.###.###-##"
                var mascara = ""

                // Cor para o helper text:
                val color = ContextCompat.getColorStateList(
                    this@ValidationCpfActivity,
                    android.R.color.black
                )
                inputCpfLayout.setHelperTextColor(color)
                inputCpfLayout.helperText = "Digite o número do CPF."

                // Checa se está sendo feito update, para não entrar em loop infinito
                if (isUpdating) {
                    isUpdating = false
                    return
                }

                // Quando houver um item numero deletado pelo usuário:
                if (count < before) {
                    var aux = 0
                    for (m in mask.toCharArray()) {
                        if (m != '#' && count < before) {
                            mascara += m
                            continue
                        }
                        try {
                            mascara += str[aux]
                        } catch (e: Exception) {
                            break
                        }
                        aux++
                    }
                    counterNumbersCpf.text = "${str.length}/11"
                }

                // Quando houver um item numero sendo inserido pelo usuário:
                if (count > before) {
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
                    counterNumbersCpf.text = "${str.length}/11"
                }
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
