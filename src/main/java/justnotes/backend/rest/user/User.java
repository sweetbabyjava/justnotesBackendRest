package justnotes.backend.rest.user;

import justnotes.backend.rest.note.Note;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String uid;
    private String displayName;
    private List<Note> sharedWithMe = new ArrayList<>();
    public User(String uid, String displayName, String email) {
    }
}
