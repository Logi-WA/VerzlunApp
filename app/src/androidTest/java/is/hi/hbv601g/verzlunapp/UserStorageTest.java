package is.hi.hbv601g.verzlunapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.persistence.UserStorage;

public class UserStorageTest {
    private UserStorage userStorage;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        userStorage = new UserStorage(context);
    }

    @Test
    public void testSaveAndRetrieveUser() {
        User testUser = new User("Charlie Davis", "charlie@example.com", "testPass123");
        userStorage.saveUser(testUser);

        List<User> users = userStorage.getUsers();
        assertNotNull(users);
        assertFalse(users.isEmpty());

        User retrievedUser = users.get(users.size() - 1);
        assertEquals("Charlie Davis", retrievedUser.getName());
        assertEquals("charlie@example.com", retrievedUser.getEmail());
    }

    @Test
    public void testJsonParsing() throws Exception {
        String jsonString = "[{\"name\":\"Alice Johnson\",\"email\":\"alice@example.com\",\"password\":\"password123\"}]";
        JSONArray jsonArray = new JSONArray(jsonString);
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        User parsedUser = User.fromJson(jsonObject);
        assertNotNull(parsedUser);
        assertEquals("Alice Johnson", parsedUser.getName());
        assertEquals("alice@example.com", parsedUser.getEmail());
    }
}
