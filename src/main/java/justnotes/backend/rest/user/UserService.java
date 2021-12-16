package justnotes.backend.rest.user;

import com.google.api.gax.rpc.InvalidArgumentException;
import justnotes.backend.rest.note.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class UserService {
    @Autowired
    UserRepository userRepo;

    public void persistUser(String uid, String displayName, String email){
 //                 List<User> del = userRepo.findAll();
//
//                List<User> tbr =new ArrayList<>();
//                if(del.size()>0) {
//                    for(User user : del){
//                        System.out.println("User : "+ user.getUid()+" gefunden!");
////                        if(user.getUid() == null){
////                            tbr.add(user);
////                        }
//                    }
 //              for(User user :del){
 //                     userRepo.delete(user);
  //                 }
//                }

            Optional<User> user = userRepo.findUserByUid(uid);

            if (user.isEmpty()) {
                User preSave = new User(uid, displayName, email);
                User usersaved = userRepo.save(preSave);
                System.out.println(usersaved.getDisplayName() + " wurde gespeicher!");
            }

    }
    public List<Note> getSharedNotes(String uid){
        if(uid != null && !uid.isBlank()){
            Optional<User> user = userRepo.findUserByUid(uid);
            if(user.isPresent()){
                return user.get().getSharedWithMe();
            }
            throw new IllegalArgumentException("User not found");
        }
        return null;
    }

    public void shareRequest(Note note,String uid, String shareUid, String request) throws IllegalAccessException {
        if(request != null && !request.isBlank()){
            switch(request){
                case "share": shareNotewithUser(note,uid, shareUid); System.out.println("Share Request incoming"); break;
                case "withdraw":
                    if (shareUid == null) {
                        withdrawNotefromMe(note, uid);
                    } else {
                        withdrawNotefromUser(note, uid, shareUid);
                    }
            }
        }
    }
    public void shareNotewithUser(Note note, String uid, String shareUid) throws IllegalAccessException {
        if(note.getUid().equals(uid)) {
            userRepo.findUserByUid(shareUid).ifPresent(
                    user -> {
                        user.getSharedWithMe().add(note);
                        userRepo.save(user);
                        System.out.println("user resaved");
                    });

        }
        else throw new IllegalAccessException();
    }
    public void withdrawNotefromMe(Note note, String uid) throws IllegalAccessException {

            userRepo.findUserByUid(uid).ifPresent(
                    user -> {
                        user.getSharedWithMe().remove(note);
                        userRepo.save(user);
                    }
                    );


    }
    public void withdrawNotefromUser(Note note, String uid, String shareUid) throws IllegalAccessException {
        if (note != null && note.getUid() != null && note.getUid().equals(uid)) {
            userRepo.findUserByUid(shareUid).ifPresent(
                    user -> {
                        user.getSharedWithMe().remove(note);
                        userRepo.save(user);
                    }
            );
        } else {
            throw new IllegalAccessException("Illegal access to note");
        }
    }
    public List<User> getUsers() {
        return userRepo.findAll();
    }
}
