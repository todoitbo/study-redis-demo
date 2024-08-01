package fun.bo.controller;

import fun.bo.service.SignInService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignInController {

    private final SignInService signInService;

    public SignInController(SignInService signInService) {
        this.signInService = signInService;
    }

    // ... 保留之前的signIn和count方法 ...

    @GetMapping("/signin/count/today")
    public String countSignInToday() {
        long count = signInService.countSignInToday();
        return "Total sign-ins today: " + count;
    }

    @GetMapping("/signin/count/week")
    public String countSignInThisWeek() {
        long count = signInService.countSignInThisWeek();
        return "Total sign-ins this week: " + count;
    }

    @GetMapping("/signin/count/month")
    public String countUserSignInThisMonth(@RequestParam long userId) {
        long count = signInService.countUserSignInThisMonth(userId);
        return "Total sign-ins this month for user " + userId + ": " + count;
    }
    @PostMapping("/signin")
    public String signIn(@RequestParam("userId") Long userId) {
        // 调用签到服务来处理签到
        signInService.signIn(userId);
        return "User " + userId + " signed in successfully.";
    }
}
