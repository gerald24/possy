package net.g24.possy.daemon;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

	@GetMapping("/")
	public ResponseEntity<String> index() {
		return ResponseEntity.ok("Hi, I'm possy daemon");
	}
}
