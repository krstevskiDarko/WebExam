package mk.ukim.finki.wp.kol2022.g3.repository;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import mk.ukim.finki.wp.kol2022.g3.service.ForumUserService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ForumUserRepository extends JpaRepository<ForumUser,Long> {

    List<ForumUser> findAllByInterestsContainingAndBirthdayBefore(Interest i, LocalDate date);

    List<ForumUser> findAllByInterestsContaining(Interest i);

    List<ForumUser> findAllByBirthdayBefore(LocalDate b);

    ForumUser findByEmail(String string);


}
