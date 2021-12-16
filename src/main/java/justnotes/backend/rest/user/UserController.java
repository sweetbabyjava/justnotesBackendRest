package justnotes.backend.rest.user;

import com.google.firebase.auth.FirebaseAuthException;
import justnotes.backend.rest.authentication.AuthenticationService;
import justnotes.backend.rest.note.NoteRepository;
import justnotes.backend.rest.note.NoteService;
import justnotes.backend.rest.translation.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("*")
@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    NoteService noteService;
    @Autowired
    NoteRepository repository;
    @Autowired
    AuthenticationService authService;
    @Autowired
    TranslationService translationService;

    @GetMapping(value = "/users", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUsers(@RequestHeader("Authorization") String token) {

        StringBuilder response= new StringBuilder("Read Users ");
        String userRequestType="read";

        return getStringResponseEntity(token,response, userRequestType);
    }
    private String verifyUser(String token,boolean check) throws FirebaseAuthException {
        return authService.verifyFirebaseUser(token, check);
    }

    private ResponseEntity<?> getStringResponseEntity(String token, StringBuilder response, String userRequestType) {
        List<User> users= null;
        try {
            String uid = verifyUser(token, true);
            if(userRequestType.equals("read")) {
                users = userService.getUsers();
            }
        } catch (FirebaseAuthException e) {
            response.append(" failed: Token is Invalid");}
        finally{
            return new ResponseEntity<>(users, HttpStatus.OK);
        }



    }
}
