package coffee.synyx.bookmarks;

import coffee.synyx.UserNotFoundException;
import coffee.synyx.bookmarks.AccountRepository;
import coffee.synyx.bookmarks.Bookmark;
import coffee.synyx.bookmarks.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookmarks")
class BookmarkRestController {

    private final BookmarkRepository bookmarkRepository;

    private final AccountRepository accountRepository;

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(Principal principal, @RequestBody Bookmark input) {
        final String userId = principal.getName();

        this.validateUser(userId);

        return accountRepository.findByUsername(userId)
                .map(account -> {
                            Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

                            HttpHeaders httpHeaders = new HttpHeaders();

                            Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");
                            httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

                            return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
                        }
                ).get();
    }

    @RequestMapping(value = "/{bookmarkId}", method = RequestMethod.GET)
    BookmarkResource readBookmark(Principal principal, @PathVariable Long bookmarkId) {
        final String userId = principal.getName();

        this.validateUser(userId);
        return new BookmarkResource(this.bookmarkRepository.findOne(bookmarkId));
    }


    @RequestMapping(method = RequestMethod.GET)
    Resources<BookmarkResource> readBookmarks(Principal principal) {
        final String userId = principal.getName();

        this.validateUser(userId);

        List<BookmarkResource> bookmarkResourceList = bookmarkRepository.findByAccountUsername(userId)
                .stream()
                .map(BookmarkResource::new)
                .collect(Collectors.toList());
        return new Resources<BookmarkResource>(bookmarkResourceList);
    }

    @Autowired
    BookmarkRestController(BookmarkRepository bookmarkRepository,
                           AccountRepository accountRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.accountRepository = accountRepository;
    }

    private void validateUser(String userId) {
        this.accountRepository.findByUsername(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}