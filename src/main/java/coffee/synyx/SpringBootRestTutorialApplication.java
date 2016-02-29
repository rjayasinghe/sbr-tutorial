package coffee.synyx;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import coffee.synyx.bookmarks.Account;
import coffee.synyx.bookmarks.AccountRepository;
import coffee.synyx.bookmarks.Bookmark;
import coffee.synyx.bookmarks.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class SpringBootRestTutorialApplication {

	// CORS
	@Bean
    FilterRegistrationBean corsFilter(
        @Value("${tagit.origin:http://localhost:9000}") String origin) {
		return new FilterRegistrationBean(new Filter() {
			public void doFilter(ServletRequest req, ServletResponse res,
                                 FilterChain chain) throws IOException, ServletException {
				HttpServletRequest request = (HttpServletRequest) req;
				HttpServletResponse response = (HttpServletResponse) res;
				String method = request.getMethod();
				// this origin value could just as easily have come from a database
				response.setHeader("Access-Control-Allow-Origin", origin);
				response.setHeader("Access-Control-Allow-Methods",
						"POST,GET,OPTIONS,DELETE");
				response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
				response.setHeader("Access-Control-Allow-Credentials", "true");
				response.setHeader(
						"Access-Control-Allow-Headers",
						"Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
				if ("OPTIONS".equals(method)) {
					response.setStatus(HttpStatus.OK.value());
				}
				else {
					chain.doFilter(req, res);
				}
			}

			public void init(FilterConfig filterConfig) {
			}

			public void destroy() {
			}
		});
	}

	@Bean
	CommandLineRunner init(AccountRepository accountRepository, BookmarkRepository bookmarkRepository) {
		return (evt) ->
				Arrays.asList("jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","))
						.forEach(a -> {
							Account account = accountRepository.save(new Account(a, "password"));
							bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/1/" + a, "A description"));
							bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/2/" + a, "A description"));
						});
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestTutorialApplication.class, args);
	}
}
