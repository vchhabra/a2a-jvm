package io.github.vchhabra.a2a.jvm.core.model;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.vchhabra.a2a.jvm.core.model.auth.AuthScheme;
import io.github.vchhabra.a2a.jvm.core.model.enums.ApiType;
import io.github.vchhabra.a2a.jvm.core.model.enums.AuthType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AgentCard}.
 */
class AgentCardTest {

    private ObjectMapper objectMapper;

    static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of(
                        ApiType.JSON_RPC_HTTP,
                        new TestOauth2AuthScheme(
                                "test-client",
                                URI.create("https://auth.com/auth"),
                                URI.create("https://auth.com/token"),
                                Collections.singletonList("read"))),
                Arguments.of(ApiType.JSON_RPC_HTTP, new TestBearerScheme()));
    }

    @BeforeEach
    void setUp() {
        // Mimic the production scenario.
        this.objectMapper =
                new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        // Register test-specific subtypes.
        this.objectMapper.registerSubtypes(TestOauth2AuthScheme.class, TestBearerScheme.class);

        // Apply the mix-in to add @JsonTypeInfo to AuthScheme without modifying the interface.
        this.objectMapper.addMixIn(AuthScheme.class, AuthSchemeMixIn.class);
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void agentCardShouldSerializeAndDeserializeCorrectly(
            final ApiType apiType, final AuthScheme authScheme) throws Exception {
        // Given - a sample AgentCard object.
        final Api api = new Api(apiType, URI.create("https://agent.com/tasks"));
        final Action action =
                new Action(
                        "testAction",
                        "A test action",
                        Collections.emptyMap(),
                        Collections.emptyMap());
        final AgentCard originalCard =
                new AgentCard(
                        "agent-123", "0.1.0", Collections.singletonList(action), authScheme, api);

        // When - serialized to a JSON string.
        final String jsonString = this.objectMapper.writeValueAsString(originalCard);

        // Then - assert that the JSON contains the correct snake_case keys.
        // The 'id' field name is already compliant with snake_case, so no change is expected.
        assertThat(jsonString).contains("\"id\"");
        assertThat(jsonString).contains("\"input_schema\"");
        // And assert that our polymorphic type property is present
        assertThat(jsonString).contains("\"type\"");

        // And When - deserialized (the JSON string) back into an object.
        AgentCard deserializedCard = this.objectMapper.readValue(jsonString, AgentCard.class);

        // Then - assert that the deserialized object is equal to the original
        assertThat(deserializedCard).isEqualTo(originalCard);
    }

    // Define a mix-in interface to apply annotations to AuthScheme programmatically.
    // This keeps the core AuthScheme interface clean of Jackson dependencies.
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.CLASS,
            include = JsonTypeInfo.As.PROPERTY, // Include type info as a property in JSON
            property = "type") // The JSON property name for the type info
    private interface AuthSchemeMixIn {
    }

    private record TestOauth2AuthScheme(
            String clientId, URI authorizationUrl, URI tokenUrl, List<String> scopes)
            implements AuthScheme {

        @Override
        public AuthType type() {
            return AuthType.OAUTH2;
        }
    }

    private record TestBearerScheme() implements AuthScheme {

        @Override
        public AuthType type() {
            return AuthType.BEARER;
        }
    }
}
