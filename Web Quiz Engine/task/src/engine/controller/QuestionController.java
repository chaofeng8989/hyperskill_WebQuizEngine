package engine.controller;

import engine.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class QuestionController {
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CompleteQuestionRepository completeQuestionRepository;

    public QuestionController() {
    }


    public String currentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("USERNAME");
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            System.out.println(currentUserName);

            return currentUserName;
        }
        return null;
    }
    @GetMapping(path = "/api/quizzes/test")
    public ResponseEntity testAddQuiz() {
        Question q = new Question();
        q.setId(100);
        q.setAnswer(Stream.of(0, 1).collect(Collectors.toSet()));
        q.setTitle("Coffee drinks");
        q.setText("Select only coffee drinks.");
        q.setOptions(Stream.of("Americano","Tea","Cappuccino","Sprite").collect(Collectors.toList()));
        questionRepository.save(q);
        return ResponseEntity.ok(q);
    }

    @DeleteMapping(path = "/api/quizzes/{id}")
    public ResponseEntity deleteQuestion(@PathVariable int id) {
        System.out.println("Delete question ID:" + id);
        if (!questionRepository.existsById(id)) return ResponseEntity.notFound().build();

        Question q = questionRepository.getOne(id);
        User owner = userRepository.getUserByUsername(currentUserName());
        if (owner.getQuestionList().contains(q)){

            questionRepository.deleteById(id);
            return ResponseEntity.noContent().build();//("Question "+ id +" deleted");
        } else {
            return ResponseEntity.status(403).build();
        }
    }
    @GetMapping(path = "/api/quizzes/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable int id) {
        Question q = questionRepository.findById(id).orElse(null);
        if (q == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.status(HttpStatus.OK).body(Question.getCopy(q));
    }



    @GetMapping(path = "/api/quizzes")
    public Page<Question> getAllQuestions(@RequestParam int page) {
        Pageable paging = PageRequest.of(page, 10,Sort.by("id"));

        Page<Question> pagedResult = questionRepository.findAll(paging);
        for (Question q : pagedResult) q.setAnswer(null);
        return pagedResult;
    }

    @GetMapping(path = "/api/quizzes/completed")
    public Page<CompleteQuestion> getCompleted(@RequestParam int page) {
        User user = userRepository.getUserByUsername(currentUserName());



        Pageable paging = PageRequest.of(page, 10,Sort.by("completedAt").descending());

        Page<CompleteQuestion> pagedResult = completeQuestionRepository.findAllByUserid(user.getId(), paging);

        for (CompleteQuestion q : pagedResult) {

        }
        System.out.println(pagedResult);

        return pagedResult;
    }


    @PostMapping(path = "/api/quizzes", consumes = "application/json")
    public ResponseEntity addQuestion(@Valid @RequestBody Question q) {
        if (q.getAnswer() == null) q.setAnswer(new HashSet<>());
        User user = userRepository.getUserByUsername(currentUserName());

        questionRepository.save(q);
        List<Question> list = user.getQuestionList();
        list.add(q);
        user.setQuestionList(list);

        System.out.println(q);
        Question cloneQuestion =  Question.getCopy(q);
        System.out.println(cloneQuestion);
        userRepository.save(user);

        return ResponseEntity.ok(cloneQuestion);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationExceptions() {
    }

    @PostMapping(path = "/api/quizzes/{id}/solve")
    public ResponseEntity checkAnswer(@PathVariable int id, @RequestBody Map<String, Set<Integer>> answer) {
        Question q = questionRepository.findById(id).orElse(null);
        if (q == null) return ResponseEntity.notFound().build();

        Set<Integer> correctAnswer = q.getAnswer();
        if (answer.get("answer").equals(correctAnswer)) {
            User user = userRepository.getUserByUsername(currentUserName());
            CompleteQuestion completeQuestion = new CompleteQuestion();

            completeQuestion.setUserid(user.getId());
            Date date = new Date();
            completeQuestion.setCompletedAt(new Timestamp(date.getTime()));
            completeQuestion.setId(q.getId());
            completeQuestionRepository.save(completeQuestion);
            System.out.println(completeQuestion);
            return ResponseEntity.status(HttpStatus.OK).body(Result.builder()
                    .success(true)
                    .feedback("Congratulations, you're right!")
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(Result.builder()
                    .success(false)
                    .feedback("Wrong answer! Please, try again.")
                    .build());
        }
    }



    }
