package coffee.synyx;

import java.util.Arrays;
import java.util.Collection;

import coffee.synyx.bookmarks.Account;
import coffee.synyx.bookmarks.AccountRepository;
import coffee.synyx.bookmarks.Bookmark;
import coffee.synyx.bookmarks.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class SpringBootRestTutorialApplication {

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
