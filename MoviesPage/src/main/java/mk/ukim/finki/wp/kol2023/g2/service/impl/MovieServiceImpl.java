package mk.ukim.finki.wp.kol2023.g2.service.impl;

import mk.ukim.finki.wp.kol2023.g2.model.Director;
import mk.ukim.finki.wp.kol2023.g2.model.Genre;
import mk.ukim.finki.wp.kol2023.g2.model.Movie;
import mk.ukim.finki.wp.kol2023.g2.model.exceptions.InvalidDirectorIdException;
import mk.ukim.finki.wp.kol2023.g2.model.exceptions.InvalidMovieIdException;
import mk.ukim.finki.wp.kol2023.g2.repository.DirectorRepository;
import mk.ukim.finki.wp.kol2023.g2.repository.MovieRepository;
import mk.ukim.finki.wp.kol2023.g2.service.MovieService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final DirectorRepository directorRepository;
    private final MovieRepository movieRepository;

    public MovieServiceImpl(DirectorRepository directorRepository, MovieRepository movieRepository) {
        this.directorRepository = directorRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> listAllMovies() {
        return this.movieRepository.findAll();
    }

    @Override
    public Movie findById(Long id) {
        return this.movieRepository.findById(id)
                .orElseThrow(InvalidMovieIdException::new);
    }

    @Transactional
    @Override
    public Movie create(String name, String description, Double rating, Genre genre, Long director) {
        Director director1=null;
        if(director!=null){
           director1 =this.directorRepository.findById(director).orElseThrow(InvalidDirectorIdException::new);
        }
        return this.movieRepository.save(new Movie(
                name,
                description,
                rating,
                genre,
                director1
        ));
    }

    @Transactional
    @Override
    public Movie update(Long id, String name, String description, Double rating, Genre genre, Long director) {
        Movie movie=this.findById(id);
        movie.setName(name);
        movie.setDescription(description);
        movie.setRating(rating);
        movie.setGenre(genre);
        Director director1=null;
        if(director!=null){
            director1 =this.directorRepository.findById(director).orElseThrow(InvalidDirectorIdException::new);
        }
        movie.setDirector(director1);

        return this.movieRepository.save(movie);
    }

    @Override
    public Movie delete(Long id) {
        Movie movie=this.findById(id);
        this.movieRepository.delete(movie);
        return movie;
    }

    @Override
    public Movie vote(Long id) {
        Movie movie=this.movieRepository.findById(id).orElseThrow(InvalidMovieIdException::new);
        Integer votes=movie.getVotes();
        movie.setVotes(votes+1);

        return this.movieRepository.save(movie);
    }

    @Override
    public List<Movie> listMoviesWithRatingLessThenAndGenre(Double rating, Genre genre) {
        if(rating!=null && genre !=null){
            return this.movieRepository.findAllByRatingBeforeAndGenreLike(rating,genre);
        } else if (rating != null) {
            return this.movieRepository.findAllByRatingBefore(rating);
        } else if (genre != null) {
            return this.movieRepository.findAllByGenre(genre);
        }else return this.movieRepository.findAll();
    }
}
