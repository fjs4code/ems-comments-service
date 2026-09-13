package com.fjs.algacomments.comments_service.api.controller;

import com.fjs.algacomments.comments_service.api.commons.CommentFilter;
import com.fjs.algacomments.comments_service.api.model.CommentInput;
import com.fjs.algacomments.comments_service.api.model.CommentOutput;
import com.fjs.algacomments.comments_service.api.model.PageResponse;
import com.fjs.algacomments.comments_service.api.model.PageResponseMapper;
import com.fjs.algacomments.comments_service.api.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService service;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentOutput create(@RequestBody CommentInput input){
       return service.create(input);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentOutput findVyId(@PathVariable String id){
        return service.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<CommentOutput> search(CommentFilter filter, @PageableDefault Pageable pageable){
        Page<CommentOutput> search = service.search(filter, pageable);
        return PageResponseMapper.from(search);

    }

}
