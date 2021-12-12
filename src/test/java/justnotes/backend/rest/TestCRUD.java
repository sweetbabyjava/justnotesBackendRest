package justnotes.backend.rest;


import justnotes.backend.rest.note.Note;
import justnotes.backend.rest.note.NoteController;
import justnotes.backend.rest.note.NoteRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest()
public class TestCRUD {


        @Autowired
        private NoteRepository noteRepository;

        @Autowired
        private NoteController controller;

        @BeforeEach
        public void setUp (){

            noteRepository.deleteById("testID");
        }

        @Test
        public void testCreate() throws Exception {


            JSONObject test =getToken("test@test.test","testtest");
            String token = test.get("idToken").toString();



            Note note = new Note();
            note.setId("testID");
            note.setText("This is just a note for testing");
            note.setTitle("TestTitle");

            assertFalse(noteRepository.findById("testID").isPresent());
            controller.newNote(note,"Bearer "+token);
            assertTrue(noteRepository.findById("testID").isPresent());
            noteRepository.deleteById("testID");
            assertFalse(noteRepository.findById("testID").isPresent());




        }
        @Test
        public void testDelete() throws IOException, JSONException {


            JSONObject test = getToken("test@test.test","testtest");
            String token =test.get("idToken").toString();

            Note note = new Note();
            note.setId("testID");
            note.setText("This is just a note for testing");
            note.setTitle("TestTitle");
            note.setUid(test.get("localId").toString());



            assertFalse(noteRepository.findById("testID").isPresent());
            noteRepository.save(note);
            assertTrue(noteRepository.findById("testID").isPresent());

            controller.deleteNote(note,"Bearer "+token);

            assertFalse(noteRepository.findById("testID").isPresent());

        }

        @Test
        public void testShowAllNotesFromUser() throws IOException, JSONException {
            JSONObject test = getToken("test@test.test","testtest");
            String token =test.get("idToken").toString();



            Note note = new Note();
            note.setId("testID1");
            note.setText("This is just a note for testing");
            note.setTitle("TestTitle1");
            note.setUid(test.get("localId").toString());

            Note note1 = new Note();
            note1.setId("testID2");
            note1.setText("This is just a note for testing");
            note1.setTitle("TestTitle2");
            note1.setUid(test.get("localId").toString());

            JSONObject test1 = getToken("test@test.test2","testtest");
            String token1 =test1.get("idToken").toString();

            Note note2 = new Note();
            note2.setId("testID3");
            note2.setText("This is just a note for testing");
            note2.setTitle("TestTitle3");
            note2.setUid(test1.get("localId").toString());

            controller.newNote(note,"Bearer "+token);
            controller.newNote(note1,"Bearer "+token);
            controller.newNote(note2,"Bearer "+token1);

            ResponseEntity response = controller.getNotes("Bearer " +token);
            HashMap<String, Note> map = new HashMap();
            Object object = response.getBody();
            if(object instanceof ArrayList)
            {
                ArrayList list = (ArrayList) object;

                Object object1 = list.get(0);
                Object object2 = list.get(1);
                if(object1 instanceof Note && object2 instanceof Note)
                {
                    Note getNote1 = (Note) object1;
                    Note getNote2 = (Note) object2;


                    map.put(getNote1.getId(),getNote1);
                    map.put(getNote2.getId(),getNote2);

                }
                assertTrue(map.containsKey("testID1"));
                assertTrue(map.containsKey("testID2"));
                assertFalse(map.containsKey("testID3"));
            }

            controller.deleteNote(note,"Bearer "+token);
            controller.deleteNote(note1,"Bearer "+token);
            controller.deleteNote(note2,"Bearer "+token1);






        }


        @Test
         public void testUpdateNote() throws IOException, JSONException {
             JSONObject test = getToken("test@test.test","testtest");
             String token =test.get("idToken").toString();

             Note note = new Note();
             note.setId("testID1");
             note.setText("This is just a note for testing");
             note.setTitle("TestTitle1");
             note.setUid(test.get("localId").toString());

             controller.newNote(note, "Bearer " + token);
             if(noteRepository.findById("testID1").isPresent())
             {
                 assertTrue(noteRepository.findById("testID1").get().getTitle().equals("TestTitle1"));
             }
             note.setTitle("TestTitle2");
             controller.updateNote(note,"Bearer "+token);
             if(noteRepository.findById("testID1").isPresent())
             {
                 assertTrue(noteRepository.findById("testID1").get().getTitle().equals("TestTitle2"));
             }
             controller.deleteNote(note,"Bearer "+token);

         }

        private JSONObject getToken(String email, String password) throws JSONException, IOException {
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
