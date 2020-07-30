package engine.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface CompleteQuestionRepository extends PagingAndSortingRepository<CompleteQuestion, Integer> {
    Page<CompleteQuestion> findAllByUserid(int userid, Pageable pageable);


}
