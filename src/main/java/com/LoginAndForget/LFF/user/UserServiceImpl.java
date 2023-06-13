package com.LoginAndForget.LFF.user;

import com.LoginAndForget.LFF.exception.UserAlreadyExistsException;
import com.LoginAndForget.LFF.registration.RegistrationRequest;
import com.LoginAndForget.LFF.registration.token.VerificationToken;
import com.LoginAndForget.LFF.registration.token.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;


    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest request) {
        Optional<User> user = this.findByEmail(request.email());
        if(user.isPresent()){
            throw new UserAlreadyExistsException("User is already with mail "+request.email());
        }
        var newUser = new User();
        newUser.setFirstName(request.firstName());
        newUser.setLastName(request.lastName());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());
        return userRepository.save(newUser);
    }

    @Override
    public boolean isPasswordStrong(String password) {
        if (!isStrongPassword(password)) {
            return false;
        }
        return true;
    }

    private boolean isStrongPassword(String password) {
        // Check if the password meets the requirements for a strong password
        // Minimum 8 characters, alphanumeric characters, special characters, and numbers.
        String pattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(pattern);
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(User theUser, String token) {
        var verificationToken = new VerificationToken(token, theUser);
        tokenRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String theToken) {
        VerificationToken byToken = tokenRepository.findByToken(theToken);
        if(byToken == null){
            return "Invalid Verification Message";
        }

        User user = byToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if((byToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            tokenRepository.delete(byToken);
            return "Token is already expire";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "Valid";
    }


}
