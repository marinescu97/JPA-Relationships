package com.spring.JpaRelationships;

import com.spring.JpaRelationships.dto.CourseDto;
import com.spring.JpaRelationships.mapper.CourseMapper;
import com.spring.JpaRelationships.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class DataInitializer implements CommandLineRunner {
    private final CourseRepository courseRepository;
    private final CourseMapper mapper;

    @Override
    public void run(String... args) {
        if (courseRepository.count() == 0){
            courseRepository.saveAll(Arrays.asList(
                    mapper.toEntity(new CourseDto("JV", "Java")),
                    mapper.toEntity(new CourseDto("SB", "Spring Boot")),
                    mapper.toEntity(new CourseDto("JS", "Java Script"))
            ));
        }
    }
}
