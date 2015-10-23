package api_gateway.services.similar_movie;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import rx.Observable;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.ObservableResult;

import edu.cmu.ini.ericsson.practicum.models.similarMovieService.SimilarMovie;
import rx.Observable;

@Service
public class SimilarMovieIntegrationService {
	@Autowired
    RestTemplate restTemplate;

	@HystrixCommand(fallbackMethod = "stubSimilarMovie")
    public Observable<SimilarMovie> getSimilarMovie(final String mID) {
        return new ObservableResult<SimilarMovie>() {
            @Override
            public SimilarMovie invoke() {
                return restTemplate.getForObject("http://similar-movies/similar-movie/{mID}", SimilarMovie.class, mID);
            }
        };
    }
	
	 @HystrixCommand(fallbackMethod = "stubSimilarMovieList")
	    public Observable<SimilarMovieList> getSimilarMovieList(final String n) {
	        return new ObservableResult<SimilarMovieList>() {
	            @Override
	            public SimilarMovieList invoke() {
	                return restTemplate.getForObject("http://similar-movies/similar-movie/latest/{n}", SimilarMovieList.class, n);
	            }
	        };
	    }

	private SimilarMovie stubSimilarMovie(final String mID) {
		SimilarMovie stub = new SimilarMovie();
		stub.setId(mID);
        stub.setName("fallback movie");
        stub.setFirstChoice("first choice");
        stub.setSecondChoice("second choice");
        stub.setThirdChoice("third choice");
        return stub;
    }
	
	private SimilarMovieList stubSimilarMovieList(final String mID) {
		List<SimilarMovie> list = new ArrayList<SimilarMovie>();
		for (int i = 1; i <= Integer.parseInt(mID); i++) {
			SimilarMovie stub = new SimilarMovie();
			stub.setId(String.valueOf(i));
			stub.setName("fallback movie");
			stub.setFirstChoice("Lord of the Rings");
			stub.setSecondChoice("The Matrix");
			stub.setThirdChoice("Interstellar");
			list.add(stub);
		}
        return new SimilarMovieList(list);
    }
}
