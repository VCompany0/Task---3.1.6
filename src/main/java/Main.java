import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class Main {
    public static RestTemplate restTemplate = new RestTemplate();
    public  static String url = "http://94.198.50.185:7081/api/users";
    public static String code = "";
    public static void main(String[] args) {
        ResponseEntity<String> response1 = request1();
        String sessionId = response1.getHeaders().getFirst("Set-Cookie").split(";")[0];
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie",  sessionId);
        try {
            ResponseEntity<String> response2 = request2(headers);
            ResponseEntity<String> response3 = request3(headers);
            ResponseEntity<String> response4 = request4(headers);
            code = response2.getBody() + response3.getBody() + response4.getBody();
            System.out.println(code + ", " + code.length() + " characters");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public static ResponseEntity<String> request1(){
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response1 = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
        return response1;
    }

    public static ResponseEntity<String> request2(HttpHeaders headers) throws JsonProcessingException {
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        User updatedUser = new User();
        updatedUser.setId(3L);
        updatedUser.setName("James");
        updatedUser.setLastName("Brown");
        updatedUser.setAge((byte) 30);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(updatedUser);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        return responseEntity;
    }

    public static ResponseEntity<String> request3(HttpHeaders headers) throws JsonProcessingException {
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

// Set the request body with the updated user data
        User updatedUser = new User();
        updatedUser.setId(3L);
        updatedUser.setName("Thomas");
        updatedUser.setLastName("Shelby");
        updatedUser.setAge((byte) 35);

// Convert the user object to JSON format
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(updatedUser);

// Send a PUT request to update the user
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );
        return responseEntity;
    }

    public static ResponseEntity<String> request4(HttpHeaders headers){
        Long userId = 3L;

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url + "/{id}",
                HttpMethod.DELETE,
                requestEntity,
                String.class,
                userId
        );
        return responseEntity;
    }
}
