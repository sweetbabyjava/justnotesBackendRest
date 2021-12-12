package justnotes.backend.rest.note;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component()
public class NoteService {
    @Autowired
    NoteRepository noteRepository;
    
    public Note changeNote(Note note, String uid, String request) throws IllegalAccessException, IllegalArgumentException {
        if(request != null && request.equals("create"))
        {
            note.setUid(uid);
            return noteRepository.save(note);
        }
        Optional<Note> preUpdate = noteRepository.findById(note.getId());
        if(preUpdate.isPresent()) {
            if (isAllowed(note,uid)) {
                switch(request) {
                    case "update": {
                        if (!preUpdate.get().getText().equals(note.getText())) {
                            preUpdate.get().setText(note.getText());
                        }
                        if (!preUpdate.get().getTitle().equals(note.getTitle())) {
                            preUpdate.get().setTitle(note.getTitle());
                        }
                        noteRepository.save(preUpdate.get());
                        return preUpdate.get();
                    }
                    case "delete": {
                        noteRepository.delete(preUpdate.get());
                    };
                    default: return preUpdate.get();
                }
            } else throw new IllegalAccessException();
        }
        else throw new IllegalArgumentException();
    }
    public List<Note> readNotes(String uid){
        return noteRepository.findAllByUid(uid);
    }
    private boolean isAllowed(Note note, String uid){
        return note.getUid().equals(uid);
    }
}
