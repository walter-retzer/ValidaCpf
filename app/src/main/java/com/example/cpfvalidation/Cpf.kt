package com.example.cpfvalidation

import android.util.Log

class Cpf {

    companion object {
        // Método que checa CPFs Inválidos:
        fun checkCpfInvalid(cpfNumber: String): Boolean {
            var status = false

            // Checagem de CPFs que são considerados inválidos:
            if (cpfNumber == "111.111.111-11" ||
                cpfNumber == "222.222.222-22" ||
                cpfNumber == "333.333.333-33" ||
                cpfNumber == "444.444.444-44" ||
                cpfNumber == "555.555.555-55" ||
                cpfNumber == "666.666.666-66" ||
                cpfNumber == "777.777.777-77" ||
                cpfNumber == "888.888.888-88" ||
                cpfNumber == "999.999.999-99" ||
                cpfNumber == "000.000.000-00"
            ) status = true

            return status
        }

        // Método que retira a máscara ao nuúmero de cpf digitado:
        private fun clearMask(s: String): String {
            return s.replace("-", "").replace("/", "").replace(".", "")
        }

        // Método para converter a string recebida para o tipo Int:
        fun converterCpfToInt(numberCpf: String): List<Int> {
            val numberCpfWithoutMask = clearMask(numberCpf)
            Log.d("Mask:", numberCpfWithoutMask)

            return numberCpfWithoutMask.map {
                it.toString().toInt()
            }
        }

        // Método que retorna o Primeiro Número de Validação do CPF:
        fun getFirstDigit(cpfNumber: List<Int>): Int {
            return cpfNumber[9].toString().toInt()
        }


        // Método que retorna o Segundo Número de Validação do CPF:
        fun getSecondDigit(cpfNumber: List<Int>): Int {
            return cpfNumber[10].toString().toInt()
        }


        // Método que calcula o valor do dígito de validação do CPF:
        fun calcSumValidate(cpf: List<Int>, isSecondDigitValidation: Boolean): Int {
            var end: Int = 8
            var digits: Int = 10
            var calcSum: Int = 0

            if (isSecondDigitValidation) {
                end = 9
                digits = 11
            }

            for (i in 0..end) {
                calcSum += cpf[i] * (digits - i)
            }

            Log.d("Soma:", "$calcSum")
            return calcSum
        }


        // Método que calcula o valor do dígito de validação do CPF:
        fun calcDigitValidation(soma: Int): Int {
            var calcDigit = (soma * 10) % 11
            if (calcDigit == 10) calcDigit = 0

            Log.d("Verify Dig:", "$calcDigit")
            return calcDigit
        }

        // Método que compara os números de verificação com os números de verificação que foram calculados:
        fun compareDigits(
            calculateFirstDigit: Int,
            firstDigitValidateNumber: Int,
            calculateSecondDigit: Int,
            secondDigitValidateNumber: Int
        ): Boolean {

            val statusCpf =
                if (calculateFirstDigit == firstDigitValidateNumber && calculateSecondDigit == secondDigitValidateNumber) {
                    Log.d("CPF:", "Válido!!")
                    true
                } else {
                    Log.d("CPF:", "Inválido!!")
                    false
                }

            return statusCpf
        }
    }
}