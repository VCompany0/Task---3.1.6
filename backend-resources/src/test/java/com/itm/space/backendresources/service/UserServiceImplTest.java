package com.itm.space.backendresources.service;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.itm.space.backendresources.BaseIntegrationTest.testId;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private Keycloak keycloakClient;

    @Mock
    private UserMapper userMapper;

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

    @Value("${keycloak.realm}")
    private String realm;

    @Test
    void createUser() throws Exception{
        final UserRequest testRequest = new UserRequest("test", "test@mail.ru", "12345", "first", "last");

        Mockito.when(keycloakClient.realm(realm)).thenReturn(realmResource);
        Mockito.when(realmResource.users()).thenReturn(usersResource);
        Mockito.when(usersResource.create(ArgumentMatchers.any())).thenReturn(Response.created(new URI(testId)).build());
        userService.createUser(testRequest);
        Mockito.verify(usersResource, Mockito.times(1)).create(ArgumentMatchers.any());

    }

    @Test
    void getUserById() {
        final UserResponse testResponse = new UserResponse("test", "test", "test@mail.ru", List.of("test"), List.of("test"));

        Mockito.when(keycloakClient.realm(realm)).thenReturn(realmResource);
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

        Mockito.when(userMapper
                .userRepresentationToUserResponse(userRepresentation
                        , List.of(roleRepresentation)
                        , List.of(groupRepresentation)))
                .thenReturn(testResponse);

        UserResponse userResponse = userService.getUserById(UUID.fromString(testId));

        assertThat(userResponse.getFirstName()).isEqualTo("test");
        assertThat(userResponse.getLastName()).isEqualTo("test");
        assertThat(userResponse.getEmail()).isEqualTo("test@mail.ru");
        assertThat(userResponse.getRoles()).isEqualTo(List.of("test"));
        assertThat(userResponse.getGroups()).isEqualTo(List.of("test"));
    }

}