package justnotes.backend.rest;

import justnotes.backend.rest.note.Note;
import justnotes.backend.rest.note.NoteController;
import justnotes.backend.rest.note.NoteRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest()
public class TestTranslation {

        @Autowired
        private NoteRepository noteRepository;

        @Autowired
        private NoteController controller;

        @BeforeEach
        public void setUp () {

            noteRepository.deleteById("testID");
        }

    @Test
    public void testTranslation() throws IOException, JSONException {
        String projectId = "seindercloud";

        JSONObject test = getToken("test@test.test","testtest");
        String token =test.get("idToken").toString();

        Note note = new Note();
        note.setId("testID1");
        note.setText("Hello");
        note.setTitle("TestTitle1");
        note.setUid(test.get("localId").toString());

        controller.newNote(note, "Bearer " + token);

        //Note wird übersetzt und wieder gespeichert
        ResponseEntity<Note> response = (ResponseEntity<Note>) controller.translateNote("de", note, "Bearer " + token);
        Note translatedNote = response.getBody();
        assertTrue(translatedNote.getText().equals("Hallo"));
        controller.deleteNote(note,"Bearer "+token);

    }
    private JSONObject getToken(String email, String password) throws IOException, JSONException {
        URL url = new URL("https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyPassword?key=AIzaSyAk-lT7_22Og0pJGRhsfqrO3bPXQtnIlMg");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json; utf-8");
        conn.setDoOutput(true);

        JSONObject loginObject = new JSONObject();
        loginObject.put("email",email);
        loginObject.put("password",password);
        loginObject.put("returnSecureToken",true);

        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = loginObject.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }


        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
        sb.toString();

        return new JSONObject(sb.toString());
    }


}
