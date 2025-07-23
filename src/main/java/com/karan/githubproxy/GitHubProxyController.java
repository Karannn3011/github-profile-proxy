package com.karan.githubproxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class GitHubProxyController {

    private final WebClient webClient;
    private final String apiToken;

    /**
     * The recommended way to initialize components with values from configuration.
     * Spring injects the builder and the property values directly into the constructor arguments.
     */
    public GitHubProxyController(
            WebClient.Builder webClientBuilder,
            @Value("${github.api.url}") String apiUrl,
            @Value("${github.api.token}") String apiToken
    ) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.apiToken = apiToken;
    }

    /**
     * This method creates a proxy endpoint to fetch a GitHub user's profile.
     * It allows requests from a React development server running on localhost:3000.
     *
     * @param username The GitHub username to search for.
     * @return A Mono containing the JSON response from the GitHub API as a String.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/users/{username}")
    public Mono<String> getUserProfile(@PathVariable String username) {
        System.out.println("Proxying request for user profile: " + username);

        return this.webClient.get()
                .uri("/users/" + username) // The path for the specific GitHub API endpoint
                .header("Authorization", "Bearer " + apiToken) // Securely adds the token here
                .header("Accept", "application/vnd.github.v3+json")
                .retrieve() // Executes the request
                .bodyToMono(String.class); // Converts the response body to a String
    }

    /**
     * NEW: This method creates a proxy endpoint to fetch a user's repositories.
     *
     * @param username The GitHub username whose repositories to fetch.
     * @return A Mono containing the JSON list of repositories as a String.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/users/{username}/repos")
    public Mono<String> getUserRepos(@PathVariable String username) {
        System.out.println("Proxying request for user repos: " + username);

        return this.webClient.get()
                .uri("/users/" + username + "/repos") // The path for the repositories endpoint
                .header("Authorization", "Bearer " + apiToken)
                .header("Accept", "application/vnd.github.v3+json")
                .retrieve()
                .bodyToMono(String.class);
    }
}