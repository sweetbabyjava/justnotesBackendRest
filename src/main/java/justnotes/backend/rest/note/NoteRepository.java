package justnotes.backend.rest.note;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface NoteRepository extends MongoRepository<Note, String> {
    ArrayList<Note> findAllByUid(String uid);
    Optional<Note> findById(String id);
}
