package com.LoginAndForget.LFF.registration;

import org.hibernate.annotations.NaturalId;

public record RegistrationRequest(
        String firstName,
        String email,
        String lastName,
        String password,
        String role) {
}
