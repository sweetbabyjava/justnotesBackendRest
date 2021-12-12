package justnotes.backend.rest.user;

import com.google.api.gax.rpc.InvalidArgumentException;
import justnotes.backend.rest.note.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class UserService {
    @Autowired
    UserRepository userRepo;

    public void persistUser(String uid, String displayName, String email){
            if (!userRepo.findById(uid).isPresent()) userRepo.save(new User(uid, displayName, email));
    }

    public void shareRequest(Note note, String uid, String request) throws IllegalAccessException {
        if(request != null && !request.isBlank()){
            switch(request){
                case "share": shareNotewithUser(note,uid); break;
                case "withdraw": withdrawNotefromUser(note,uid);
            }
        }
    }
    public void shareNotewithUser(Note note, String uid) throws IllegalAccessException {
        if(note.getUid().equals(uid)) {
            userRepo.findById(uid).ifPresent(
                    user -> user.getSharedWithMe().add(note));
        }
        else throw new IllegalAccessException();
    }
    public void withdrawNotefromUser(Note note, String uid) throws IllegalAccessException {
        if(note.getUid().equals(uid)) {
            userRepo.findById(uid).ifPresent(
                    user -> user.getSharedWithMe().remove(note));
        }
        else throw new IllegalAccessException();
    }
}
