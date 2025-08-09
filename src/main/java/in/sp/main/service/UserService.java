package in.sp.main.service;

import in.sp.main.entity.User;
import java.util.Optional;

public interface UserService {
    User register(User user);
    Optional<User> login(String email, String password);
}
