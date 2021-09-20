package tr.com.beb.game.web.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tr.com.beb.boardgame.configuration.SecurityConfiguration;
import tr.com.beb.boardgame.domain.application.UserService;
import tr.com.beb.boardgame.domain.model.user.UsernameExistsException;
import tr.com.beb.boardgame.utils.JsonUtils;
import tr.com.beb.boardgame.web.api.RegistrationController;
import tr.com.beb.boardgame.web.payload.RegistrationPayload;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { SecurityConfiguration.class, RegistrationController.class })
@WebMvcTest
public class RegistrationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  ////////////////////////
  /// METHOD: register ///
  ////////////////////////

  @Test
  public void register_emptyPayload_shouldFail() throws Exception {
    mockMvc.perform(post("/api/registrations")).andExpect(status().isBadRequest());
  }

  @Test
  public void register_existingUsername_shouldFail() throws Exception {

    RegistrationPayload registrationPayload = new RegistrationPayload();
    registrationPayload.setUsername("username");
    registrationPayload.setPassword("password");
    registrationPayload.setName("name");
    registrationPayload.setSurname("surname");

    doThrow(UsernameExistsException.class).when(userService).register(registrationPayload.getUsername(),
        registrationPayload.getPassword(), registrationPayload.getName(), registrationPayload.getSurname());

    mockMvc.perform(post("/api/registrations").contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtils.toJson(registrationPayload))).andExpect(status().isBadRequest());

  }

  @Test
  public void register_validPayload_shouldSucceed() throws Exception {
    RegistrationPayload registrationPayload = new RegistrationPayload();
    registrationPayload.setUsername("username");
    registrationPayload.setPassword("password");
    registrationPayload.setName("name");
    registrationPayload.setSurname("surname");

    doNothing().when(userService).register(registrationPayload.getUsername(), registrationPayload.getPassword(),
        registrationPayload.getName(), registrationPayload.getSurname());

    mockMvc.perform(post("/api/registrations").contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtils.toJson(registrationPayload))).andExpect(status().isOk());
  }

}
