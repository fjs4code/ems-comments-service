package com.fjs.algacomments.comments_service.api.commons;

import com.fjs.algacomments.comments_service.domain.model.Comment;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentSpecifications {

    public static Specification<Comment> filter(CommentFilter filter){
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.text() != null && !filter.text().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("text")),
                                "%" + filter.text().toLowerCase() + "%"
                        )
                );
            }

            if (filter.author() != null && !filter.author().isBlank()) {
                predicates.add(
                        cb.equal(
                                cb.lower(root.get("author")),
                                filter.author().toLowerCase()
                        )
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

}
