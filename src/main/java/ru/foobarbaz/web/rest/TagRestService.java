package ru.foobarbaz.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.foobarbaz.entity.TagStatistic;
import ru.foobarbaz.repo.ChallengeRepository;

import java.util.List;

@RestController
@RequestMapping("api/tags")
public class TagRestService {
    private ChallengeRepository repository;

    @Autowired
    public TagRestService(ChallengeRepository repository) {
        this.repository = repository;
    }

    @RequestMapping
    public List<TagStatistic> searchTags(@RequestParam(required = false) String name){
        return name == null ? repository.findAllTags() : repository.searchTags(name);
    }
}
