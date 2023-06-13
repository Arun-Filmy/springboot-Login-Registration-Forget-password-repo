package com.LoginAndForget.LFF.registration;

import org.hibernate.annotations.NaturalId;

public record RegistrationRequest(
        String partnerId,
        String email,
        String password,
        String role) {
}
