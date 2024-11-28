package testJUnit;

import model.DataStore;
import model.User;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserFlow {

    private DataStore dataStore;

    @BeforeAll
    void setup() {
        dataStore = DataStore.getInstance();
        System.out.println("DataStore instance initialized.");
    }

    @BeforeEach
    void clearDataStore() {
        dataStore.getUserList().clear();
        dataStore.getInvitations().clear();
        System.out.println("DataStore cleared before test.");
    }

    @Test
    void testInvitationCodeGenerationAndRegistration() {
        System.out.println("Starting test for invitation code generation and registration.");
        
        // Arrange
        String role1 = "Student";
        String role2 = "Instructor";
        List<String> roles = List.of(role1, role2);
        System.out.println("Roles defined for invitation: " + roles);

        // Act
        String invitationCode = generateInvitationCode(roles);
        System.out.println("Generated invitation code: " + invitationCode);

        // Assert - Validate invitation code
        assertNotNull(invitationCode, "Invitation code should not be null.");
        System.out.println("Validated: Invitation code is not null.");
        
        assertTrue(invitationCode.length() == 4, "Invitation code should be 4 characters long.");
        System.out.println("Validated: Invitation code length is correct.");

        assertTrue(dataStore.getInvitations().containsKey(invitationCode), "Invitation code should be stored in DataStore.");
        System.out.println("Validated: Invitation code is stored in DataStore.");

        assertEquals(roles, dataStore.getInvitations().get(invitationCode).getRoles(), "Roles associated with the invitation code should match.");
        System.out.println("Validated: Invitation code roles match expected roles.");

        // Simulate user registration with the invitation code
        String username = "testUser";
        String password = "securePassword";
        System.out.println("Simulating user registration with username: " + username);
        registerUserWithInvitationCode(invitationCode, username, password);

        // Assert - Validate user registration
        User registeredUser = dataStore.findUserByUsername(username);
        assertNotNull(registeredUser, "User should be registered successfully.");
        System.out.println("Validated: User is registered successfully.");

        assertEquals(username, registeredUser.getUsername(), "Registered username should match.");
        System.out.println("Validated: Registered username matches.");

        assertEquals(password, registeredUser.getPassword(), "Registered password should match.");
        System.out.println("Validated: Registered password matches.");

        assertEquals(roles, registeredUser.getRoles(), "Registered user roles should match the invitation roles.");
        System.out.println("Validated: Registered user roles match invitation roles.");

        // Assert - Ensure invitation code is removed after registration
        assertFalse(dataStore.getInvitations().containsKey(invitationCode), "Invitation code should be removed after registration.");
        System.out.println("Validated: Invitation code is removed after registration.");
    }

    private String generateInvitationCode(List<String> roles) {
        System.out.println("Generating invitation code for roles: " + roles);
        String code = "abcd"; // Simulating a random code generation
        dataStore.getInvitations().put(code, new User.Invitation(roles));
        System.out.println("Invitation code generated and stored: " + code);
        return code;
    }

    private void registerUserWithInvitationCode(String invitationCode, String username, String password) {
        System.out.println("Registering user with invitation code: " + invitationCode);
        User.Invitation invitation = dataStore.getInvitations().get(invitationCode);
        if (invitation != null) {
            User newUser = new User(username, password, invitation.getRoles());
            dataStore.getUserList().add(newUser);
            dataStore.getInvitations().remove(invitationCode);
            System.out.println("User registered successfully and invitation code removed.");
        } else {
            System.out.println("Error: Invalid invitation code.");
        }
    }
}

