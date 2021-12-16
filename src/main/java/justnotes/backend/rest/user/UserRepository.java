package justnotes.backend.rest.user;


import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findById(@Param("id") String id);

    @Query("{'uid': ?0}")
    Optional<User> findUserByUid(String uid );

    List<User> findAll();

}
