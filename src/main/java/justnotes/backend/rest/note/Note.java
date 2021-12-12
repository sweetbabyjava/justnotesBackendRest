package justnotes.backend.rest.note;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class Note {
    @Id
    public String id;
    private String uid;
    private Date creationDate;
    private String title;
    private String text;
    public Note() {
        this.creationDate = new Date(System.currentTimeMillis());
    }
}
