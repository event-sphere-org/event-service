package com.eventsphere.event.service.client;

import com.eventsphere.event.exception.UserNotFoundException;
import com.eventsphere.event.model.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserClientService {

    private final WebClient webClient;

    public UserClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("lb://user-service/v1/user").build();
    }

    public UserDto findUser(Long id) {
        log.info("Find user by id: {} from user-service", id);

        Mono<UserDto> userDtoMono = webClient.get()
                .uri("/" + id)
                .retrieve()
                .bodyToMono(UserDto.class)
                .onErrorMap(error -> {
                    log.error(error.getMessage());
                    throw new UserNotFoundException(id);
                });

        return userDtoMono.block();
    }
}
