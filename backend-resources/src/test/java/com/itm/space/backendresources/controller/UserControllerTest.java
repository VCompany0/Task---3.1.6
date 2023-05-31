package com.itm.space.backendresources.controller;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@WithMockUser(authorities = "ROLE_MODERATOR", value = "23276ffd-aacb-4ea7-92e8-3f83bfae58e3")
class UserControllerTest extends BaseIntegrationTest {

    @MockBean
    private Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;
    @Mock
    private UserResource userResource;

    @Mock
    private RoleMappingResource roleMappingResource;

    @Mock
    private MappingsRepresentation mappingsRepresentation;

    @Test
    void create() throws Exception {
        final UserRequest testRequest = new UserRequest("test", "test@mail.ru", "test", "test", "test");

        RequestBuilder requestBuilder = requestWithContent(MockMvcRequestBuilders.post("/api/users"), testRequest);
        Mockito.when(keycloak.realm(realm)).thenReturn(realmResource);
        Mockito.when(realmResource.users()).thenReturn(usersResource);
        Mockito.when(usersResource.create(ArgumentMatchers.any())).thenReturn(Response.created(new URI(testId)).build());
        mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getUserById() throws Exception {
        Mockito.when(keycloak.realm(realm)).thenReturn(realmResource);
        Mockito.when(realmResource.users()).thenReturn(usersResource);
        Mockito.when(usersResource.get(testId)).thenReturn(userResource);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(testId);
        userRepresentation.setFirstName("test");
        userRepresentation.setLastName("test");
        userRepresentation.setEmail("test@mail.ru");

        Mockito.when(userResource.toRepresentation()).thenReturn(userRepresentation);
        Mockito.when(userResource.roles()).thenReturn(roleMappingResource);
        Mockito.when(roleMappingResource.getAll()).thenReturn(mappingsRepresentation);

        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("test");

        Mockito.when(mappingsRepresentation.getRealmMappings()).thenReturn(List.of(roleRepresentation));

        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName("test");

        Mockito.when(userResource.groups()).thenReturn(List.of(groupRepresentation));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + testId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@mail.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles.[0]").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.groups.[0]").value("test"));
    }

    @Test
    void hello() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/hello"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(testId));
    }
}