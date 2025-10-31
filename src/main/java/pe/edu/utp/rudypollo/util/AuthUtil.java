/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.edu.utp.rudypollo.util;

import java.security.MessageDigest;

/**
 *
 * @author Vinz
 */
public class AuthUtil {
     /**
     * Genera un hash SHA-256 en hexadecimal para una contraseña.
     */
    public static String hashPassword(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generando SHA-256", e);
        }
    }

    /**
     * Verifica una contraseña ingresada contra el valor almacenado.
     * 
     * - Si stored = plain, también acepta (para compatibilidad temporal).
     * - Si stored = hash SHA-256, calcula hash de entered y compara.
     */
    public static boolean verifyPassword(String entered, String stored) {
        if (entered == null || stored == null) return false;
        if (entered.equals(stored)) return true; // coincidencia plain (caso legacy)
        String hashed = hashPassword(entered);
        return hashed.equalsIgnoreCase(stored);
    }
}
