package br.com.fatec.muttley.Muttley.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<CPFValido, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) return true; // @NotBlank cuida disso

        String numeros = cpf.replaceAll("\\D", "");

        if (numeros.length() != 11) return false;

        // Rejeita sequências repetidas (111.111.111-11, 000.000.000-00, etc.)
        if (numeros.matches("(\\d)\\1{10}")) return false;

        // Valida primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(numeros.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) primeiroDigito = 0;
        if (primeiroDigito != Character.getNumericValue(numeros.charAt(9))) return false;

        // Valida segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(numeros.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) segundoDigito = 0;
        return segundoDigito == Character.getNumericValue(numeros.charAt(10));
    }
}