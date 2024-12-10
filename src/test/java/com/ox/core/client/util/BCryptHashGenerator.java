package com.ox.core.client.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("Pippero666!");
        System.out.println("Hash for Pippero666!: " + hash);
    }
}
