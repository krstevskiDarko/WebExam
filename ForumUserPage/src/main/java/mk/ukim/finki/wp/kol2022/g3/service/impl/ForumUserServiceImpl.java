package mk.ukim.finki.wp.kol2022.g3.service.impl;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.ForumUserType;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidForumUserIdException;
import mk.ukim.finki.wp.kol2022.g3.repository.ForumUserRepository;
import mk.ukim.finki.wp.kol2022.g3.repository.InterestRepository;
import mk.ukim.finki.wp.kol2022.g3.service.ForumUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForumUserServiceImpl implements ForumUserService{

    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final ForumUserRepository forumUserRepository;
    private final InterestRepository interestRepository;
    private final InterestServiceImpl interestService;


    public ForumUserServiceImpl(PasswordEncoder passwordEncoder, ForumUserRepository forumUserRepository, InterestRepository interestRepository, InterestServiceImpl interestService) {
        this.passwordEncoder = passwordEncoder;
        this.forumUserRepository = forumUserRepository;
        this.interestRepository = interestRepository;
        this.interestService = interestService;
    }

    @Override
    public List<ForumUser> listAll() {
       return this.forumUserRepository.findAll();
    }

    @Override
    public ForumUser findById(Long id) {
        return this.forumUserRepository.findById(id)
                .orElseThrow(InvalidForumUserIdException::new);
    }

    @Override
    public ForumUser create(String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {

        List<Interest> interests=null;
        if(interestId!=null){
            interests=interestId.stream().map(interestService::findById).collect(Collectors.toList());
        }

        return this.forumUserRepository.save(new ForumUser(
                name,
                email,
                passwordEncoder.encode(password),
                type,
                interests,
                birthday
                )
        );
    }

    @Override
    public ForumUser update(Long id, String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        ForumUser user=this.findById(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setType(type);

        List<Interest> interests=null;
        if(interestId!=null){
            interests=interestId.stream().map(interestService::findById).collect(Collectors.toList());
        }

        user.setInterests(interests);
        user.setBirthday(birthday);

        return this.forumUserRepository.save(user);

    }

    @Override
    public ForumUser delete(Long id) {
        ForumUser user=findById(id);

        this.forumUserRepository.delete(user);

        return user;
    }

    @Override
    public List<ForumUser> filter(Long interestId, Integer age) {
        if(interestId!=null && age!=null){
            return this.forumUserRepository.findAllByInterestsContainingAndBirthdayBefore(this.interestService.findById(interestId),LocalDate.now().minusYears(age));
        } else if (interestId!=null) {
            return this.forumUserRepository.findAllByInterestsContaining(this.interestService.findById(interestId));
        } else if (age!=null) {
            return this.forumUserRepository.findAllByBirthdayBefore(LocalDate.now().minusYears(age));
        }else return this.forumUserRepository.findAll();
    }


}
