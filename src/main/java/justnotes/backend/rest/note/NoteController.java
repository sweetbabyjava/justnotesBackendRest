package justnotes.backend.rest.note;

import com.google.firebase.auth.FirebaseAuthException;
import justnotes.backend.rest.authentication.AuthenticationService;
import justnotes.backend.rest.translation.TranslationService;
import justnotes.backend.rest.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
public class NoteController {
    @Autowired
    UserService userService;
    @Autowired
    NoteService noteService;
    @Autowired
    NoteRepository repository;
    @Autowired
    AuthenticationService authService;
    @Autowired
    TranslationService  translationService;

    @GetMapping(value = "/notes", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getNotes(@RequestHeader("Authorization") String token) {

        StringBuilder response= new StringBuilder("Read UserNotes ");
        String noteRequestType="read", shareRequestType= null, shareUid= null, language = null;

        return getStringResponseEntity(null, token, shareUid, response, noteRequestType, shareRequestType,language);
    }
    @GetMapping(value = "/notes/shared", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getSharedNotes(@RequestHeader("Authorization") String token) {

        StringBuilder response= new StringBuilder("Read SharedUserNotes ");
        String noteRequestType="readShared", shareRequestType= null, shareUid= null, language = null;

        return getStringResponseEntity(null, token, shareUid, response, noteRequestType, shareRequestType,language);
    }
    @PostMapping(path = "/notes/create", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> newNote(@RequestBody Note note,
                                          @RequestHeader("Authorization") String token) {

        StringBuilder response= new StringBuilder("Create ");
        String noteRequestType="create", shareRequestType= null, shareUid= null, language= null;

        return getStringResponseEntity(note, token, shareUid, response, noteRequestType, shareRequestType,language);
    }
    @PutMapping(path = "/notes/update", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateNote(@RequestBody Note note,
                                             @RequestHeader("Authorization") String token){

        StringBuilder response= new StringBuilder("Update ");
        String noteRequestType="update", shareRequestType=null, shareUid=null, language=null;

        return getStringResponseEntity(note, token, shareUid, response, noteRequestType, shareRequestType,language);
    }
    @PutMapping(path = "/notes/share/{shareUid}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> shareNote(@RequestBody Note note,
                                            @RequestHeader("Authorization") String token,
                                            @PathVariable String shareUid){

        StringBuilder response= new StringBuilder("Sharing ");
        String noteRequestType="share", shareRequestType="share", language=null;

        return getStringResponseEntity(note, token, shareUid, response, noteRequestType, shareRequestType,language);
    }
    @PutMapping(path = "/notes/withdraw", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> withdrawNote(@RequestBody Note note,
                                          @RequestHeader("Authorization") String token){

        StringBuilder response= new StringBuilder("Withdrawing note ");
        String noteRequestType="share", shareRequestType="withdraw", language=null, shareUid=null;

        return getStringResponseEntity(note, token, shareUid, response, noteRequestType, shareRequestType,language);
    }
    @PutMapping(path = "/notes/withdraw/from/{shareUid}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> withdrawNote(@RequestBody Note note,
                                             @RequestHeader("Authorization") String token,
                                             @PathVariable String shareUid){

        StringBuilder response= new StringBuilder("Withdrawing ");
        String noteRequestType="share", shareRequestType="withdraw", language=null;

        return getStringResponseEntity(note, token, shareUid, response, noteRequestType, shareRequestType,language);
    }
    @DeleteMapping(path = "/notes/delete", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> deleteNote(@RequestBody Note note,
                                        @RequestHeader(value="Authorization",required = false) String token) {
        StringBuilder response= new StringBuilder("Deleting ");
        String noteRequestType="delete", shareRequestType=null, shareUid=null, language=null;

        return getStringResponseEntity(note, token, shareUid, response, noteRequestType, shareRequestType,language);
    }
    /**
     Language has to be in unicode like "ru" for russian and "en" for english
     */
    @PutMapping("/notes/translate/{language}")
    public ResponseEntity<?> translateNote(@PathVariable String language,
                              @RequestBody Note note,
                              @RequestHeader("Authorization") String token){
        StringBuilder response= new StringBuilder("Update ");
        String noteRequestType="update", shareRequestType=null, shareUid=null;

        return getStringResponseEntity(note, token, shareUid, response, noteRequestType, shareRequestType, language);

    }
    private String verifyUser(String token,boolean check) throws FirebaseAuthException {
        return authService.verifyFirebaseUser(token, check);
    }
    private ResponseEntity<?> getStringResponseEntity(@RequestBody Note note,
                                                      @RequestHeader("Authorization") String token,
                                                      @RequestParam("uid") String shareUid,
                                                      StringBuilder response,
                                                      String noteRequestType,
                                                      String shareRequestType,
                                                      String language
                                                      ){
        List<Note> notes = null;
        Note updatedNote = null;
        //Initialize project id for translation and title for debugging purposes
        String projectId = "seindercloud", title= (note != null ? note.getTitle() : "");
        try {
            String uid = verifyUser(token, true);
            if(noteRequestType.equals("read")){
                notes = noteService.readNotes(uid);
            }else if(noteRequestType.equals("readShared")){
                notes = userService.getSharedNotes(uid);
            }
            else if(noteRequestType.equals("update") || noteRequestType.equals("delete") || noteRequestType.equals("create")){
                if (language != null && note != null && !language.isBlank()) {
                    note.setText(translationService.translateText(projectId, language, note.getText()));
                }
                updatedNote = noteService.changeNote(note, uid, noteRequestType);
                if(shareRequestType != null && shareRequestType.equals("share")) {
                    userService.shareRequest(updatedNote, uid, shareUid, shareRequestType);
                }
                }
            else if (noteRequestType.equals("share") && note != null && shareRequestType != null && !shareRequestType.isBlank()) {
                userService.shareRequest(note,uid, shareUid, shareRequestType);
            }
            response.append(title).append(" successful");
        }
        catch(FirebaseAuthException e)      {
            response.append(title).append(" failed: Token is Invalid");}
        catch(IllegalAccessException e)     {
            response.append(title).append(" failed: Illegal Access");}
        catch(IllegalArgumentException e)   {
            response.append(title).append(" failed: ").append(e.getMessage());}
        catch (Exception e)                 {
            response.append(title).append(" failed: Unknown Exception"+e.getMessage());
            e.printStackTrace();
        }
        //TODO: Finally block ??ndern
        finally {
            System.out.println(response.toString());
            if(notes != null){
                return new ResponseEntity<>(notes, HttpStatus.OK);
            }
            if(updatedNote != null){
                return new ResponseEntity<>(updatedNote, HttpStatus.OK);
            }
            if(response.toString().contains(" successful")){
                return new ResponseEntity<>(response.toString(), HttpStatus.OK);
            }
            return new ResponseEntity<>(response.toString(), HttpStatus.BAD_REQUEST);
        }
    }
}
