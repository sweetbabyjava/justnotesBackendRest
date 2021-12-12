package justnotes.backend.rest.authentication;

import com.google.firebase.auth.FirebaseAuthException;
import justnotes.backend.rest.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

@Component
public class AuthenticationService {
    @Autowired
    UserService userService;
    public String verifyFirebaseUser(String bearerToken, boolean check) throws FirebaseAuthException {
        String token = getBearerToken(bearerToken);
        FirebaseToken verifiedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        if(check)
            userService.persistUser(verifiedToken.getUid(),verifiedToken.getName(),verifiedToken.getEmail());
        return verifiedToken.getUid();
    }
    private String getBearerToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
            bearerToken = bearerToken.substring(7);
        return bearerToken;
    }
}
