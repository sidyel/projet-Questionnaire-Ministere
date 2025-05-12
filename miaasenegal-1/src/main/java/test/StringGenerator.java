package test;

import java.security.SecureRandom;

public class StringGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String genererChaine(int longueur) {
        if (longueur <= 0) {
            throw new IllegalArgumentException("La longueur doit être supérieure à 0");
        }

        StringBuilder sb = new StringBuilder(longueur);
        for (int i = 0; i < longueur; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(genererChaine(5)); // Exemple : "aB3kLmN9Xy"
    }
}
