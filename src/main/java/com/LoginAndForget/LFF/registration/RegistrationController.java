package com.LoginAndForget.LFF.registration;

import com.LoginAndForget.LFF.event.RegistrationCompleteEvent;
import com.LoginAndForget.LFF.registration.token.VerificationToken;
import com.LoginAndForget.LFF.registration.token.VerificationTokenRepository;
import com.LoginAndForget.LFF.user.User;
import com.LoginAndForget.LFF.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository tokenRepository;

    @PostMapping()
    public String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request) {
        boolean b = userService.isPasswordStrong(registrationRequest.password());
        if (b) {
            User user = userService.registerUser(registrationRequest);
            publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
            return "Success! Please check your mail for further formalities";
        }
        return "Password is not strong enough! Please use at least one UpperCase letter, one special character, and minimum 8 length of password";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token){
        VerificationToken theToken = tokenRepository.findByToken(token);
        if(theToken.getUser().isEnabled()){
            return "This account has already verified, please Login";
        }
        String validateToken = userService.validateToken(token);
        if(validateToken.equalsIgnoreCase("Valid")){
            return "Email verified successfully. Now you can login to your account";
        }
        return "Invalid verification token";
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }
}
