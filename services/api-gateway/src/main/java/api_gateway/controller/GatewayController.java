package api_gateway.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import rx.Observable;
import rx.Observer;
import api_gateway.services.user.UserIntegrationService;

import api_gateway.services.movie.MovieIntegrationService;
import api_gateway.services.ratings.RatingIntegrationService;
import api_gateway.services.similar_movie.SimilarMovieIntegrationService;
import edu.cmu.ini.ericsson.practicum.models.apiGatewayService.MovieDetails;
import edu.cmu.ini.ericsson.practicum.models.apiGatewayService.MovieDetailsList;

import edu.cmu.ini.ericsson.practicum.models.userService.User;
import edu.cmu.ini.ericsson.practicum.models.movieService.Movie;
import edu.cmu.ini.ericsson.practicum.models.ratingsService.Rating;
import edu.cmu.ini.ericsson.practicum.models.similarMovieService.SimilarMovie;

import edu.cmu.ini.ericsson.practicum.models.movieService.MovieList;
import edu.cmu.ini.ericsson.practicum.models.ratingsService.RatingList;
import edu.cmu.ini.ericsson.practicum.models.similarMovieService.SimilarMovieList;

import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;

@RestController
@RequestMapping("/movie")
public class GatewayController {

	@Autowired
    MovieIntegrationService movieIntegrationService;

    @Autowired
    RatingIntegrationService ratingIntegrationService;

    @Autowired
    SimilarMovieIntegrationService similarMovieIntegrationService;
    
    @Autowired
    UserIntegrationService userIntegrationService;
    
    @Autowired
	private Tracer tracer;
    
//    @RequestMapping(value="{mID}", method=RequestMethod.GET)
//    public DeferredResult<MovieDetails> getMovieDetails(@PathVariable String mID) {
//    	String uuid = UUID.randomUUID().toString();
//        Observable<MovieDetails> details = Observable.zip(
//			movieIntegrationService.getMovie(mID, uuid),
//			ratingIntegrationService.ratingFor(mID, uuid),
//			similarMovieIntegrationService.getSimilarMovie(mID, uuid),
//            (movie, ratings, similars) -> {
//                MovieDetails movieDetails = new MovieDetails();
//                movieDetails.setMovie(movie);
//                movieDetails.setRatings(ratings);
//                movieDetails.setSimilars(similars);
//                return movieDetails;
//            }
//        );
//        return toDeferredResult(details);
//    }
    
    @RequestMapping(value="{mID}", method=RequestMethod.GET)
    public DeferredResult<MovieDetails> getMovieDetails(@PathVariable String mID) {
    	String uuid = UUID.randomUUID().toString();
    	
//    	Span span = this.tracer.createSpan("testSpan",
//				new AlwaysSampler());
    	
    	System.out.println("mID: " + mID);
    	System.out.println("uuid: " + uuid);
    	
    	this.tracer.addTag("source", "api-gateway");
    	
    	MovieDetails movieDetails = new MovieDetails();
    	
    	Movie movie = movieIntegrationService.getMovie(mID, uuid);
    	System.out.println("----------------");
    	System.out.println("movie: " + movie);
    	System.out.println("----------------");
    	List<Rating> ratings = ratingIntegrationService.ratingFor(mID, uuid);
    	SimilarMovie similars = similarMovieIntegrationService.getSimilarMovie(mID, uuid);
    	
    	movieDetails.setMovie(movie);
    	movieDetails.setRatings(ratings);
    	movieDetails.setSimilars(similars);
    	
        return toDeferredResult(movieDetails);
    }

    public DeferredResult<MovieDetails> toDeferredResult(MovieDetails details) {
        DeferredResult<MovieDetails> deferredResult = new DeferredResult<>();
        deferredResult.setResult(details);
        return deferredResult;
    }
    
    @RequestMapping(method = RequestMethod.POST)
	public Movie postMovie(@RequestBody Movie movie) {
		return movieIntegrationService.postMovie(movie);
	}
    
    @RequestMapping(value="/user/{mID}", method=RequestMethod.GET)
    public DeferredResult<User> getUser(@PathVariable String mID) {
        User user = userIntegrationService.getUser(mID);
        return toDeferredResultUser(user);
    }

    public DeferredResult<User> toDeferredResultUser(User user) {
    	DeferredResult<User> deferredResult = new DeferredResult<>();
        deferredResult.setResult(user);
        return deferredResult;
    }
    
//    @RequestMapping(value="/latest/{n}", method=RequestMethod.GET)
//    public DeferredResult<MovieDetailsList> getMovieListDetails(@PathVariable String n) { 	
//    	Observable<MovieDetailsList> details = Observable.zip(
//    			movieIntegrationService.getMovieList(n),
//    			ratingIntegrationService.getRatingList(n),
//    			similarMovieIntegrationService.getSimilarMovieList(n),
//                (movie, ratings, similars) -> {
//                	List<MovieDetails> list = new ArrayList<MovieDetails>();
//                	for(int i = 0; i < Integer.parseInt(n); i++) {
//                		 MovieDetails movieDetails = new MovieDetails();
//                         movieDetails.setMovie(movie.getMovie().get(i));
//                         movieDetails.setRatings(ratings.getList().get(i));
//                         movieDetails.setSimilars(similars.getSimilarMovieList().get(i));
//                         list.add(movieDetails);
//                	}
//                   
//                    return new MovieDetailsList(list);
//                }
//            );
//        return toDeferredListResult(details);
//    }
    
    @RequestMapping(value="/latest/{n}", method=RequestMethod.GET)
    public DeferredResult<MovieDetailsList> getMovieListDetails(@PathVariable String n) { 	
   	
        MovieList movie = movieIntegrationService.getMovieList(n);
        RatingList ratings = ratingIntegrationService.getRatingList(n);
        SimilarMovieList similars = similarMovieIntegrationService.getSimilarMovieList(n);
        
        List<MovieDetails> list = new ArrayList<MovieDetails>();
    	for (int i = 0; i < Integer.parseInt(n); i++) {
    		 MovieDetails movieDetails = new MovieDetails();
    		 
             movieDetails.setMovie(movie.getMovie().get(i));
             movieDetails.setRatings(ratings.getList().get(i));
             movieDetails.setSimilars(similars.getSimilarMovieList().get(i));
             
             list.add(movieDetails);
    	}
    	
    	MovieDetailsList details = new MovieDetailsList(list);
    	
    	return toDeferredListResult(details);
    }

	public DeferredResult<MovieDetailsList> toDeferredListResult(MovieDetailsList details) {
//		DeferredResult<MovieDetailsList> result = new DeferredResult<>();
//		details.subscribe(new Observer<MovieDetailsList>() {
//			@Override
//			public void onCompleted() {
//			}
//
//			@Override
//			public void onError(Throwable throwable) {
//			}
//
//			@Override
//			public void onNext(MovieDetailsList movieDetails) {
//				result.setResult(movieDetails);
//			}
//		});
//		return result;
		
		DeferredResult<MovieDetailsList> deferredResult = new DeferredResult<>();
        deferredResult.setResult(details);
        return deferredResult;
	}
}
